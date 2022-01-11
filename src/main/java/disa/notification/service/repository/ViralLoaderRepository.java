package disa.notification.service.repository;

import disa.notification.service.entity.ViralLoaderEntity;
import disa.notification.service.service.interfaces.ViralLoaderResult;
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
            " from VlData where  CREATED_AT BETWEEN :startDateTime AND :endDateTime group by RequestingFacilityName",nativeQuery = true)
    List<ViralLoaderResult> findViralLoadResult( @Param("startDateTime") LocalDateTime startDateTime,@Param("endDateTime") LocalDateTime endDateTime);
}
