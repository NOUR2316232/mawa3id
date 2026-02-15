package com.mawa3id.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsDTO {
    private long totalAppointments;
    private long confirmedAppointments;
    private long cancelledAppointments;
    private long noShowAppointments;
    private long pendingAppointments;
    
    private double noShowRate;
    private double confirmationRate;
    private double cancellationRate;
    
    private BigDecimal totalRevenue;
    private BigDecimal revenueLost;
    private BigDecimal revenueSaved;
}
