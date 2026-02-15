package com.mawa3id.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicBookingProfileDTO {
    private UUID businessId;
    private String businessName;
    private String phone;
    private List<ServiceDTO> services;
    private List<AvailabilityDTO> availability;
}