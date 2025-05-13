package com.habilitator.orchestator.infrastructure.repository;

import com.habilitator.orchestator.domain.model.QueueStatus;

import java.util.List;

public interface QueueStatusRepository {
    List<QueueStatus> findAndMarkInProcess();
}
