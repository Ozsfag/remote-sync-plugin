package org.blacksoil.remotesync.page.welcome.provider;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.blacksoil.remotesync.page.welcome.WelcomeFileEditor;
import org.blacksoil.remotesync.page.welcome.WelcomeVirtualFile;
import org.jetbrains.annotations.NotNull;

public class WelcomeFileEditorProvider implements FileEditorProvider, DumbAware {

  public static final String EDITOR_TYPE_ID = "remote-sync-welcome";

  @Override
  public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
    return file instanceof WelcomeVirtualFile;
  }

  @Override
  public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
    return new WelcomeFileEditor(file);
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
