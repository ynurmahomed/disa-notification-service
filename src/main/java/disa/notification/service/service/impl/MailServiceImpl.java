package disa.notification.service.service.impl;

import disa.notification.service.service.interfaces.MailService;
import disa.notification.service.service.interfaces.ViralLoaderResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    public static final String EMAIL_SUBJECT = "Relatorio de Sincronizacao de cargas virais";
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendEmail(final String recipientName, final String recipientEmail, final List<ViralLoaderResult> viralLoaders) throws MessagingException {
        // Prepare the evaluation context
        final Context ctx = new Context(new Locale("pt", "BR"));
        ctx.setVariable("name", recipientName);
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("viralLoaders", viralLoaders);
        ctx.setVariable("recipientName",recipientName);

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message =
                new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
        message.setSubject(EMAIL_SUBJECT);
        message.setFrom(fromEmail);
        message.setTo(recipientEmail);

        // Create the HTML body using Thymeleaf
        final String htmlContent = this.templateEngine.process("index.html", ctx);
        message.setText(htmlContent, true); // true = isHtml

        // Send mail
        this.mailSender.send(mimeMessage);
    }
}
