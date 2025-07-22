package com.coachera.backend.dto;

import com.coachera.backend.entity.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for sending notifications")
public class SendNotificationRequest {

    @NotNull(message = "User ID is required")
    @Schema(example = "123", description = "ID of the recipient user", required = true)
    private Long userId;

    @NotNull(message = "Notification type is required")
    @Schema(example = "SYSTEM_ALERT", 
           description = "Type of notification", 
           required = true,
           allowableValues = {"SYSTEM_ALERT", "PROMOTIONAL", "ORDER_UPDATE", "SECURITY", "SOCIAL"})
    private NotificationType type;

    @NotBlank(message = "Title is required")
    @Schema(example = "New Message Received", 
           description = "Title of the notification", 
           required = true,
           maxLength = 255)
    private String title;

    @NotBlank(message = "Content is required")
    @Schema(example = "You have a new message from John Doe", 
           description = "Content of the notification", 
           required = true)
    private String content;

    @Schema(example = "/messages/123", 
           description = "URL for deep linking when notification is clicked")
    private String actionUrl;

    @Schema(example = "{\"senderId\":\"456\",\"messageId\":\"789\"}", 
           description = "Additional metadata for the notification")
    private Map<String, String> metadata;

    @Builder.Default
    @Schema(example = "[\"mobile\", \"web\"]", 
           description = "Channels to send the notification through",
           allowableValues = {"mobile", "web", "email"},
           defaultValue = "[\"mobile\", \"web\"]")
    private List<String> channels = List.of("mobile", "web");

    @Schema(example = "fcm_token_abc123", 
           description = "Specific device token for mobile push (optional)")
    private String deviceToken;

    @Schema(description = "Web push subscription JSON (optional)")
    private String webPushSubscriptionJson;

    @Schema(example = "user@example.com", 
           description = "Email address for email notifications (optional)",
           format = "email")
    private String emailAddress;
}