package disa.notification.service.service.interfaces;

import disa.notification.service.entity.NotificationConfig;
import disa.notification.service.entity.PendingViralResultSummary;

import java.util.List;

public interface ViralLoaderService {
    List<ViralLoaderResultSummary> findViralLoadsFromLastWeek(String province);

    List<ViralLoaderResults> findViralLoadResultsFromLastWeek(String province);

    List<ViralLoaderResults> findUnsyncronizedViralResults(String province);

    List<PendingViralResultSummary> findPendingHealthFacilitySummary(String province);

    List<NotificationConfig> findActive();

}
