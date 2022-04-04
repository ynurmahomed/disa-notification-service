package disa.notification.service.service;

import disa.notification.service.entity.NotificationConfig;
import disa.notification.service.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;


@Component
@RequiredArgsConstructor
public class ViralResultSenderTask {
    private static final Logger log = LoggerFactory.getLogger(ViralResultSenderTask.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final ViralLoaderService viralLoaderService;
    private final MailService mailService;

    //Todas as segundas feiras as 8h
    @Scheduled(cron = "0 0 8 * * MON")
    public void sendViralResultReport() {
        log.info("Iniciando a task de Sincronizacao de Cargas virais");
        log.info("A Compor Dados para envio");

        List<NotificationConfig> notificationConfigs=viralLoaderService.findActive();
        for (NotificationConfig notificationConfig: notificationConfigs) {
            log.info(" A Sincronizar Dados da Provincia de {}",notificationConfig.getProvince());
            List<ViralLoaderResultSummary> result =viralLoaderService.findViralLoadsFromLastWeek(notificationConfig.getProvince());
            List<ViralLoaderResults> viralLoadResults=viralLoaderService.findViralLoadResultsFromLastWeek(notificationConfig.getProvince());
            List<ViralLoaderResults> unsyncronizedViralLoadResults =viralLoaderService.findUnsyncronizedViralResults(notificationConfig.getProvince());
            List<PendingHealthFacilitySummary> pendingHealthFacilitySummaries=viralLoaderService.findPendingHealthFacilitySummary(notificationConfig.getProvince());

            if( !result.isEmpty() || !unsyncronizedViralLoadResults.isEmpty() ){
                log.info("A enviar email...");
                sendViralLoads(notificationConfig,result, viralLoadResults,unsyncronizedViralLoadResults,pendingHealthFacilitySummaries);
            }


        }

    }

    private void sendViralLoads(NotificationConfig notificationConfig, List<ViralLoaderResultSummary> result, List<ViralLoaderResults> viralLoadResults, List<ViralLoaderResults> unsyncronizedViralLoadResults, List<PendingHealthFacilitySummary> pendingHealthFacilitySummaries) {
        try {
            mailService.sendEmail(notificationConfig, result,viralLoadResults,unsyncronizedViralLoadResults,pendingHealthFacilitySummaries);
        } catch (MessagingException| UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("Erro ao enviar relatorio de Cargas virais");
        }
        log.info("Relatorio de cargas virais enviado com sucesso!");
    }

}
