package io.github.douglasliebl.msbooks.api.service;

import io.github.douglasliebl.msbooks.api.model.entity.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledService {

    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";
    @Value("${app.mail.late-loans.message}")
    private String message;

    private final LoanService loanService;
    private final EmailService emailService;

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendMailToLateLoans() {
        List<Loan> allLateLoans = loanService.getAllLateLoans();
        List<String> mailsList = allLateLoans.stream()
                .map(Loan::getEmail)
                .toList();

        emailService.sendMail(message, mailsList);

    }
}
