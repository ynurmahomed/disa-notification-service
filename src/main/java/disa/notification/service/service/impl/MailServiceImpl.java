package disa.notification.service.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import disa.notification.service.entity.ImplementingPartner;
import disa.notification.service.service.interfaces.MailService;
import disa.notification.service.service.interfaces.PendingHealthFacilitySummary;
import disa.notification.service.service.interfaces.LabResultSummary;
import disa.notification.service.service.interfaces.LabResults;
import disa.notification.service.utils.DateInterval;
import disa.notification.service.utils.DateTimeUtils;
import disa.notification.service.utils.MultipartUtil;
import disa.notification.service.utils.SyncReport;
import disa.notification.service.utils.TemplateEngineUtils;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MailServiceImpl implements MailService {

    public static final String EMAIL_SUBJECT = "Relatório de Sincronização de resultados lab de %s a %s";
    
    private final JavaMailSender mailSender;
    private TemplateEngine templateEngine;
    private final MessageSource messageSource;
    
    public MailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine, MessageSource messageSource) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
    }

    @Value("${spring.mail.username}")
    private String fromEmail;

<<<<<<< HEAD
    @Value("${disa.notifier.rest.endpoint}") 
    private String disaNotifierEndPoint;
    
    public void sendEmail(final NotificationConfig notificationConfig,
            final List<LabResultSummary> viralLoaders, List<LabResults> viralLoadResults,
            List<LabResults> unsyncronizedViralLoadResults,
=======
    @Override
    public void sendEmail(final ImplementingPartner ip,
            final List<ViralLoaderResultSummary> viralLoaders, List<ViralLoaderResults> viralLoadResults,
            List<ViralLoaderResults> unsyncronizedViralLoadResults,
>>>>>>> b36528ed97a45389f0024a50486fb2f43865c6cc
            List<PendingHealthFacilitySummary> pendingHealthFacilitySummaries)
            throws MessagingException, IOException {
    	
        // Prepare the evaluation context
        final Context ctx = new Context(new Locale("pt", "BR"));
        DateInterval lastWeekInterval = DateTimeUtils.getLastWeekInterVal();
        String startDateFormatted = lastWeekInterval.getStartDateTime().toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String endDateFormatted = lastWeekInterval.getEndDateTime().toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        ctx.setVariable("fromDate", startDateFormatted);
        ctx.setVariable("toDate", endDateFormatted);
        ctx.setVariable("viralLoaders", viralLoaders);

<<<<<<< HEAD
=======
        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
        message.setSubject(String.format(EMAIL_SUBJECT, startDateFormatted, endDateFormatted));
        message.setFrom(fromEmail, "[DISA_SESP]");
        String[] mailList = ip.getMailListItems();
        message.setTo(mailList);

>>>>>>> b36528ed97a45389f0024a50486fb2f43865c6cc
        // Create the HTML body using Thymeleaf
        templateEngine = TemplateEngineUtils.getTemplateEngine();
        final String htmlContent = this.templateEngine.process("index", ctx);

        String attachmentName = "Lab_Results_from_" + startDateFormatted + "_To_" + endDateFormatted + ".xlsx";
        SyncReport syncReport = new SyncReport(messageSource);
        String[] mailList = notificationConfig.getMailList().split(",");
        ByteArrayResource attachment = syncReport.getViralResultXLS(viralLoaders, viralLoadResults, unsyncronizedViralLoadResults, pendingHealthFacilitySummaries);
        sendEmailHelper(mailList, htmlContent, attachment, "notification", attachmentName, startDateFormatted, endDateFormatted);
    }

    @Override
    public void sendNoResultsEmail(ImplementingPartner ip)
            throws MessagingException, UnsupportedEncodingException {

        Context ctx = new Context(new Locale("pt", "BR"));
        DateInterval lastWeekInterval = DateTimeUtils.getLastWeekInterVal();
        String startDateFormatted = lastWeekInterval.getStartDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String endDateFormatted = lastWeekInterval.getEndDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        ctx.setVariable("fromDate", startDateFormatted);
        ctx.setVariable("toDate", endDateFormatted);

<<<<<<< HEAD
        String[] mailList = notificationConfig.getMailList().split(",");
        templateEngine = TemplateEngineUtils.getTemplateEngine();
        final String htmlContent = this.templateEngine.process("noResults", ctx);
        sendEmailHelper(mailList, htmlContent, null, "notification", null, startDateFormatted, endDateFormatted);
    }
    
    private void sendEmailHelper(String[] mailList, String htmlContent, ByteArrayResource attachment, String module, String attachmentName, String startDateFormatted, String endDateFormatted) {
    	
        String subject = String.format(EMAIL_SUBJECT, startDateFormatted, endDateFormatted);
        byte[] byteArray = null;
        
        ResponseEntity<String> emailResult = null;
        if (attachment!=null) { 
        	byteArray = attachment.getByteArray();
        }
        
		try {
			emailResult = MultipartUtil.sendMultipartRequest(disaNotifierEndPoint, mailList, 
					subject, htmlContent, byteArray, module, attachmentName, startDateFormatted, endDateFormatted);
		} catch (IOException e) {e.printStackTrace();}
        
        if (emailResult.getStatusCode().is2xxSuccessful()) {
            log.info("Email sent successfully");
        } else {
        	log.error("Failed to send email. Response code: " + emailResult.getStatusCode());
        }
=======
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
        message.setSubject(String.format(EMAIL_SUBJECT, startDateFormatted, endDateFormatted));
        message.setFrom(fromEmail, "[DISA_SESP]");
        String[] mailList = ip.getMailListItems();
        message.setTo(mailList);

        final String htmlContent = this.templateEngine.process("noResults.html", ctx);
        message.setText(htmlContent, true);

        this.mailSender.send(mimeMessage);

>>>>>>> b36528ed97a45389f0024a50486fb2f43865c6cc
    }
}
