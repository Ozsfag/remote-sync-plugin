package org.blacksoil.remotesync.error.report;

public interface IssueReporter {
  boolean submitIssue(String title, String body) throws Exception;
}
