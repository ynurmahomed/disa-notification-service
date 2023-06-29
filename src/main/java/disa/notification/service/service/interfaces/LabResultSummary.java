package disa.notification.service.service.interfaces;

public interface ViralLoaderResultSummary {
    String getRequestingDistrictName();
    String getHealthFacilityLabCode();
    String getFacilityName();
    int getTotalReceived();
    int getTotalPending();
    int getProcessed();
    int getNotProcessedInvalidResult();
    int getNotProcessedNidNotFount();
    int getNotProcessedDuplicateNid();
    int getNotProcessedDuplicatedRequestId();
}
