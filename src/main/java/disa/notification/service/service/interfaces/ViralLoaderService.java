package disa.notification.service.service.interfaces;

import java.util.List;

import disa.notification.service.entity.ImplementingPartner;

public interface ViralLoaderService {
    List<LabResultSummary> findViralLoadsFromLastWeek(ImplementingPartner ip);

    List<LabResults> findViralLoadResultsFromLastWeek(ImplementingPartner ip);

    List<LabResults> findViralLoadResultsPendingMoreThan2Days(ImplementingPartner ip);

    List<PendingHealthFacilitySummary> findPendingHealthFacilitySummary(ImplementingPartner ip);
}
