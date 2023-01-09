package disa.notification.service.entity;

import disa.notification.service.service.interfaces.ViralLoaderResultSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViralResultStatistics {

    private int total;
    private int processed;
    private int pending;
    private int noProcessedNoResult;
    private int noProcessedNidNotFound;
    private int notProcessedDuplicateNid;
    private int notProcessedFlaggedForReview;

    public void accumulate(ViralLoaderResultSummary summary) {
        total += summary.getTotalReceived();
        processed += summary.getProcessed();
        pending += summary.getTotalPending();
        noProcessedNoResult += summary.getNotProcessedNoResult();
        noProcessedNidNotFound += summary.getNotProcessedNidNotFount();
        notProcessedDuplicateNid += summary.getNotProcessedDuplicateNid();
        notProcessedFlaggedForReview += summary.getNotProcessedFlaggedForReview();
    }

    public void accumulate(ViralResultStatistics stats) {
        total += stats.getTotal();
        processed += stats.getProcessed();
        pending += stats.getPending();
        noProcessedNoResult += stats.getNoProcessedNoResult();
        noProcessedNidNotFound += stats.getNoProcessedNidNotFound();
        notProcessedDuplicateNid += stats.getNotProcessedDuplicateNid();
        notProcessedFlaggedForReview += stats.getNotProcessedFlaggedForReview();
    }

    public ViralResultStatistics combine(ViralResultStatistics other) {
        this.accumulate(other);
        return this;
    }

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

    public double getNotProcessedDuplicateNidPercentage() {
        return (double) notProcessedDuplicateNid / total;
    }

    public double getNotProcessedFlaggedForReviewPercentage() {
        return (double) notProcessedFlaggedForReview / total;
    }
}
