package org.blacksoil.sshsync.web.api;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.blacksoil.shareddto.sshsync.DeleteRequest;
import org.blacksoil.shareddto.sshsync.TestRequest;
import org.blacksoil.shareddto.sshsync.UploadRequest;
import org.blacksoil.sshsync.app.service.SyncService;
import org.blacksoil.sshsync.web.sse.SseStreamExecutor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ssh")
@RequiredArgsConstructor
@Validated
@Slf4j
public class SshSyncController {

  private final SyncService syncService;
  private final SseStreamExecutor sseExec;

  @PostMapping("/test")
  public Map<String, Object> test(@RequestBody TestRequest req) throws Exception {
    syncService.testConnection(req.host(), req.username(), req.password(), req.remotePath());
    return Map.of("ok", true);
  }

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> upload(
      @RequestParam(name = "stream", defaultValue = "false") boolean stream,
      @RequestPart("uploadRequest") UploadRequest uploadRequest) {

    return sseExec.run(
        stream,
        "upload",
        progress ->
            syncService.uploadFiles(
                uploadRequest.files(),
                uploadRequest.localRoot(),
                uploadRequest.remotePath(),
                uploadRequest.host(),
                uploadRequest.username(),
                uploadRequest.password(),
                progress));
  }

  @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> delete(
      @RequestParam(name = "stream", defaultValue = "false") boolean stream,
      @RequestBody DeleteRequest deleteRequest) {
    return sseExec.run(
        stream,
        "delete",
        progress ->
            syncService.deleteFiles(
                deleteRequest.files(),
                deleteRequest.remotePath(),
                deleteRequest.host(),
                deleteRequest.username(),
                deleteRequest.password(),
                progress));
  }
}
