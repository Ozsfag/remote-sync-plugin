package org.blacksoil.remotesync.backend.diff;

import java.util.List;

public record DiffResult(List<String> addedOrModified, List<String> deleted) {}
