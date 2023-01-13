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
    private int notProcessedDuplicatedReqId;

    public void accumulate(ViralLoaderResultSummary summary) {
        total += summary.getTotalReceived();
        processed += summary.getProcessed();
        pending += summary.getTotalPending();
        noProcessedNoResult += summary.getNotProcessedNoResult();
        noProcessedNidNotFound += summary.getNotProcessedNidNotFount();
        notProcessedDuplicateNid += summary.getNotProcessedDuplicateNid();
        notProcessedFlaggedForReview += summary.getNotProcessedFlaggedForReview();
        notProcessedDuplicatedReqId += summary.getNotProcessedDuplicatedRequestId();
    }

    public void accumulate(ViralResultStatistics stats) {
        total += stats.getTotal();
        processed += stats.getProcessed();
        pending += stats.getPending();
        noProcessedNoResult += stats.getNoProcessedNoResult();
        noProcessedNidNotFound += stats.getNoProcessedNidNotFound();
        notProcessedDuplicateNid += stats.getNotProcessedDuplicateNid();
        notProcessedFlaggedForReview += stats.getNotProcessedFlaggedForReview();
        notProcessedDuplicatedReqId += stats.getNotProcessedDuplicatedReqId();
    }

    public ViralResultStatistics combine(ViralResultStatistics other) {
        this.accumulate(other);
        return this;
    }

    public double getProcessedPercentage() {
        return total == 0 ? 0.0 : (double) processed / total;
    }

    public double getPendingPercentage() {
        return total == 0 ? 0.0 : (double) pending / total;
    }

    public double getNoProcessedNoResultPercentage() {
        return total == 0 ? 0.0 : (double) noProcessedNoResult / total;
    }

    public double getNoProcessedNidNotFoundPercentage() {
        return total == 0 ? 0.0 : (double) noProcessedNidNotFound / total;
    }

    public double getNotProcessedDuplicateNidPercentage() {
        return total == 0 ? 0.0 : (double) notProcessedDuplicateNid / total;
    }

    public double getNotProcessedFlaggedForReviewPercentage() {
        return total == 0 ? 0.0 : (double) notProcessedFlaggedForReview / total;
    }

    public double getNotProcessedDuplicatedReqIdPercentage() {
        return total == 0 ? 0.0 : (double) notProcessedDuplicatedReqId / total;
    }
}
