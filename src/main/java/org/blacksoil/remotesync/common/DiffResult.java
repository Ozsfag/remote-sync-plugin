package org.blacksoil.remotesync.common;

import java.util.List;

public record DiffResult(List<String> addedOrModified, List<String> deleted) {

}
