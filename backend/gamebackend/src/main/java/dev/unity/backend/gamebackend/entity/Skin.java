package dev.unity.backend.gamebackend.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "skins")
public class Skin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer skinId;

    @Column(nullable = false)
    private String name;

    private Integer price = 0;
    private String rarity;
    private String imageUrl;
    private Boolean unlockedByDefault = false;

    // Getters, setters, constructors
}
