package disa.notification.service.utils;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import disa.notification.service.entity.ViralResultStatistics;
import disa.notification.service.service.interfaces.ViralLoaderResultSummary;

public class ViralResultStatisticsCollector
        implements Collector<ViralLoaderResultSummary, ViralResultStatistics, ViralResultStatistics> {

    public static ViralResultStatisticsCollector toVlResultStatistics() {
        return new ViralResultStatisticsCollector();
    }

    @Override
    public BiConsumer<ViralResultStatistics, ViralLoaderResultSummary> accumulator() {
        return (stats, summary) -> {
            stats.setTotal(stats.getTotal() + summary.getTotalReceived());
            stats.setProcessed(stats.getProcessed() + summary.getProcessed());
            stats.setPending(stats.getPending() + summary.getTotalPending());
            stats.setNoProcessedNoResult(stats.getNoProcessedNoResult() + summary.getNotProcessedNoResult());
            stats.setNoProcessedNidNotFound(stats.getNoProcessedNidNotFound() + summary.getNotProcessedNidNotFount());
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED);
    }

    @Override
    public BinaryOperator<ViralResultStatistics> combiner() {
        return (stats, other) -> {
            stats.setTotal(stats.getTotal() + other.getTotal());
            stats.setProcessed(stats.getProcessed() + other.getProcessed());
            stats.setPending(stats.getPending() + other.getPending());
            stats.setNoProcessedNoResult(stats.getNoProcessedNoResult() + other.getNoProcessedNoResult());
            stats.setNoProcessedNidNotFound(stats.getNoProcessedNidNotFound() + other.getNoProcessedNidNotFound());
            return stats;
        };
    }

    @Override
    public Function<ViralResultStatistics, ViralResultStatistics> finisher() {
        return Function.identity();
    }

    @Override
    public Supplier<ViralResultStatistics> supplier() {
        return ViralResultStatistics::new;
    }
}
