package com.example.chefbackend.event;

import java.time.LocalDateTime;

public class ChefViewedEvent {
    private final Long chefId;
    private final LocalDateTime timestamp;

    public ChefViewedEvent(Long chefId) {
        this.chefId = chefId;
        this.timestamp = LocalDateTime.now();
    }

    public Long getChefId() {
        return chefId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
