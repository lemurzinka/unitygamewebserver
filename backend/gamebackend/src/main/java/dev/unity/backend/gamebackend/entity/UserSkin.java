package dev.unity.backend.gamebackend.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_skins")
@IdClass(UserSkinId.class)
public class UserSkin {
    @Id
    private Integer userId;

    @Id
    private Integer skinId;

    private Boolean unlocked = false;
    private Boolean isActive = false;

    @ManyToOne
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "skinId", insertable = false, updatable = false)
    private Skin skin;

    // Getters, setters, constructors
}
