package org.blacksoil.sshsync.app.client.config;

import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import org.blacksoil.sshsync.app.client.DefaultSshClient;
import org.blacksoil.sshsync.infra.exec.SshFileOps;
import org.blacksoil.sshsync.infra.scp.JschScpUploader;
import org.springframework.beans.factory.ObjectProvider;
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
    ObjectProvider<JschScpUploader> scpProv = ctx.getBeanProvider(JschScpUploader.class);
    return new DefaultSshClient(session, fileOps, scpProv);
  }
}
