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
    public List<ViralLoaderResults> findViralLoadResultsPendingMoreThan2Days(String province) {
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
