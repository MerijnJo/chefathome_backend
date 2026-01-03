package com.example.chefbackend.listener;

import com.example.chefbackend.model.Chef;
import com.example.chefbackend.event.ChefViewedEvent;
import com.example.chefbackend.repository.ChefRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ChefEventListener {
    private static final Logger log = LoggerFactory.getLogger(ChefEventListener.class);

    private final ChefRepository chefRepository;

    public ChefEventListener(ChefRepository chefRepository) {
        this.chefRepository = chefRepository;
    }

    @Async
    @EventListener
    @Transactional
    public void handleChefViewed(ChefViewedEvent event) {
        log.info("Async: Incrementing view count for chef {}", event.getChefId());

        chefRepository.findById(event.getChefId())
                .ifPresent(chef -> {
                    chef.incrementViewCount();
                    chefRepository.save(chef);
                    log.info("View count updated to {} for chef {}",
                            chef.getViewCount(), chef.getId());
                });
    }
}
