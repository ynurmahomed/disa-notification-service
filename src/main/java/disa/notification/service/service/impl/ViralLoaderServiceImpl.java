package disa.notification.service.service.impl;

import disa.notification.service.repository.ViralLoaderRepository;
import disa.notification.service.service.interfaces.ViralLoaderResult;
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
    public  List<ViralLoaderResult> findViralLoadsFromLastWeek() {
        DateInterval lastWeekInterval= DateTimeUtils.getLastWeekInterVal();
        return viralLoaderRepository.findViralLoadResult(lastWeekInterval.getStartDateTime(),lastWeekInterval.getEndDateTime());
    }
}
