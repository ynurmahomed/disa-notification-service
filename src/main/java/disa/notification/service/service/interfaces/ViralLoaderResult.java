package disa.notification.service.service.interfaces;

import java.time.LocalDateTime;

public interface ViralLoaderResult {
    String getFacilityName();
    int getTotalReceived();
    int getProcessed();
    int getNotProcessedNoResult();
    int getNotProcessedNidNotFount();
}
