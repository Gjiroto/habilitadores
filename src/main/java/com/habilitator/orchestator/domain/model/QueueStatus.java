package com.habilitator.orchestator.domain.model;

public class QueueStatus {
    private Long id;
    private String info;
    private int status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}
