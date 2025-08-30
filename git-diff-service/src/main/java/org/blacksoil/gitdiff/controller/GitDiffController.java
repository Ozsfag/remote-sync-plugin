package org.blacksoil.gitdiff.controller;

import org.blacksoil.dto.GitDiffRequest;
import org.blacksoil.dto.GitDiffResponse;
import org.blacksoil.gitdiff.service.GitDiffService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/diff")
public class GitDiffController {
    private final GitDiffService gitDiffService;

    public GitDiffController(GitDiffService gitDiffService) {
        this.gitDiffService = gitDiffService;
    }

    @PostMapping
    public GitDiffResponse getDiff(@RequestBody GitDiffRequest request) {
        return gitDiffService.getChangedFiles(request.projectDir(), request.branch());
    }
}
