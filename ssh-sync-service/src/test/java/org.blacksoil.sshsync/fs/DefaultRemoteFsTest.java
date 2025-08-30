package org.blacksoil.remotesync.infrastructure.ssh.fs;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.blacksoil.remotesync.infrastructure.ssh.exec.SshExec;
import org.blacksoil.remotesync.infrastructure.ssh.exec.SshExec.ExecResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class DefaultRemoteFsTest {

  private SshExec exec;
  private DefaultRemoteFs fs;

  @BeforeEach
  void setUp() {
    exec = mock(SshExec.class);
    fs = new DefaultRemoteFs(exec);
  }

  @Test
  void mkDirs_ok_builds_expected_command() throws Exception {
    when(exec.run(anyString(), anyInt())).thenReturn(new ExecResult(0, "", ""));

    fs.mkDirs("/home/u/dir");

    ArgumentCaptor<String> cmd = ArgumentCaptor.forClass(String.class);
    verify(exec).run(cmd.capture(), anyInt());
    assertTrue(cmd.getValue().startsWith("mkdir -p "));
    assertTrue(cmd.getValue().contains("\"/home/u/dir\"")); // в кавычках
  }

  @Test
  void mkdirs_nonzero_exit_throws() throws Exception {
    when(exec.run(anyString(), anyInt())).thenReturn(new ExecResult(1, "", "boom"));
    var ex = assertThrows(java.io.IOException.class, () -> fs.mkDirs("/x"));
    assertTrue(ex.getMessage().startsWith("mkdir failed with code 1"));
  }

  @Test
  void rm_ok_and_builds_command() throws Exception {
    when(exec.run(anyString(), anyInt())).thenReturn(new ExecResult(0, "", ""));

    fs.rm("/tmp/a.txt");

    ArgumentCaptor<String> cmd = ArgumentCaptor.forClass(String.class);
    verify(exec).run(cmd.capture(), anyInt());
    assertTrue(cmd.getValue().startsWith("rm -f "));
    assertTrue(cmd.getValue().contains("\"/tmp/a.txt\""));
  }

  @Test
  void dirExists_parses_stdout() throws Exception {
    when(exec.run(anyString(), anyInt())).thenReturn(new ExecResult(0, "exists\n", ""));
    assertTrue(fs.dirExists("/ok"));

    when(exec.run(anyString(), anyInt())).thenReturn(new ExecResult(0, "missing\n", ""));
    assertFalse(fs.dirExists("/ok"));
  }
}
