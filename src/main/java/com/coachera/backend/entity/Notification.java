package com.coachera.backend.entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.coachera.backend.entity.enums.NotificationStatus;
import com.coachera.backend.entity.enums.NotificationType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Notification extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User recipient;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private String actionUrl; // Deep link URL

    @ElementCollection
    @CollectionTable(name = "notification_metadata", joinColumns = @JoinColumn(name = "notification_id"))
    @MapKeyColumn(name = "meta_key")
    @Column(name = "meta_value")
    @Builder.Default
    private Map<String, String> metadata = new HashMap<>();

    @Builder.Default
    private boolean read = false;

    private LocalDateTime sentAt;

    private LocalDateTime readAt;

    // For web push
    @Column(columnDefinition = "TEXT")
    private String webPushSubscriptionJson;

    // For email
    private String emailAddress;
}
