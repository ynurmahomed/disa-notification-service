package disa.notification.service.repository;

import disa.notification.service.entity.ViralLoaderEntity;
import disa.notification.service.service.interfaces.ViralLoaderResultSummary;
import disa.notification.service.service.interfaces.ViralLoaderResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface ViralLoaderRepository extends JpaRepository<ViralLoaderEntity,Integer> {

    @Query(value = "SELECT RequestingFacilityName as facilityName, AnalysisDateTime as processingDate,COUNT(*) as totalReceived, " +
            "COALESCE(SUM( CASE WHEN VIRAL_LOAD_STATUS ='PROCESSED' THEN 1 END),0) AS processed," +
            "COALESCE(SUM( CASE WHEN VIRAL_LOAD_STATUS = 'NOT_PROCESSED' AND NOT_PROCESSING_CAUSE='NID_NOT_FOUND' THEN 1 END),0) AS notProcessedNidNotFount, " +
            "COALESCE(SUM( CASE WHEN VIRAL_LOAD_STATUS = 'NOT_PROCESSED' AND NOT_PROCESSING_CAUSE='NO_RESULT' THEN 1 END),0) AS notProcessedNoResult "+
            " from VlData where VIRAL_LOAD_STATUS!='PENDING' AND  CREATED_AT BETWEEN :startDateTime AND :endDateTime group by RequestingFacilityName",nativeQuery = true)
    List<ViralLoaderResultSummary> findViralLoadResultSummary(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    @Query(value = "SELECT RequestId as requestId, UNIQUEID as nid, FIRSTNAME as firstName, SURNAME as lastName,RequestingFacilityName as requestingFacilityName," +
            "ReceivingFacilityCode as healthFacilityLabCode, VIRAL_LOAD_STATUS as viralLoadStatus, NOT_PROCESSING_CAUSE as notProcessingCause, " +
            "CREATED_AT as createdAt " +
            " from VlData where  VIRAL_LOAD_STATUS!='PENDING' AND  CREATED_AT BETWEEN :startDateTime AND :endDateTime ",nativeQuery = true)
    List<ViralLoaderResults> findViralLoadResults(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);


    @Query(value = "SELECT RequestId as requestId, UNIQUEID as nid, FIRSTNAME as firstName, SURNAME as lastName,RequestingFacilityName as requestingFacilityName," +
            "ReceivingFacilityCode as healthFacilityLabCode, VIRAL_LOAD_STATUS as viralLoadStatus, NOT_PROCESSING_CAUSE as notProcessingCause, " +
            "CREATED_AT as createdAt " +
            " from VlData where  VIRAL_LOAD_STATUS='PENDING' AND  DATEDIFF(CURRENT_TIMESTAMP, CREATED_AT)>=7 ",nativeQuery = true)
    List<ViralLoaderResults> findUnsicronizedViralLoadResults();
}
