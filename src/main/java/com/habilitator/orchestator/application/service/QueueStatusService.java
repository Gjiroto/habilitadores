package com.habilitator.orchestator.application.service;

import com.habilitator.orchestator.domain.model.QueueStatus;
import com.habilitator.orchestator.infrastructure.repository.QueueStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueueStatusService {
    private static final Logger logger = LoggerFactory.getLogger(QueueStatusService.class);
    private final QueueStatusRepository repository;

    public QueueStatusService(QueueStatusRepository repository) {
        this.repository = repository;
    }

    public List<QueueStatus> processQueueStatuses() {
        try {
            List<QueueStatus> result = repository.findAndMarkInProcess();
            logger.info("Procesados {} registros", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Error procesando registros", e);
            throw e;
        }
    }
}
