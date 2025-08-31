package org.blacksoil.sshsync.app.web.jobs.repo;

import java.util.Collection;
import org.blacksoil.sshsync.app.web.jobs.model.Job;
import org.blacksoil.sshsync.app.web.jobs.model.JobState;

public interface JobRepository {
  Job create();

  Job get(String id);

  void setState(String id, JobState state);

  void setProgress(String id, int progress, String message);

  boolean cancel(String id);

  Collection<Job> findAll();

  int deleteExpired(long ttlMs); // возвращает, сколько удалили
}
