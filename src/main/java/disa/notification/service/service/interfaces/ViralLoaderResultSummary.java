package disa.notification.service.service.interfaces;

public interface ViralLoaderResultSummary {
    String getRequestingDistrictName();
    String getHealthFacilityLabCode();
    String getFacilityName();
    int getTotalReceived();
    int getTotalPending();
    int getProcessed();
    int getNotProcessedNoResult();
    int getNotProcessedNidNotFount();
    int getNotProcessedDuplicateNid();
    int getNotProcessedFlaggedForReview();
}
