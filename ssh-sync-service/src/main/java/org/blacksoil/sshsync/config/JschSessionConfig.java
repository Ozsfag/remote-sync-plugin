package org.blacksoil.sshsync.config;

import com.jcraft.jsch.Session;
import org.blacksoil.sshsync.infra.session.JschSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JschSessionConfig {
    @Bean
    public Session session() {
        return JschSessionFactory.open()
    }
}
