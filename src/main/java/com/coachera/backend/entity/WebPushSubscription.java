package com.coachera.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "web_push_subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebPushSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String subscriptionJson;

    private String userAgent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
