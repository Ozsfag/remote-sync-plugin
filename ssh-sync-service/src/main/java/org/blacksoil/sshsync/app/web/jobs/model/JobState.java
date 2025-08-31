package org.blacksoil.sshsync.app.web.jobs.model;

public enum JobState {
  QUEUED,
  RUNNING,
  COMPLETED,
  FAILED,
  CANCELED
}
