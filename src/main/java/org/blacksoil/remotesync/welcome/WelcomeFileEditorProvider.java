package org.blacksoil.remotesync.welcome;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public final class WelcomeFileEditorProvider implements FileEditorProvider {
  public static final String EDITOR_TYPE_ID = "remote-sync-welcome";

  @Override
  public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
    return file instanceof WelcomeVirtualFile;
  }

  @Override
  public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
    return new WelcomeFileEditor(project);
  }

  @Override
  public @NotNull String getEditorTypeId() {
    return EDITOR_TYPE_ID;
  }

  @Override
  public @NotNull FileEditorPolicy getPolicy() {
    return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
  }
}
