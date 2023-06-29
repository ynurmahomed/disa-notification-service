package disa.notification.service.service.impl;

import disa.notification.service.entity.NotificationConfig;
import disa.notification.service.repository.NotificationConfigRepository;
import disa.notification.service.repository.ViralLoaderRepository;
import disa.notification.service.service.interfaces.*;
import disa.notification.service.utils.DateInterval;
import disa.notification.service.utils.DateTimeUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ViralLoaderServiceImpl  implements LabLoaderService {
    private final ViralLoaderRepository viralLoaderRepository;
    private final NotificationConfigRepository notificationConfigRepository;

    @Override
    public  List<LabResultSummary> findLabSummaryResultsFromLastWeek(String province) {
        DateInterval lastWeekInterval= DateTimeUtils.getLastWeekInterVal();
        return viralLoaderRepository.findViralLoadResultSummary(lastWeekInterval.getStartDateTime(),lastWeekInterval.getEndDateTime(),province);
    }

    @Override
    public List<LabResults> findLabResultsFromLastWeek(String province) {
        DateInterval lastWeekInterval= DateTimeUtils.getLastWeekInterVal();
        return viralLoaderRepository.findViralLoadResults(lastWeekInterval.getStartDateTime(),lastWeekInterval.getEndDateTime(),province);
    }

    @Override
    public List<LabResults> findLabResultsPendingMoreThan2Days(String province) {
        return viralLoaderRepository.findViralLoadResultsPendingMoreThan2Days(province);
    }

    @Override
    public List<PendingHealthFacilitySummary> findPendingHealthFacilitySummary(String province) {
       return viralLoaderRepository.findUnsincronizedHealthFacilities(province);

    }

    @Override
    public List<NotificationConfig> findActive() {
        return notificationConfigRepository.findByActiveTrue();
    }

}
