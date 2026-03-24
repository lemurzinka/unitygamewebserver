package dev.unity.backend.gamebackend.repository;

import dev.unity.backend.gamebackend.entity.UserLogin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserLoginRepository extends JpaRepository<UserLogin, Long> {
    @Query("SELECT DATE(l.loginDate), COUNT(l) FROM UserLogin l GROUP BY DATE(l.loginDate) ORDER BY DATE(l.loginDate)")
List<Object[]> countLoginsPerDay();
 
}
