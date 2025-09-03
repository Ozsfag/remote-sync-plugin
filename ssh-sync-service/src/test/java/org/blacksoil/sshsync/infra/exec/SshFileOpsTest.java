package org.blacksoil.sshsync.infra.exec;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.jcraft.jsch.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Тестирует построение команд и обработку кодов возврата в SshFileOps. Session здесь не
 * используется напрямую — мы проверяем только делегирование в SshExec.
 */
class SshFileOpsTest {

  private SshExec exec;
  private SshFileOps ops;
  private Session dummySession; // можно замокать, но он не нужен exec.run(...)

  @BeforeEach
  void setUp() {
    exec = mock(SshExec.class);
    ops = new SshFileOps(exec);
    dummySession = mock(Session.class);
  }

  @Test
  void mkDirs_ok_builds_expected_command() throws Exception {
    when(exec.run(eq(dummySession), anyString())).thenReturn(new SshExec.ExecResult(0, "", ""));

    ops.mkDirs(dummySession, "/home/u/dir");

    var cmd = ArgumentCaptor.forClass(String.class);
    verify(exec).run(eq(dummySession), cmd.capture());
    assertTrue(cmd.getValue().startsWith("mkdir -p "));
    assertTrue(cmd.getValue().contains("\"/home/u/dir\"")); // в кавычках
  }

  @Test
  void mkdirs_nonzero_exit_throws() throws Exception {
    when(exec.run(eq(dummySession), anyString())).thenReturn(new SshExec.ExecResult(1, "", "boom"));

    var ex = assertThrows(java.io.IOException.class, () -> ops.mkDirs(dummySession, "/x"));
    assertTrue(ex.getMessage().startsWith("mkdir failed"));
  }

  @Test
  void rm_ok_and_builds_command() throws Exception {
    when(exec.run(eq(dummySession), anyString())).thenReturn(new SshExec.ExecResult(0, "", ""));

    ops.rm(dummySession, "/tmp/a.txt");

    var cmd = ArgumentCaptor.forClass(String.class);
    verify(exec).run(eq(dummySession), cmd.capture());
    assertTrue(cmd.getValue().startsWith("rm -f "));
    assertTrue(cmd.getValue().contains("\"/tmp/a.txt\""));
  }

  @Test
  void dirExists_parses_stdout() throws Exception {
    when(exec.run(eq(dummySession), anyString()))
        .thenReturn(new SshExec.ExecResult(0, "exists\n", ""));
    assertTrue(ops.dirExists(dummySession, "/ok"));

    when(exec.run(eq(dummySession), anyString()))
        .thenReturn(new SshExec.ExecResult(0, "missing\n", ""));
    assertFalse(ops.dirExists(dummySession, "/ok"));
  }
}
