package dev.unity.backend.gamebackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_levels")
@IdClass(UserLevelId.class)
public class UserLevel {
    @Id
    private Integer userId;

    @Id
    private Integer levelId;

    private Float bestTime;
    private Integer deaths = 0;
    private Boolean completed = false;

    @ManyToOne
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "levelId", insertable = false, updatable = false)
    private Level level;

    // Getters, setters, constructors
}
