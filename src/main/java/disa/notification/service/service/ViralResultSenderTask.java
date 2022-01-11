package disa.notification.service.service;

import disa.notification.service.service.interfaces.MailService;
import disa.notification.service.service.interfaces.ViralLoaderResult;
import disa.notification.service.service.interfaces.ViralLoaderService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${email.recipients}")
    private String recipients;

    //Todas as segundas feiras 8h
    //TODO: substituir o cron expression por "0 8 * * MON"
    @Scheduled(cron = "0 1/2 * * * *")
    public void sendViralResultReport() {
        log.info("Iniciando a task de Sincronizacao de Cargas virais");
        List<ViralLoaderResult> result =viralLoaderService.findViralLoadsFromLastWeek();
        if(!result.isEmpty()){
            sendViralLoads(result);
        }
    }

    private void sendViralLoads(List<ViralLoaderResult> result) {
        try {
            mailService.sendEmail(recipients, result);
        } catch (MessagingException| UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("Erro ao enviar relatorio de Cargas virais");
        }
        log.info("Relatorio de cargas virais enviado com sucesso!");
    }

}
