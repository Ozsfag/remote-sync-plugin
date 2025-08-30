package org.blacksoil.shareddto;

import java.util.List;


public record GitDiffResponse(List<String> addedOrModified, List<String> deleted) {}
