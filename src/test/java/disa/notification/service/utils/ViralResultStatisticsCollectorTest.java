package disa.notification.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;

import org.junit.jupiter.api.Test;

import disa.notification.service.entity.ViralResultStatistics;
import disa.notification.service.service.interfaces.ViralLoaderResultSummary;

public class ViralResultStatisticsCollectorTest {

    @Test
    public void testAccumulator() {
        // Create a ViralResultStatisticsCollector instance
        ViralResultStatisticsCollector collector = new ViralResultStatisticsCollector();

        // Create a test summary
        ViralLoaderResultSummary summary = VlResultSummaryImpl.builder()
                .totalReceived(100)
                .processed(50)
                .totalPending(30)
                .notProcessedNoResult(15)
                .notProcessedNidNotFount(5)
                .build();

        // Create an instance of ViralResultStatistics
        ViralResultStatistics statistics = new ViralResultStatistics();

        // Get the accumulator BiConsumer from the ViralResultStatisticsCollector instance
        BiConsumer<ViralResultStatistics, ViralLoaderResultSummary> accumulator = collector.accumulator();

        // Use the accumulator to add the test summary to the statistics
        accumulator.accept(statistics, summary);

        // Assert that the summary data was added correctly
        assertThat(statistics.getTotal()).isEqualTo(100);
        assertThat(statistics.getProcessed()).isEqualTo(50);
        assertThat(statistics.getPending()).isEqualTo(30);
        assertThat(statistics.getNoProcessedNoResult()).isEqualTo(15);
        assertThat(statistics.getNoProcessedNidNotFound()).isEqualTo(5);
    }

    @Test
    public void testCombiner() {
        // Create a ViralResultStatisticsCollector instance
        ViralResultStatisticsCollector collector = new ViralResultStatisticsCollector();

        // Create some test ViralResultStatistics
        ViralResultStatistics statistics1 = ViralResultStatistics.builder()
                .total(100)
                .processed(50)
                .pending(30)
                .noProcessedNoResult(15)
                .noProcessedNidNotFound(5)
                .build();

        ViralResultStatistics statistics2 = ViralResultStatistics.builder()
                .total(50)
                .processed(20)
                .pending(10)
                .noProcessedNoResult(5)
                .noProcessedNidNotFound(2)
                .build();

        // Get the combiner BinaryOperator from the ViralResultStatisticsCollector
        // instance
        BinaryOperator<ViralResultStatistics> combiner = collector.combiner();

        // Use the combiner to combine the two statistics instances
        ViralResultStatistics combined = combiner.apply(statistics1, statistics2);

        // Assert that the summary data was added correctly
        assertThat(combined.getTotal()).isEqualTo(150);
        assertThat(combined.getProcessed()).isEqualTo(70);
        assertThat(combined.getPending()).isEqualTo(40);
        assertThat(combined.getNoProcessedNoResult()).isEqualTo(20);
        assertThat(combined.getNoProcessedNidNotFound()).isEqualTo(7);

    }
}
