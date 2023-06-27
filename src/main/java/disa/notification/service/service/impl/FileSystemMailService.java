package disa.notification.service.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.context.MessageSource;

import disa.notification.service.entity.NotificationConfig;
import disa.notification.service.service.interfaces.MailService;
import disa.notification.service.service.interfaces.PendingHealthFacilitySummary;
import disa.notification.service.service.interfaces.ViralLoaderResultSummary;
import disa.notification.service.service.interfaces.ViralLoaderResults;
import disa.notification.service.utils.DateInterval;
import disa.notification.service.utils.DateTimeUtils;
import disa.notification.service.utils.SyncReport;
import lombok.extern.log4j.Log4j2;

/**
 * A mail service that writes the email attachments to the file system.
 */
@Log4j2
public class FileSystemMailService implements MailService {

    private MessageSource messageSource;

    public FileSystemMailService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void sendEmail(NotificationConfig notificationConfig, List<ViralLoaderResultSummary> viralLoaders,
            List<ViralLoaderResults> viralLoadResults, List<ViralLoaderResults> unsyncronizedViralLoadResults,
            List<PendingHealthFacilitySummary> pendingHealthFacilitySummaries)
            throws MessagingException, UnsupportedEncodingException {

        try {
            DateInterval lastWeekInterval = DateTimeUtils.getLastWeekInterVal();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String start = formatter.format(lastWeekInterval.getStartDateTime());
            String end = formatter.format(lastWeekInterval.getEndDateTime());
            SyncReport syncReport = new SyncReport(messageSource);
            byte[] xls = syncReport.getViralResultXLS(viralLoaders, viralLoadResults, unsyncronizedViralLoadResults,
                    pendingHealthFacilitySummaries);
            Path path = Paths.get("viral_Result_from_" + start + "_To_" + end + ".xlsx");
            Files.write(path, xls);
            log.info("File writen to path {}", path.toAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendNoResultsEmail(NotificationConfig notificationConfig)
            throws MessagingException, UnsupportedEncodingException {
        log.info("No results to generate xls.");
    }

}
