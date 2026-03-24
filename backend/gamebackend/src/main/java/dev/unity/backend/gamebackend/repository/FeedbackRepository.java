package dev.unity.backend.gamebackend.repository;


import dev.unity.backend.gamebackend.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
