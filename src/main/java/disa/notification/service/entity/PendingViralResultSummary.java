package disa.notification.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingViralResultSummary {
    private String requestingDistrictName;
    private String healthFacilityLabCode;
    private String facilityName;
    private int totalPending;
    private LocalDateTime lastSyncDate;
}
