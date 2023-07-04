package disa.notification.service.service.interfaces;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;

import disa.notification.service.entity.ImplementingPartner;

public interface MailService {
    void sendEmail(ImplementingPartner ip, final List<ViralLoaderResultSummary> viralLoaders,
            List<ViralLoaderResults> viralLoadResults, List<ViralLoaderResults> unsyncronizedViralLoadResults,
            List<PendingHealthFacilitySummary> pendingHealthFacilitySummaries)
            throws MessagingException, UnsupportedEncodingException;

    void sendNoResultsEmail(ImplementingPartner ip) throws MessagingException, UnsupportedEncodingException;
}
