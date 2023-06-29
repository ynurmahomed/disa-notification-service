package disa.notification.service.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import disa.notification.service.entity.NotificationConfig;
import disa.notification.service.service.interfaces.LabLoaderService;
import disa.notification.service.service.interfaces.LabResultSummary;
import disa.notification.service.service.interfaces.LabResults;
import disa.notification.service.service.interfaces.MailService;
import disa.notification.service.service.interfaces.PendingHealthFacilitySummary;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LabResultSenderTask {
    private static final Logger log = LoggerFactory.getLogger(LabResultSenderTask.class);

    private final LabLoaderService labLoaderService;
    private final MailService mailService;

    @Scheduled(cron = "${task.cron}")
    public void sendLabResultReport() throws IOException {
        log.info("Iniciando a task de Sincronizacao de Cargas virais");
        log.info("A Compor Dados para envio");

        List<NotificationConfig> notificationConfigs = labLoaderService.findActive();
        
        for (NotificationConfig notificationConfig : notificationConfigs) {
            log.info(" A Sincronizar Dados da Provincia de {}", notificationConfig.getProvince());
            sendEmailForNotificationConfig(notificationConfig);
        }
    }
    
    private void sendEmailForNotificationConfig(NotificationConfig notificationConfig) {
        List<LabResultSummary> labResultSummary = labLoaderService.findLabSummaryResultsFromLastWeek(notificationConfig.getProvince());
        List<LabResults> labResults = labLoaderService.findLabResultsFromLastWeek(notificationConfig.getProvince());
        List<LabResults> pendingResultsForMoreThan2Days = labLoaderService.findLabResultsPendingMoreThan2Days(notificationConfig.getProvince());
        List<PendingHealthFacilitySummary> pendingHealthFacilitySummaries = labLoaderService.findPendingHealthFacilitySummary(notificationConfig.getProvince());

        try {
            if (!labResultSummary.isEmpty() || !pendingResultsForMoreThan2Days.isEmpty()) {
                mailService.sendEmail(notificationConfig, labResultSummary, labResults, pendingResultsForMoreThan2Days, pendingHealthFacilitySummaries);
            } else {
                mailService.sendNoResultsEmail(notificationConfig);
            }
        } catch (IOException | MessagingException e) {
            log.error("Erro ao enviar relatório de Cargas virais", e);
        }
    }
}
