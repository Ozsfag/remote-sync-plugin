package org.blacksoil.sshsync.app.web.jobs.mapper;

import org.blacksoil.shareddto.sshsync.JobStatus;
import org.blacksoil.sshsync.app.web.jobs.model.Job;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobStatusMapper {
  @Mapping(target = "jobId", source = "id")
  @Mapping(target = "state", source = "state")
  @Mapping(target = "progress", source = "progress")
  @Mapping(target = "message", source = "message")
  JobStatus jobToJobStatus(Job job);
}
