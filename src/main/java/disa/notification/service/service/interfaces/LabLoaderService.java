package disa.notification.service.service.interfaces;

import java.util.List;

import disa.notification.service.entity.ImplementingPartner;
import disa.notification.service.utils.DateInterval;

public interface LabLoaderService {
	
    List<LabResultSummary> findLabSummaryResultsFromDateInterval(ImplementingPartner ip, DateInterval dateInterval);

    List<LabResults> findLabResultsFromDateInterval(ImplementingPartner ip, DateInterval dateInterval);

    List<LabResults> findLabResultsPendingMoreThan2Days(ImplementingPartner ip);

    List<PendingHealthFacilitySummary> findPendingHealthFacilitySummary(ImplementingPartner ip);
}
