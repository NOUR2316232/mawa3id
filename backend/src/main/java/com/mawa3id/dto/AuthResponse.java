package com.mawa3id.dto;

import com.mawa3id.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String refreshToken;
    private UserDTO user;
    private String message;

    @Data
    @NoArgsConstructor
    public static class UserDTO {
        private UUID id;
        private String businessName;
        private String email;
        private String phone;
        private String subscriptionPlan;
        private LocalDateTime createdAt;

        public static UserDTO fromUser(User user) {
            return UserDTO.builder()
                    .id(user.getId())
                    .businessName(user.getBusinessName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .subscriptionPlan(user.getSubscriptionPlan().name())
                    .createdAt(user.getCreatedAt())
                    .build();
        }

        @Builder
        public UserDTO(UUID id, String businessName, String email, String phone, String subscriptionPlan, LocalDateTime createdAt) {
            this.id = id;
            this.businessName = businessName;
            this.email = email;
            this.phone = phone;
            this.subscriptionPlan = subscriptionPlan;
            this.createdAt = createdAt;
        }
    }
}