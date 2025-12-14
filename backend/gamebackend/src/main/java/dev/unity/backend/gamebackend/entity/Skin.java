package dev.unity.backend.gamebackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "skins")
@EqualsAndHashCode(onlyExplicitlyIncluded = true) 
public class Skin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer skinId;

    @Column(nullable = false)
    private String name;

    private Integer price = 0;
    private String rarity;
    private Boolean unlockedByDefault = false;

    @Column(name = "image_data")
    @Basic(fetch = FetchType.LAZY) 
    private byte[] imageData;

  
    @ManyToMany(mappedBy = "ownedSkins")
    @JsonIgnore
    private Set<User> users;
}
