package disa.notification.service.service.impl;

import disa.notification.service.entity.NotificationConfig;
import disa.notification.service.entity.PendingViralResultSummary;
import disa.notification.service.repository.NotificationConfigRepository;
import disa.notification.service.repository.ViralLoaderRepository;
import disa.notification.service.service.interfaces.*;
import disa.notification.service.utils.DateInterval;
import disa.notification.service.utils.DateTimeUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ViralLoaderServiceImpl  implements ViralLoaderService {
    private final ViralLoaderRepository viralLoaderRepository;
    private final NotificationConfigRepository notificationConfigRepository;

    @Override
    public  List<ViralLoaderResultSummary> findViralLoadsFromLastWeek(String province) {
        DateInterval lastWeekInterval= DateTimeUtils.getLastWeekInterVal();
        return viralLoaderRepository.findViralLoadResultSummary(lastWeekInterval.getStartDateTime(),lastWeekInterval.getEndDateTime(),province);
    }

    @Override
    public List<ViralLoaderResults> findViralLoadResultsFromLastWeek(String province) {
        DateInterval lastWeekInterval= DateTimeUtils.getLastWeekInterVal();
        return viralLoaderRepository.findViralLoadResults(lastWeekInterval.getStartDateTime(),lastWeekInterval.getEndDateTime(),province);
    }

    @Override
    public List<ViralLoaderResults> findUnsyncronizedViralResults(String province) {
        return viralLoaderRepository.findUnsicronizedViralLoadResults(province);
    }

    @Override
    public List<PendingViralResultSummary> findPendingHealthFacilitySummary(String province) {
       List<PendingHealthFacilitySummary> pendingHealthFacilitySummaries= viralLoaderRepository.findUnsincronizedHealthFacilities(province);

      return  pendingHealthFacilitySummaries.stream()
                .map(pendingHealthFacilitySummary -> PendingViralResultSummary.builder()
                        .facilityName(pendingHealthFacilitySummary.getFacilityName())
                        .totalPending(pendingHealthFacilitySummary.getTotalPending())
                        .healthFacilityLabCode(pendingHealthFacilitySummary.getHealthFacilityLabCode())
                        .requestingDistrictName(pendingHealthFacilitySummary.getRequestingDistrictName())
                        .lastSyncDate(getLastSyncDate(pendingHealthFacilitySummary))
                        .build())
                .collect(Collectors.toList());

    }

    private LocalDateTime getLastSyncDate(PendingHealthFacilitySummary pendingHealthFacilitySummary) {
        LastSyncDate  lastSyncDate=viralLoaderRepository.findLastSyncDateByHFCodeAndName(pendingHealthFacilitySummary.getHealthFacilityLabCode());
        return lastSyncDate.getLastSyncDate();
    }

    @Override
    public List<NotificationConfig> findActive() {
        return notificationConfigRepository.findByActiveTrue();
    }

}
