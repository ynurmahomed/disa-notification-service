package disa.notification.service.utils;

import disa.notification.service.service.interfaces.ViralLoaderResultSummary;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VlResultSummaryImpl implements ViralLoaderResultSummary {
    private String requestingDistrictName;
    private String healthFacilityLabCode;
    private String facilityName;
    private int totalReceived;
    private int totalPending;
    private int processed;
    private int notProcessedNoResult;
    private int notProcessedNidNotFount;
    private int notProcessedDuplicateNid;
    private int notProcessedFlaggedForReview;
}
