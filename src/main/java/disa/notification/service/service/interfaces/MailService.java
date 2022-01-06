package disa.notification.service.service.interfaces;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Locale;

public interface MailService {
    void sendEmail(final String recipientName, final String recipientEmail, final List<ViralLoaderResult> viralLoaders) throws MessagingException;

}
