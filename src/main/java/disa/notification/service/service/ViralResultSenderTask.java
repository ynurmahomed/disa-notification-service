package disa.notification.service.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import disa.notification.service.entity.ImplementingPartner;
import disa.notification.service.repository.ImplementingPartnerRepository;
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
    private final ImplementingPartnerRepository ipRepository;
    private final MailService mailService;

    @Scheduled(cron = "${task.cron}")
    public void sendViralResultReport() {
        log.info("Iniciando a task de Sincronizacao de Cargas virais");
        log.info("A Compor Dados para envio");

        List<ImplementingPartner> implementingPartners = ipRepository.findByEnabledTrue();
        for (ImplementingPartner ip : implementingPartners) {
            log.info(" A Sincronizar Dados de {}", ip.getOrgName());
            List<ViralLoaderResultSummary> result = viralLoaderService
                    .findViralLoadsFromLastWeek(ip);
            List<ViralLoaderResults> viralLoadResults = viralLoaderService
                    .findViralLoadResultsFromLastWeek(ip);
            List<ViralLoaderResults> pendingFor2Days = viralLoaderService
                    .findViralLoadResultsPendingMoreThan2Days(ip);
            List<PendingHealthFacilitySummary> pendingHealthFacilitySummaries = viralLoaderService
                    .findPendingHealthFacilitySummary(ip);

            try {
                if (!result.isEmpty() || !pendingFor2Days.isEmpty()) {
                    mailService.sendEmail(ip, result, viralLoadResults, pendingFor2Days,
                            pendingHealthFacilitySummaries);
                } else {
                    mailService.sendNoResultsEmail(ip);
                }
            } catch (UnsupportedEncodingException | MessagingException e) {
                log.error("Erro ao enviar relatório de Cargas virais", e);
            }
        }
    }
}
