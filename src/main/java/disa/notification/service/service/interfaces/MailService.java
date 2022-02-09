package disa.notification.service.service.interfaces;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface MailService {
    void sendEmail(final String[] recipientEmails, final List<ViralLoaderResultSummary> viralLoaders, List<ViralLoaderResults> viralLoadResults, List<ViralLoaderResults> unsyncronizedViralLoadResults) throws MessagingException, UnsupportedEncodingException;

}
