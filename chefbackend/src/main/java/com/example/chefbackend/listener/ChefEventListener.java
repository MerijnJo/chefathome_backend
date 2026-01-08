package com.example.chefbackend.listener;

import com.example.chefbackend.dto.ChefLeaderboardDto;
import com.example.chefbackend.event.ChefViewedEvent;
import com.example.chefbackend.repository.ChefRepository;
import com.example.chefbackend.service.ChefService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ChefEventListener {
    private static final Logger log = LoggerFactory.getLogger(ChefEventListener.class);

    private final ChefRepository chefRepository;
    private final ChefService chefService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChefEventListener(ChefRepository chefRepository,
                             ChefService chefService,
                             SimpMessagingTemplate messagingTemplate) {
        this.chefRepository = chefRepository;
        this.chefService = chefService;
        this.messagingTemplate = messagingTemplate;
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

                    List<ChefLeaderboardDto> topChefs = chefService.getTopChefs();
                    messagingTemplate.convertAndSend("/topic/chef-leaderboard", topChefs);
                    log.info("Broadcasted updated leaderboard to WebSocket clients");
                });
    }
}
