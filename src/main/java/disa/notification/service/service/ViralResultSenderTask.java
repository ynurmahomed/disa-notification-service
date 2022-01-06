package disa.notification.service.service;

import disa.notification.service.service.interfaces.MailService;
import disa.notification.service.service.interfaces.ViralLoaderResult;
import disa.notification.service.service.interfaces.ViralLoaderService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.util.List;


@Component
@AllArgsConstructor
public class ViralResultSenderTask {
    private static final Logger log = LoggerFactory.getLogger(ViralResultSenderTask.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final ViralLoaderService viralLoaderService;
    private final MailService mailService;

    @Scheduled(fixedDelay = 30000)
    public void sendViralResultReport() {
        log.info("A enviar relatorio de Sincronizacao de Cargas virais");
        List<ViralLoaderResult> result =viralLoaderService.findTop10ViralLoaders();
        try {
            mailService.sendEmail("Judiao Mbaua","judiao.mbaua@fgh.org.mz",result);
        } catch (MessagingException e) {
            e.printStackTrace();
            log.error("Erro ao enviar relatorio de Cargas virais");
        }
        log.info("Relatorio de cargas virais envoado com sucesso!");

    }

}
