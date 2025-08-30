package org.blacksoil.gitdiff.web;

import lombok.RequiredArgsConstructor;
import org.blacksoil.gitdiff.app.service.GitDiffService;
import org.blacksoil.shareddto.GitDiffRequest;
import org.blacksoil.shareddto.GitDiffResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/diff")
@RequiredArgsConstructor
public class GitDiffController {
  private final GitDiffService gitDiffService;

  @PostMapping
  public GitDiffResponse getDiff(@RequestBody GitDiffRequest request) {
    return gitDiffService.getChangedFiles(request.projectDir(), request.branch());
  }
}
