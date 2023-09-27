package io.github.douglasliebl.library.api.service;

import java.util.List;

public interface EmailService {

    void sendMail(String message, List<String> mailsList);
}
