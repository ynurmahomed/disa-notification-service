package disa.notification.service.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import disa.notification.service.entity.ImplementingPartner;
import disa.notification.service.repository.ViralLoaderRepository;
import disa.notification.service.service.interfaces.LabLoaderService;
import disa.notification.service.service.interfaces.LabResultSummary;
import disa.notification.service.service.interfaces.LabResults;
import disa.notification.service.service.interfaces.PendingHealthFacilitySummary;
import disa.notification.service.utils.DateInterval;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class LabLoaderServiceImpl implements LabLoaderService {
    private final ViralLoaderRepository viralLoaderRepository;

    @Override
    public List<LabResultSummary> findLabSummaryResultsFromDateInterval(ImplementingPartner ip,
            DateInterval dateInterval) {
        return viralLoaderRepository.findViralLoadResultSummary(dateInterval.getStartDateTime(),
                dateInterval.getEndDateTime(), ip.getOrgUnitCodes());
    }

    @Override
    public List<LabResults> findLabResultsFromDateInterval(ImplementingPartner ip, DateInterval dateInterval) {
        return viralLoaderRepository.findViralLoadResults(dateInterval.getStartDateTime(),
                dateInterval.getEndDateTime(), ip.getOrgUnitCodes());
    }

    @Override
    public List<LabResults> findLabResultsPendingMoreThan2Days(ImplementingPartner ip) {
        return viralLoaderRepository.findViralLoadResultsPendingMoreThan2Days(ip.getOrgUnitCodes());
    }

    @Override
    public List<PendingHealthFacilitySummary> findPendingHealthFacilitySummary(ImplementingPartner ip) {
        return viralLoaderRepository.findUnsincronizedHealthFacilities(ip.getOrgUnitCodes());
    }
}
