package com.habilitator.orchestator.entrypoints.controller;

import com.habilitator.orchestator.application.service.QueueStatusService;
import com.habilitator.orchestator.domain.model.QueueStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QueueStatusController {
    private final QueueStatusService service;

    public QueueStatusController(QueueStatusService service) {
        this.service = service;
    }

    @GetMapping("/queue-status/process")
    public List<QueueStatus> processQueues() {
        return service.processQueueStatuses();
    }
}
