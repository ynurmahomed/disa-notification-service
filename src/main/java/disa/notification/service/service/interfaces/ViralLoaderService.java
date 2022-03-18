package disa.notification.service.service.interfaces;

import disa.notification.service.entity.NotificationConfig;

import java.util.List;

public interface ViralLoaderService {
    List<ViralLoaderResultSummary> findViralLoadsFromLastWeek(String province);

    List<ViralLoaderResults> findViralLoadResultsFromLastWeek(String province);

    List<ViralLoaderResults> findUnsyncronizedViralResults(String province);

    List<PendingHealthFacilitySummary> findPendingHealthFacilitySummary(String province);

    List<NotificationConfig> findActive();

}
