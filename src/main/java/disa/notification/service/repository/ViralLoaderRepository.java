package disa.notification.service.repository;

import disa.notification.service.entity.ViralLoaderEntity;
import disa.notification.service.service.interfaces.ViralLoaderResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ViralLoaderRepository extends JpaRepository<ViralLoaderEntity,Integer> {

    @Query(value = "SELECT RequestingFacilityName as facilityName, AnalysisDateTime as processingDate,SUM( CASE WHEN VIRAL_LOAD_STATUS ='PROCESSED' THEN 1 END) AS processed," +
            "SUM( CASE WHEN VIRAL_LOAD_STATUS = 'NOT_PROCESSED' THEN 1 END) AS notProcessed from VlData where AnalysisDateTime is not null group by RequestingFacilityName",nativeQuery = true)
    List<ViralLoaderResult> findViralLoadResult();
}
