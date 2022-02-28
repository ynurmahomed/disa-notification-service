package disa.notification.service.service.interfaces;

import disa.notification.service.entity.NotificationConfig;
import disa.notification.service.entity.PendingViralResultSummary;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface MailService {
    void sendEmail(NotificationConfig notificationConfig, final List<ViralLoaderResultSummary> viralLoaders, List<ViralLoaderResults> viralLoadResults, List<ViralLoaderResults> unsyncronizedViralLoadResults, List<PendingViralResultSummary> pendingHealthFacilitySummaries) throws MessagingException, UnsupportedEncodingException;

}
