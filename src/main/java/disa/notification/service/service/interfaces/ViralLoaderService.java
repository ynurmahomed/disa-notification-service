package disa.notification.service.service.interfaces;

import java.util.List;

public interface ViralLoaderService {
    List<ViralLoaderResultSummary> findViralLoadsFromLastWeek();

    List<ViralLoaderResults> findViralLoadResultsFromLastWeek();

    List<ViralLoaderResults> findUnsyncronizedViralResults();
}
