package disa.notification.service.service.interfaces;

import disa.notification.service.entity.NotificationConfig;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface MailService {
    void sendEmail(NotificationConfig notificationConfig, final List<ViralLoaderResultSummary> viralLoaders, List<ViralLoaderResults> viralLoadResults, List<ViralLoaderResults> unsyncronizedViralLoadResults, List<PendingHealthFacilitySummary> pendingHealthFacilitySummaries) throws MessagingException, UnsupportedEncodingException;

}
