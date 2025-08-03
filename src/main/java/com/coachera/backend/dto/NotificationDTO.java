package com.coachera.backend.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.coachera.backend.entity.Notification;
import com.coachera.backend.entity.enums.NotificationType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Notification Data Transfer Object")
public class NotificationDTO extends AuditableDTO {
    
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "15")
    private Long id;
    
    @Schema(example = "SYSTEM_ALERT", description = "Type of notification")
    private NotificationType type;
    
    @Schema(example = "New Message Received", description = "Notification title")
    private String title;
    
    @Schema(example = "You have a new message from John Doe", description = "Notification content")
    private String content;
    
    @Schema(example = "/messages/123", description = "URL for deep linking")
    private String actionUrl;
    
    @Schema(example = "{\"senderId\":\"123\",\"messageId\":\"456\"}", 
           description = "Additional metadata for the notification")
    private Map<String, String> metadata;
    
    @Schema(example = "false", description = "Whether the notification has been read")
    private boolean read;
    
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, 
           example = "2023-05-15T10:30:00", 
           description = "When the notification was sent")
    private LocalDateTime sentAt;
    
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, 
           example = "2023-05-15T10:35:00", 
           description = "When the notification was marked as read")
    private LocalDateTime readAt;
    
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, 
           example = "3", 
           description = "ID of the recipient user")
    private Integer recipientId;

    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.type = notification.getType();
        this.title = notification.getTitle();
        this.content = notification.getContent();
        this.actionUrl = notification.getActionUrl();
        this.metadata = notification.getMetadata();
        this.read = notification.isRead();
        this.sentAt = notification.getSentAt();
        this.readAt = notification.getReadAt();
        this.recipientId = notification.getRecipient() != null ? notification.getRecipient().getId() : null;
        this.setCreatedAt(notification.getCreatedAt());
        this.setUpdatedAt(notification.getUpdatedAt());
    }
}
