package disa.notification.service.service.interfaces;

import disa.notification.service.entity.NotificationConfig;

import java.util.List;

public interface LabLoaderService {
    List<LabResultSummary> findLabSummaryResultsFromLastWeek(String province);

    List<LabResults> findLabResultsFromLastWeek(String province);

    List<LabResults> findLabResultsPendingMoreThan2Days(String province);

    List<PendingHealthFacilitySummary> findPendingHealthFacilitySummary(String province);

    List<NotificationConfig> findActive();

}
