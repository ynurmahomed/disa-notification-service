package disa.notification.service.service.interfaces;

import java.time.LocalDateTime;


public interface ViralLoaderResults {

     String getNID();

     String getRequestId();

     String getFirstName();

     String getLastName();

     String getHealthFacilityLabCode();

     String getRequestingFacilityName();

     String  getViralLoadStatus();

     String getNotProcessingCause();

     LocalDateTime getCreatedAt();

}
