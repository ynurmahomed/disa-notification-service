package disa.notification.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViralResultStatistics {

    private int processed;
    private int pending;
    private int noProcessedNoResult;
    private int noProcessedNidNotFound;
    private int total;

    public double getProcessedPercentage() {
        return (double) processed / total;
    }

    public double getPendingPercentage() {
        return (double) pending / total;
    }

    public double getNoProcessedNoResultPercentage() {
        return (double) noProcessedNoResult / total;
    }

    public double getNoProcessedNidNotFoundPercentage() {
        return (double) noProcessedNidNotFound / total;
    }

    public double getTotalPercentage() {
        return (double) total / total;
    }
}
