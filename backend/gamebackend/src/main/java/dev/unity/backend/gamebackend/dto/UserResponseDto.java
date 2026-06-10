package dev.unity.backend.gamebackend.dto;

import java.util.List;

public class UserResponseDto {
    private Long id;
    private String email;
    private String username;
    private Integer balance;
    private Boolean isAdmin;
    private Long selectedSkinId;
    private List<Long> ownedSkinIds;
    private String token;
    private String message;

    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getBalance() { return balance; }
    public void setBalance(Integer balance) { this.balance = balance; }

    public Boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }

    public Long getSelectedSkinId() { return selectedSkinId; }
    public void setSelectedSkinId(Long selectedSkinId) { this.selectedSkinId = selectedSkinId; }

    public List<Long> getOwnedSkinIds() { return ownedSkinIds; }
    public void setOwnedSkinIds(List<Long> ownedSkinIds) { this.ownedSkinIds = ownedSkinIds; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
