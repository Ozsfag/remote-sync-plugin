package org.blacksoil.gitdiff.web;

import lombok.RequiredArgsConstructor;
import org.blacksoil.gitdiff.app.service.GitDiffService;
import org.blacksoil.shareddto.gitdiff.GitDiffRequest;
import org.blacksoil.shareddto.gitdiff.GitDiffResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GitDiffController {
  private final GitDiffService gitDiffService;

  @PostMapping("/diff")
  public GitDiffResponse getDiff(@RequestBody GitDiffRequest request) {
    return gitDiffService.getChangedFiles(request.projectDir(), request.branch());
  }
}
