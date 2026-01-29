package com.merchantsledger.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.merchantsledger.dto.NotificationResponse;
import com.merchantsledger.entity.Notification;
import com.merchantsledger.entity.User;
import com.merchantsledger.exception.BadRequestException;
import com.merchantsledger.exception.NotFoundException;
import com.merchantsledger.repository.NotificationRepository;

@Service
public class NotificationService {
  private final NotificationRepository notificationRepository;
  private final SimpMessagingTemplate messagingTemplate;

  public NotificationService(NotificationRepository notificationRepository,
                             SimpMessagingTemplate messagingTemplate) {
    this.notificationRepository = notificationRepository;
    this.messagingTemplate = messagingTemplate;
  }

  public NotificationResponse notify(User actor, String title, String message, String severity) {
    Notification notification = new Notification();
    notification.setTitle(title);
    notification.setMessage(message);
    notification.setSeverity(severity);
    notification.setTenantKey(TenantResolver.resolveTenantKey(actor));

    Notification saved = notificationRepository.save(notification);
    NotificationResponse response = toResponse(saved);
    messagingTemplate.convertAndSend("/topic/alerts", response);
    return response;
  }

  public List<NotificationResponse> list(User actor) {
    String tenantKey = TenantResolver.resolveTenantKey(actor);
    return notificationRepository.findTop50ByTenantKeyOrderByCreatedAtDesc(tenantKey).stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  public void markRead(User actor, Long id) {
    Notification notification = notificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Notification not found"));
    if (!notification.getTenantKey().equals(TenantResolver.resolveTenantKey(actor))) {
      throw new BadRequestException("Forbidden");
    }
    notification.setReadAt(Instant.now());
    notificationRepository.save(notification);
  }

  private NotificationResponse toResponse(Notification notification) {
    return new NotificationResponse(
        notification.getId(),
        notification.getTitle(),
        notification.getMessage(),
        notification.getSeverity(),
        notification.getCreatedAt(),
        notification.getReadAt()
    );
  }
}
