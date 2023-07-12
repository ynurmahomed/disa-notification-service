package disa.notification.service.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import disa.notification.service.entity.ImplementingPartner;
import disa.notification.service.repository.ViralLoaderRepository;
import disa.notification.service.service.interfaces.PendingHealthFacilitySummary;
import disa.notification.service.service.interfaces.ViralLoaderResultSummary;
import disa.notification.service.service.interfaces.ViralLoaderResults;
import disa.notification.service.service.interfaces.ViralLoaderService;
import disa.notification.service.utils.DateInterval;
import disa.notification.service.utils.DateTimeUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ViralLoaderServiceImpl implements ViralLoaderService {
    private final ViralLoaderRepository viralLoaderRepository;

    @Override
    public List<ViralLoaderResultSummary> findViralLoadsFromLastWeek(ImplementingPartner ip) {
        DateInterval lastWeekInterval = DateTimeUtils.getLastWeekInterVal();
        return viralLoaderRepository.findViralLoadResultSummary(lastWeekInterval.getStartDateTime(),
                lastWeekInterval.getEndDateTime(), ip.getOrgUnitCodes());
    }

    @Override
    public List<ViralLoaderResults> findViralLoadResultsFromLastWeek(ImplementingPartner ip) {
        DateInterval lastWeekInterval = DateTimeUtils.getLastWeekInterVal();
        return viralLoaderRepository.findViralLoadResults(lastWeekInterval.getStartDateTime(),
                lastWeekInterval.getEndDateTime(), ip.getOrgUnitCodes());
    }

    @Override
    public List<ViralLoaderResults> findViralLoadResultsPendingMoreThan2Days(ImplementingPartner ip) {
        return viralLoaderRepository.findViralLoadResultsPendingMoreThan2Days(ip.getOrgUnitCodes());
    }

    @Override
    public List<PendingHealthFacilitySummary> findPendingHealthFacilitySummary(ImplementingPartner ip) {
        return viralLoaderRepository.findUnsincronizedHealthFacilities(ip.getOrgUnitCodes());
    }
}
