package disa.notification.service.service.interfaces;

import java.util.List;

import disa.notification.service.entity.ImplementingPartner;

public interface ViralLoaderService {
    List<ViralLoaderResultSummary> findViralLoadsFromLastWeek(ImplementingPartner ip);

    List<ViralLoaderResults> findViralLoadResultsFromLastWeek(ImplementingPartner ip);

    List<ViralLoaderResults> findViralLoadResultsPendingMoreThan2Days(ImplementingPartner ip);

    List<PendingHealthFacilitySummary> findPendingHealthFacilitySummary(ImplementingPartner ip);
}
