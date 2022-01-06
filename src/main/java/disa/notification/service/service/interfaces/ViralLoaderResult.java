package disa.notification.service.service.interfaces;

import java.time.LocalDateTime;

public interface ViralLoaderResult {
    String getFacilityName();
    int getProcessed();
    int getNotProcessed();
    LocalDateTime getProcessingDate();

}
