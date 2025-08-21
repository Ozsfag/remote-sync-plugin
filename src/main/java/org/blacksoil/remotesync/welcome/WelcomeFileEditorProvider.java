package org.blacksoil.remotesync.welcome;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class WelcomeFileEditorProvider implements FileEditorProvider, DumbAware {

  public static final String WELCOME_EDITOR_ID = "WelcomeEditor";

  @Override
  public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
    return file.getName().equals("WELCOME.md"); // или любой другой критерий
  }

  @Override
  public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
    return new WelcomeFileEditor(project, file);
  }

  @Override
  public @NotNull String getEditorTypeId() {
    return WELCOME_EDITOR_ID;
  }

  @Override
  public @NotNull FileEditorPolicy getPolicy() {
    return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
  }
}
