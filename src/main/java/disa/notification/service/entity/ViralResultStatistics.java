package disa.notification.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class  ViralResultStatistics {
    private String district;
    private String processed;
    private String pending;
    private String noProcessedNoResult;
    private String noProcessedNidNotFound;
    private String total;
    private String processedPercentage;
    private String pendingPercentage;
    private String noProcessedNoResultPercentage;
    private String noProcessedNidNotFoundPercentage;
    private String totalPercentage;
}
