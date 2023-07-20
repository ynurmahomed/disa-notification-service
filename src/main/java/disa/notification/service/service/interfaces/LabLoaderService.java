package disa.notification.service.service.interfaces;

import java.util.List;

import disa.notification.service.entity.ImplementingPartner;

public interface LabLoaderService {
	
    List<LabResultSummary> findLabSummaryResultsFromLastWeek(ImplementingPartner ip);

    List<LabResults> findLabResultsFromLastWeek(ImplementingPartner ip);

    List<LabResults> findLabResultsPendingMoreThan2Days(ImplementingPartner ip);

    List<PendingHealthFacilitySummary> findPendingHealthFacilitySummary(ImplementingPartner ip);
}
