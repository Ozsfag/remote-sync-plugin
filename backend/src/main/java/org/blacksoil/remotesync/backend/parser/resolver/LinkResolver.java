package org.blacksoil.remotesync.backend.parser.resolver;

public interface LinkResolver {
  String resolve(String numericId, String xmlId, String url);
}
