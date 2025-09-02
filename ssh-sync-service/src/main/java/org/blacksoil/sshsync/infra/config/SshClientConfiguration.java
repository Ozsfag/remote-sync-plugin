package org.blacksoil.sshsync.infra.config;

import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import org.blacksoil.sshsync.app.client.DefaultSshClient;
import org.blacksoil.sshsync.infra.exec.SshFileOps;
import org.blacksoil.sshsync.infra.scp.JschScpUploader;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@RequiredArgsConstructor
public class SshClientConfiguration {

  private final ApplicationContext ctx;

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public DefaultSshClient defaultSshClient(Session session) {
    SshFileOps fileOps = ctx.getBean(SshFileOps.class);
    SshSyncProperties props = ctx.getBean(SshSyncProperties.class);
    return new DefaultSshClient(
        session, fileOps, (s) -> ctx.getBean(JschScpUploader.class, s, fileOps, props));
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public JschScpUploader jschScpUploader(
      Session session, SshFileOps fileOps, SshSyncProperties props) {
    return new JschScpUploader(session, fileOps, props);
  }
}
