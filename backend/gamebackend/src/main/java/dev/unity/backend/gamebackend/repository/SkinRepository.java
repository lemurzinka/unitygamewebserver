package dev.unity.backend.gamebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.unity.backend.gamebackend.entity.Skin;

public interface SkinRepository extends JpaRepository<Skin, Integer> {
}
