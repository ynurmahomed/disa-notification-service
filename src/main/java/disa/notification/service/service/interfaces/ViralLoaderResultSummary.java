package disa.notification.service.service.interfaces;

public interface ViralLoaderResultSummary {
    String getFacilityName();
    int getTotalReceived();
    int getProcessed();
    int getNotProcessedNoResult();
    int getNotProcessedNidNotFount();
}
