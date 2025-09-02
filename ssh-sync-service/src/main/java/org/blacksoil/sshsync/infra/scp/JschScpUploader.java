package org.blacksoil.sshsync.infra.scp;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import java.io.*;
import java.nio.charset.StandardCharsets;
import org.blacksoil.sshsync.domain.util.ShellEscaper;
import org.blacksoil.sshsync.infra.config.SshSyncProperties;
import org.blacksoil.sshsync.infra.exec.SshFileOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JschScpUploader {
  private final Session session;
  @Autowired private SshFileOps fileOps;
  @Autowired private SshSyncProperties props;

  public JschScpUploader(Session session) {
    this.session = session;
  }

  private static void waitForExit(ChannelExec ch) throws InterruptedException {
    for (int i = 0; i < 100 && ch.getExitStatus() == -1; i++) Thread.sleep(10);
  }

  private static void checkAck(InputStream in) throws IOException {
    int b = in.read();
    if (b == 0) return;
    if (b == -1) throw new EOFException("SCP ack: EOF");
    if (b == 1 || b == 2) {
      StringBuilder sb = new StringBuilder();
      for (int c; (c = in.read()) != '\n' && c != -1; ) sb.append((char) c);
      throw new IOException("SCP error: " + sb);
    }
    throw new IOException("SCP unexpected ack: " + b);
  }

  public void upload(File localFile, String remoteDir, String remoteFileName) throws Exception {
    if (!localFile.exists()) throw new FileNotFoundException(localFile.getAbsolutePath());

    if (localFile.isFile()) {
      uploadSingleFile(localFile, remoteDir, remoteFileName);
    } else if (localFile.isDirectory()) {
      uploadDirectory(localFile, remoteDir, remoteFileName);
    } else {
      throw new IOException("Unsupported file type: " + localFile.getAbsolutePath());
    }
  }

  private void uploadSingleFile(File localFile, String remoteDir, String remoteFileName)
      throws Exception {
    String cmd = "scp -t " + ShellEscaper.quote(remoteDir);
    ChannelExec channel = (ChannelExec) session.openChannel("exec");
    channel.setCommand(cmd);

    try (OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();
        FileInputStream fis = new FileInputStream(localFile)) {

      channel.connect(props.getTimeoutMs());
      checkAck(in);

      String mode = localFile.canExecute() ? "0755" : "0644";
      String header = "C" + mode + " " + localFile.length() + " " + remoteFileName + "\n";
      out.write(header.getBytes(StandardCharsets.UTF_8));
      out.flush();
      checkAck(in);

      byte[] buf = new byte[props.getScpBufferSize()];
      for (int read; (read = fis.read(buf)) != -1; ) out.write(buf, 0, read);

      out.write(0);
      out.flush();
      checkAck(in);
    } finally {
      waitForExit(channel);
      channel.disconnect();
    }
  }

  private void uploadDirectory(File localFile, String remoteDir, String remoteFileName)
      throws Exception {
    String targetDir = remoteDir + "/" + remoteFileName;
    fileOps.mkDirs(session, targetDir);

    File[] children = localFile.listFiles();
    if (children == null) return;

    for (File child : children) {
      upload(child, targetDir, child.getName());
    }
  }
}
