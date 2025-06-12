package org.example.lifechart.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.lifechart.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String title;

    private String message;

    @Column(columnDefinition = "DATETIME(0)")
    private LocalDateTime requestedAt;

    private LocalDateTime processedAt;

    private LocalDateTime viewedAt;

    private LocalDateTime readAt;


    public String getEventId(){
        return String.valueOf(user.getId()) + "-" +
                type + "-" +
                requestedAt.toString() + "-" +
                title;
    }


    public enum Type {
        NOTICE, EMAIL, PUSH, EVENT
    }

    public enum Status {
        FAILED, UNREAD, READ
    }
}