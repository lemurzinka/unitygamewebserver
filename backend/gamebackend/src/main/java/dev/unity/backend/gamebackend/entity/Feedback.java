package dev.unity.backend.gamebackend.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feedbacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 1000)
    private String message;


    @Column(nullable = false)
    private String sentiment;


    private Double score;

}
