package disa.notification.service.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import disa.notification.service.entity.NotificationConfig;
import disa.notification.service.service.interfaces.MailService;
import disa.notification.service.service.interfaces.PendingHealthFacilitySummary;
import disa.notification.service.service.interfaces.ViralLoaderResultSummary;
import disa.notification.service.service.interfaces.ViralLoaderResults;
import disa.notification.service.service.interfaces.ViralLoaderService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ViralResultSenderTask {
    private static final Logger log = LoggerFactory.getLogger(ViralResultSenderTask.class);
    private final ViralLoaderService viralLoaderService;
    private final MailService mailService;

    @Scheduled(cron = "${task.cron}")
    public void sendViralResultReport() {
        log.info("Iniciando a task de Sincronizacao de Cargas virais");
        log.info("A Compor Dados para envio");

        List<NotificationConfig> notificationConfigs = viralLoaderService.findActive();
        for (NotificationConfig notificationConfig : notificationConfigs) {
            log.info(" A Sincronizar Dados da Provincia de {}", notificationConfig.getProvince());
            List<ViralLoaderResultSummary> result = viralLoaderService
                    .findViralLoadsFromLastWeek(notificationConfig.getProvince());
            List<ViralLoaderResults> viralLoadResults = viralLoaderService
                    .findViralLoadResultsFromLastWeek(notificationConfig.getProvince());
            List<ViralLoaderResults> pendingFor2Days = viralLoaderService
                    .findViralLoadResultsPendingMoreThan2Days(notificationConfig.getProvince());
            List<PendingHealthFacilitySummary> pendingHealthFacilitySummaries = viralLoaderService
                    .findPendingHealthFacilitySummary(notificationConfig.getProvince());

            try {
                log.info("A enviar email...");
                if (!result.isEmpty() || !pendingFor2Days.isEmpty()) {
                    mailService.sendEmail(notificationConfig, result, viralLoadResults, pendingFor2Days,
                            pendingHealthFacilitySummaries);
                } else {
                    mailService.sendNoResultsEmail(notificationConfig);
                }
                log.info("Relatório de cargas virais enviado com sucesso!");
            } catch (UnsupportedEncodingException | MessagingException e) {
                log.error("Erro ao enviar relatório de Cargas virais", e);
            }
        }
    }
}
