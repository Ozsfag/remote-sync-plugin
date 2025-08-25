package org.blacksoil.remotesync.core.model;

import java.util.List;

public record DiffResult(List<String> addedOrModified, List<String> deleted) {}
