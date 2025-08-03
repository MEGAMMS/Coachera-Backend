package com.coachera.backend.service;

import java.security.Security;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;

@Service
@Slf4j
public class WebPushService {

    @Value("${webpush.public-key}")
    private String publicKey;

    @Value("${webpush.private-key}")
    private String privateKey;

    @Value("${webpush.subject:mailto:admin@coachera.com}")
    private String subject;

    private final PushService pushService;
    private final ObjectMapper objectMapper;

    public WebPushService() {
        // Add Bouncy Castle as security provider for web push
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        
        this.pushService = new PushService();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Send web push notification
     */
    public CompletableFuture<Void> sendNotification(
            String subscriptionJson, 
            String title, 
            String body, 
            String actionUrl,
            Map<String, String> metadata) {
        
        return CompletableFuture.runAsync(() -> {
            try {
                // Configure push service
                pushService.setPublicKey(publicKey);
                pushService.setPrivateKey(privateKey);
                pushService.setSubject(subject);

                // Parse subscription
                Subscription subscription = objectMapper.readValue(subscriptionJson, Subscription.class);

                // Create notification payload
                WebPushPayload payload = WebPushPayload.builder()
                    .title(title)
                    .body(body)
                    .icon("/icons/notification-icon.png")
                    .badge("/icons/notification-badge.png")
                    .url(actionUrl)
                    .tag("coachera-notification")
                    .requireInteraction(true)
                    .data(metadata)
                    .build();

                String payloadJson = objectMapper.writeValueAsString(payload);

                // Create and send notification
                Notification notification = new Notification(subscription, payloadJson);
                pushService.send(notification);

                log.info("Successfully sent web push notification");

            } catch (Exception e) {
                log.error("Error sending web push notification", e);
                throw new RuntimeException("Failed to send web push notification", e);
            }
        });
    }

    /**
     * Generate VAPID keys (run this once to generate your keys)
     */
    public VapidKeys generateVapidKeys() {
        try {
            return VapidKeys.builder()
                .publicKey(pushService.getPublicKey().toString())
                .privateKey(pushService.getPrivateKey().toString())
                .build();
        } catch (Exception e) {
            log.error("Error generating VAPID keys", e);
            throw new RuntimeException("Failed to generate VAPID keys", e);
        }
    }

    // Inner classes for web push payload and VAPID keys
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class WebPushPayload {
        private String title;
        private String body;
        private String icon;
        private String badge;
        private String url;
        private String tag;
        private boolean requireInteraction;
        private Map<String, String> data;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class VapidKeys {
        private String publicKey;
        private String privateKey;
    }
}