package disa.notification.service.service.impl;

import disa.notification.service.entity.NotificationConfig;
import disa.notification.service.service.interfaces.MailService;
import disa.notification.service.service.interfaces.PendingHealthFacilitySummary;
import disa.notification.service.service.interfaces.ViralLoaderResultSummary;
import disa.notification.service.service.interfaces.ViralLoaderResults;
import disa.notification.service.utils.DateInterval;
import disa.notification.service.utils.DateTimeUtils;
import disa.notification.service.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    public static final String EMAIL_SUBJECT = "Relatório de Sincronização de cargas virais de %s a %s";
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendEmail(final NotificationConfig notificationConfig, final List<ViralLoaderResultSummary> viralLoaders, List<ViralLoaderResults> viralLoadResults, List<ViralLoaderResults> unsyncronizedViralLoadResults, List<PendingHealthFacilitySummary> pendingHealthFacilitySummaries) throws MessagingException, UnsupportedEncodingException {
        // Prepare the evaluation context
        final Context ctx = new Context(new Locale("pt", "BR"));
        DateInterval lastWeekInterval= DateTimeUtils.getLastWeekInterVal();
        String startDateFormatted=lastWeekInterval.getStartDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String endDateFormatted=lastWeekInterval.getEndDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        ctx.setVariable("fromDate",startDateFormatted );
        ctx.setVariable("toDate", endDateFormatted);
        ctx.setVariable("viralLoaders", viralLoaders);

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message =
                new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
        message.setSubject(String.format(EMAIL_SUBJECT,startDateFormatted,endDateFormatted));
        message.setFrom(fromEmail,"[DISA_SESP]");
        String [] mailList=notificationConfig.getMailList().split(",");
        message.setTo(mailList);

        // Create the HTML body using Thymeleaf
        final String htmlContent = this.templateEngine.process("index.html", ctx);
        message.setText(htmlContent, true); // true = isHtml

        String fileName="viral_Result_from_"+startDateFormatted+"_To_"+endDateFormatted+".xlsx";
        message.addAttachment(fileName, FileUtils.getViralResultXLS(viralLoaders,viralLoadResults,unsyncronizedViralLoadResults,pendingHealthFacilitySummaries));

        // Send mail
        this.mailSender.send(mimeMessage);
    }

    @Override
    public void sendNoResultsEmail(NotificationConfig notificationConfig)
            throws MessagingException, UnsupportedEncodingException {

        Context ctx = new Context(new Locale("pt", "BR"));
        DateInterval lastWeekInterval = DateTimeUtils.getLastWeekInterVal();
        String startDateFormatted = lastWeekInterval.getStartDateTime().toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String endDateFormatted = lastWeekInterval.getEndDateTime().toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        ctx.setVariable("fromDate", startDateFormatted);
        ctx.setVariable("toDate", endDateFormatted);

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
        message.setSubject(String.format(EMAIL_SUBJECT, startDateFormatted, endDateFormatted));
        message.setFrom(fromEmail, "[DISA_SESP]");
        String[] mailList = notificationConfig.getMailList().split(",");
        message.setTo(mailList);

        final String htmlContent = this.templateEngine.process("noResults.html", ctx);
        message.setText(htmlContent, true);

        this.mailSender.send(mimeMessage);

    }
}
