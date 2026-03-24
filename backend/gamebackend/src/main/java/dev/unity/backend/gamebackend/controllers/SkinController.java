package dev.unity.backend.gamebackend.controllers;

import dev.unity.backend.gamebackend.entity.Skin;
import dev.unity.backend.gamebackend.entity.User;
import dev.unity.backend.gamebackend.repository.SkinRepository;
import dev.unity.backend.gamebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/skins")
@RequiredArgsConstructor
public class SkinController {

    private static final Logger logger = LoggerFactory.getLogger(SkinController.class);

    private final SkinRepository skinRepository;
    private final UserRepository userRepository;

  
    public record SkinDto(Integer skinId, String name, Integer price, String rarity, Boolean unlockedByDefault) {}

  
    @GetMapping
    @Transactional(readOnly = true)
    public List<SkinDto> getAllSkins() {
        return skinRepository.findAll().stream()
                .map(s -> new SkinDto(
                        s.getSkinId(),
                        s.getName(),
                        s.getPrice(),
                        s.getRarity(),
                        s.getUnlockedByDefault()
                ))
                .toList();
    }

  
@PostMapping("/upload")
@Transactional
public ResponseEntity<?> uploadSkin(
        @RequestParam("file") MultipartFile file,
        @RequestParam("name") String name,
        @RequestParam("rarity") String rarity,
        @RequestParam(value = "price", defaultValue = "0") Integer price,
        Authentication auth
) throws IOException {

    String email = auth.getName();
    User user = userRepository.findByEmailIgnoreCase(email).orElse(null);

    if (user == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "USER_NOT_FOUND"));
    }


    if (!Boolean.TRUE.equals(user.getIsAdmin())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "NOT_ADMIN"));
    }


    Skin skin = new Skin();
    skin.setName(name);
    skin.setRarity(rarity);
    skin.setPrice(price);
    skin.setUnlockedByDefault(false);
    skin.setImageData(file.getBytes());

    Skin saved = skinRepository.save(skin);

    return ResponseEntity.ok(new SkinDto(
            saved.getSkinId(),
            saved.getName(),
            saved.getPrice(),
            saved.getRarity(),
            saved.getUnlockedByDefault()
    ));
}



    @GetMapping("/{skinId}/image")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> getSkinImage(@PathVariable Integer skinId) {
        Skin skin = skinRepository.findById(skinId).orElseThrow();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                .body(skin.getImageData());
    }


  @PostMapping("/{skinId}/buy")
@Transactional
public ResponseEntity<?> buySkin(@PathVariable Integer skinId, Authentication auth) {
    String email = auth.getName();
    User user = userRepository.findByEmailWithSkins(email).orElse(null);
    Skin skin = skinRepository.findById(skinId).orElse(null);

    if (user == null || skin == null) {
        return ResponseEntity.badRequest().body(Map.of("error", "USER_OR_SKIN_NOT_FOUND"));
    }

    if (user.getBalance() < skin.getPrice()) {
        return ResponseEntity.badRequest().body(Map.of("error", "INSUFFICIENT_FUNDS"));
    }

    user.setBalance(user.getBalance() - skin.getPrice());
    user.getOwnedSkins().add(skin); 

    userRepository.save(user);

    logger.info("💸 User {} bought skin '{}', new balance={}", user.getUsername(), skin.getName(), user.getBalance());

    return ResponseEntity.ok(Map.of(
            "success", true,
            "newBalance", user.getBalance(),
            "ownedSkinId", skin.getSkinId()
    ));
}



   
   @PostMapping("/{skinId}/select")
@Transactional
public ResponseEntity<?> selectSkin(@PathVariable Integer skinId, Authentication auth) {
    String email = auth.getName();
    User user = userRepository.findByEmailWithSkins(email).orElse(null); 
    Skin skin = skinRepository.findById(skinId).orElse(null);

    if (user == null || skin == null) {
        return ResponseEntity.badRequest().body(Map.of("error", "USER_OR_SKIN_NOT_FOUND"));
    }

    boolean owns = user.getOwnedSkins().stream()
            .anyMatch(s -> s.getSkinId().equals(skinId));
    if (!owns) {
        return ResponseEntity.badRequest().body(Map.of("error", "SKIN_NOT_OWNED"));
    }

    user.setSelectedSkin(skin);
    userRepository.save(user);

    logger.info("🎨 User {} selected skin '{}'", user.getUsername(), skin.getName());

    return ResponseEntity.ok(Map.of(
            "success", true,
            "selectedSkinId", skin.getSkinId()
    ));
}

@DeleteMapping("/{skinId}")
@Transactional
public ResponseEntity<?> deleteSkin(@PathVariable Integer skinId, Authentication auth) {
    String email = auth.getName();
    User user = userRepository.findByEmailIgnoreCase(email).orElse(null);

    if (user == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "USER_NOT_FOUND"));
    }

    if (!Boolean.TRUE.equals(user.getIsAdmin())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "NOT_ADMIN"));
    }

    Skin skin = skinRepository.findById(skinId).orElse(null);
    if (skin == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "SKIN_NOT_FOUND"));
    }

  
    userRepository.findAll().forEach(u -> u.getOwnedSkins().remove(skin));

    skinRepository.delete(skin);

    logger.info("🗑️ Admin {} deleted skin '{}'", user.getUsername(), skin.getName());

    return ResponseEntity.ok(Map.of("success", true, "deletedSkinId", skinId));
}



}
