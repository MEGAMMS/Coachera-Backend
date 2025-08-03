package com.coachera.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Request object for registering a web push subscription")
public class WebPushSubscriptionRequest {

    @NotBlank(message = "Subscription JSON is required")
    @Schema(required = true, example = "{\"endpoint\":\"https://fcm.googleapis.com/fcm/send/...\",\"keys\":{\"p256dh\":\"...\",\"auth\":\"...\"}}", description = "Web push subscription in JSON format")
    private String subscriptionJson;

    @Schema(example = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)...", description = "User agent string of the browser")
    private String userAgent;
}
