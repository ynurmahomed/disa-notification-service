package disa.notification.service.controller;

import disa.notification.service.service.interfaces.MailService;
import disa.notification.service.service.interfaces.ViralLoaderResult;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
public class MailSenderController {
   private final  MailService mailService;
    @PostMapping
    public String sendEmail(
          final String[] recipientEmail, final List<ViralLoaderResult> viralLoaders)
            throws MessagingException, IOException {
        this.mailService.sendEmail(recipientEmail, viralLoaders);
        return "redirect:sent.html";

    }
}
