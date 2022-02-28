package disa.notification.service.service.interfaces;

import java.time.LocalDateTime;

public interface PendingHealthFacilitySummary {
    String getRequestingDistrictName();
    String getHealthFacilityLabCode();
    String getFacilityName();
    int getTotalPending();
    LocalDateTime getLastSyncDate();

}
