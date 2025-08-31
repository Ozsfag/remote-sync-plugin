package org.blacksoil.sshsync.app.web.api;

import java.io.FileOutputStream;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.blacksoil.shareddto.sshsync.DeleteRequest;
import org.blacksoil.shareddto.sshsync.JobResponse;
import org.blacksoil.shareddto.sshsync.JobStatus;
import org.blacksoil.shareddto.sshsync.TestRequest;
import org.blacksoil.shareddto.sshsync.UploadMeta;
import org.blacksoil.sshsync.app.service.SyncService;
import org.blacksoil.sshsync.app.web.jobs.JobService;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SshSyncController {

  private final SyncService syncService;
  private final JobService jobs;

  // --- test connection ---
  @PostMapping("/ssh/test")
  public Map<String, Object> test(@RequestBody TestRequest req) throws Exception {
    syncService.testConnection(req.host(), req.username(), req.password(), req.remotePath());
    return Map.of("ok", true);
  }

  // --- upload (async, multipart) ---
  @PostMapping(value = "/ssh/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public JobResponse upload(
      @RequestPart("meta") UploadMeta meta,
      @RequestPart(value = "archive", required = false) MultipartFile archive,
      @RequestPart(value = "files", required = false) List<MultipartFile> files) {

    String jobId = jobs.create();

    jobs.submit(
        jobId,
        sink -> {
          try {
            Path workDir = Files.createTempDirectory("ssh-sync-upload-");
            try {
              Path localRoot = prepareLocalRoot(workDir, meta, archive, files);
              int n = meta.relativePaths() == null ? 0 : meta.relativePaths().size();

              for (int i = 0; i < n; i++) {
                String rel = meta.relativePaths().get(i);
                int progress = (i * 100) / n;
                sink.update(progress, "Uploading: " + rel);
                try {
                  syncService.uploadFiles(
                      List.of(rel),
                      localRoot.toString(),
                      meta.remotePath(),
                      meta.host(),
                      meta.username(),
                      meta.password(),
                      msg -> sink.update(progress, msg));
                } catch (Exception e) {
                  throw new RuntimeException("Upload failed for " + rel + ": " + e.getMessage(), e);
                }
              }
              sink.update(100, "Done");
            } finally {
              try {
                deleteRecursively(workDir);
              } catch (Throwable ignored) {
              }
            }
          } catch (Throwable t) {
            throw new RuntimeException(t);
          }
        });

    return new JobResponse(jobId, "QUEUED");
  }

  // --- delete (async) ---
  @PostMapping("/ssh/delete")
  public JobResponse delete(@RequestBody DeleteRequest req) {
    String jobId = jobs.create();

    jobs.submit(
        jobId,
        sink -> {
          int n = req.relativePaths() == null ? 0 : req.relativePaths().size();
          for (int i = 0; i < n; i++) {
            String rel = req.relativePaths().get(i);
            int progress = (i * 100) / n;
            sink.update(progress, "Deleting: " + rel);
            try {
              syncService.deleteFiles(
                  List.of(rel),
                  req.remotePath(),
                  req.host(),
                  req.username(),
                  req.password(),
                  msg -> sink.update(progress, msg));
            } catch (Exception e) {
              throw new RuntimeException("Delete failed for " + rel + ": " + e.getMessage(), e);
            }
          }
          sink.update(100, "Done");
        });

    return new JobResponse(jobId, "QUEUED");
  }

  // --- job status & SSE ---
  @GetMapping("/jobs/{jobId}")
  public JobStatus status(@PathVariable String jobId) {
    return jobs.status(jobId); // готовый DTO из shared-dto
  }

  @GetMapping(path = "/jobs/{jobId}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter events(@PathVariable String jobId) {
    return jobs.subscribe(jobId); // SSE-подписка (с снапшотом внутри)
  }

  // --- helpers ---
  private static Path prepareLocalRoot(
      Path workDir, UploadMeta meta, MultipartFile archive, List<MultipartFile> files)
      throws Exception {
    Path localRoot = workDir.resolve("root");
    Files.createDirectories(localRoot);

    if (archive != null && !archive.isEmpty()) {
      try (ZipInputStream zis = new ZipInputStream(archive.getInputStream())) {
        java.util.zip.ZipEntry e;
        while ((e = zis.getNextEntry()) != null) {
          Path out = localRoot.resolve(e.getName()).normalize();
          if (!out.startsWith(localRoot))
            throw new IllegalArgumentException("Entry outside root: " + e.getName());
          if (e.isDirectory()) {
            Files.createDirectories(out);
          } else {
            Files.createDirectories(out.getParent());
            try (FileOutputStream fos = new FileOutputStream(out.toFile())) {
              StreamUtils.copy(zis, fos);
            }
          }
        }
      }
      return localRoot;
    }

    if (files != null && !files.isEmpty()) {
      for (MultipartFile f : files) {
        String rel = f.getOriginalFilename();
        if (rel == null || rel.isBlank()) continue;
        Path out = localRoot.resolve(rel).normalize();
        if (!out.startsWith(localRoot))
          throw new IllegalArgumentException("Filename outside root: " + rel);
        Files.createDirectories(out.getParent());
        try (var is = f.getInputStream()) {
          Files.copy(is, out, StandardCopyOption.REPLACE_EXISTING);
        }
      }
      return localRoot;
    }

    throw new IllegalArgumentException("Either 'archive' or 'files[]' must be provided");
  }

  private static void deleteRecursively(Path dir) throws Exception {
    if (dir == null) return;
    try (var walk = Files.walk(dir)) {
      walk.sorted(Comparator.reverseOrder())
          .forEach(
              p -> {
                try {
                  Files.deleteIfExists(p);
                } catch (Exception ignored) {
                }
              });
    }
  }
}
