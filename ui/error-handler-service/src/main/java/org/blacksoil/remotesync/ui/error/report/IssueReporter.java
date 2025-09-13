package org.blacksoil.remotesync.ui.error.report;

public interface IssueReporter {
  boolean submitIssue(String title, String body) throws Exception;
}
