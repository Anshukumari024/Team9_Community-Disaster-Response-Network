package com.example.cdrn.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.cdrn.model.Incident;

@Service
public class NotificationService {
    private final SimpMessagingTemplate template;

    public NotificationService(SimpMessagingTemplate template) { this.template = template; }

    public void broadcastIncident(Incident incident) {
        template.convertAndSend("/topic/incidents", incident);
    }
}
