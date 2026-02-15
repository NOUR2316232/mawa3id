package com.mawa3id.service;

import com.mawa3id.dto.ServiceDTO;
import com.mawa3id.model.Service;
import com.mawa3id.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Transactional
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    public Service createService(UUID userId, ServiceDTO request) {
        Service service = new Service();
        service.setUserId(userId);
        service.setName(request.getName());
        service.setDurationMinutes(request.getDurationMinutes());
        service.setPrice(request.getPrice());

        return serviceRepository.save(service);
    }

    public List<ServiceDTO> getServicesByUserId(UUID userId) {
        return serviceRepository.findByUserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ServiceDTO getServiceById(UUID serviceId) {
        return serviceRepository.findById(serviceId)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    public Service updateService(UUID serviceId, UUID userId, ServiceDTO request) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        if (!service.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this service");
        }

        service.setName(request.getName());
        service.setDurationMinutes(request.getDurationMinutes());
        service.setPrice(request.getPrice());

        return serviceRepository.save(service);
    }

    public void deleteService(UUID serviceId, UUID userId) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        if (!service.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this service");
        }

        serviceRepository.delete(service);
    }

    private ServiceDTO toDTO(Service service) {
        return new ServiceDTO(
                service.getId(),
                service.getName(),
                service.getDurationMinutes(),
                service.getPrice(),
                service.getCreatedAt(),
                service.getUpdatedAt()
        );
    }
}


