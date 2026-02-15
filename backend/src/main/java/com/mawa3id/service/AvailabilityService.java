package com.mawa3id.service;

import com.mawa3id.dto.AvailabilityDTO;
import com.mawa3id.model.Availability;
import com.mawa3id.repository.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    public Availability createAvailability(UUID userId, AvailabilityDTO request) {
        if (request.getDayOfWeek() < 0 || request.getDayOfWeek() > 6) {
            throw new RuntimeException("Day of week must be between 0 and 6");
        }

        Availability availability = new Availability();
        availability.setUserId(userId);
        availability.setDayOfWeek(request.getDayOfWeek());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());

        if (availability.getStartTime().isAfter(availability.getEndTime())) {
            throw new RuntimeException("Start time must be before end time");
        }

        return availabilityRepository.save(availability);
    }

    public List<AvailabilityDTO> getAvailabilitiesByUserId(UUID userId) {
        return availabilityRepository.findByUserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AvailabilityDTO> getAvailabilitiesByDayOfWeek(UUID userId, Integer dayOfWeek) {
        return availabilityRepository.findByUserIdAndDayOfWeek(userId, dayOfWeek)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Availability updateAvailability(UUID availabilityId, UUID userId, AvailabilityDTO request) {
        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Availability not found"));

        if (!availability.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this availability");
        }

        availability.setDayOfWeek(request.getDayOfWeek());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());

        return availabilityRepository.save(availability);
    }

    public void deleteAvailability(UUID availabilityId, UUID userId) {
        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Availability not found"));

        if (!availability.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this availability");
        }

        availabilityRepository.delete(availability);
    }

    private AvailabilityDTO toDTO(Availability availability) {
        return new AvailabilityDTO(
                availability.getId(),
                availability.getDayOfWeek(),
                availability.getStartTime(),
                availability.getEndTime()
        );
    }
}
