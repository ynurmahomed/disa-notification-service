package disa.notification.service.service.impl;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import disa.notification.service.entity.NotificationConfig;
import disa.notification.service.service.interfaces.MailService;
import disa.notification.service.service.interfaces.PendingHealthFacilitySummary;
import disa.notification.service.service.interfaces.ViralLoaderResultSummary;
import disa.notification.service.service.interfaces.ViralLoaderResults;
import disa.notification.service.utils.DateInterval;
import disa.notification.service.utils.DateTimeUtils;
import disa.notification.service.utils.FileUtils;
import disa.notification.service.utils.MultipartUtil;
import disa.notification.service.utils.TemplateEngineUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    
    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);
    
    public static final String EMAIL_SUBJECT = "Relatório de Sincronização de cargas virais de %s a %s";
    
    private TemplateEngine templateEngine;

    @Value("${disa.notifier.rest.endpoint}") 
    private String disaNotifierEndPoint;
    
    @Override
    public void sendEmail(final NotificationConfig notificationConfig, final List<ViralLoaderResultSummary> viralLoaders, 
                            List<ViralLoaderResults> viralLoadResults, List<ViralLoaderResults> unsyncronizedViralLoadResults, 
                            List<PendingHealthFacilitySummary> pendingHealthFacilitySummaries) throws MessagingException, UnsupportedEncodingException {
        
        // Prepare the evaluation context
        final Context ctx = new Context(new Locale("pt", "BR"));
        DateInterval lastWeekInterval= DateTimeUtils.getLastWeekInterVal();
        String startDateFormatted=lastWeekInterval.getStartDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String endDateFormatted=lastWeekInterval.getEndDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        ctx.setVariable("fromDate",startDateFormatted );
        ctx.setVariable("toDate", endDateFormatted);
        ctx.setVariable("viralLoaders", viralLoaders);

        // Create the HTML body using Thymeleaf
        templateEngine = TemplateEngineUtils.getTemplateEngine();
        final String htmlContent = this.templateEngine.process("index", ctx);

        String attachmentName = "viral_Result_from_" + startDateFormatted + "_To_" + endDateFormatted + ".xlsx";
        String[] mailList = notificationConfig.getMailList().split(",");
        ByteArrayResource attachment = FileUtils.getViralResultXLS(viralLoaders, viralLoadResults, unsyncronizedViralLoadResults, pendingHealthFacilitySummaries);
        sendEmailHelper(mailList, htmlContent, attachment, "notification", attachmentName, startDateFormatted, endDateFormatted);
    }

    @Override
    public void sendNoResultsEmail(NotificationConfig notificationConfig)
            throws MessagingException, UnsupportedEncodingException {

        Context ctx = new Context(new Locale("pt", "BR"));
        DateInterval lastWeekInterval = DateTimeUtils.getLastWeekInterVal();
        String startDateFormatted = lastWeekInterval.getStartDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String endDateFormatted = lastWeekInterval.getEndDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        ctx.setVariable("fromDate", startDateFormatted);
        ctx.setVariable("toDate", endDateFormatted);

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
            logger.info("Email sent successfully");
        } else {
            logger.error("Failed to send email. Response code: " + emailResult.getStatusCode());
        }
    }

}
