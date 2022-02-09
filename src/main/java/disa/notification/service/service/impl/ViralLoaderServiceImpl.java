package disa.notification.service.service.impl;

import disa.notification.service.repository.ViralLoaderRepository;
import disa.notification.service.service.interfaces.ViralLoaderResultSummary;
import disa.notification.service.service.interfaces.ViralLoaderResults;
import disa.notification.service.service.interfaces.ViralLoaderService;
import disa.notification.service.utils.DateInterval;
import disa.notification.service.utils.DateTimeUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ViralLoaderServiceImpl  implements ViralLoaderService {
    private final ViralLoaderRepository viralLoaderRepository;

    @Override
    public  List<ViralLoaderResultSummary> findViralLoadsFromLastWeek() {
        DateInterval lastWeekInterval= DateTimeUtils.getLastWeekInterVal();
        return viralLoaderRepository.findViralLoadResultSummary(lastWeekInterval.getStartDateTime(),lastWeekInterval.getEndDateTime());
    }

    @Override
    public List<ViralLoaderResults> findViralLoadResultsFromLastWeek() {
        DateInterval lastWeekInterval= DateTimeUtils.getLastWeekInterVal();
        return viralLoaderRepository.findViralLoadResults(lastWeekInterval.getStartDateTime(),lastWeekInterval.getEndDateTime());
    }

    @Override
    public List<ViralLoaderResults> findUnsyncronizedViralResults() {
        return viralLoaderRepository.findUnsicronizedViralLoadResults();
    }
}
