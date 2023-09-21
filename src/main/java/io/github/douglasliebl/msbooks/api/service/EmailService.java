package io.github.douglasliebl.msbooks.api.service;

import java.util.List;

public interface EmailService {

    void sendMail(String message, List<String> mailsList);
}
