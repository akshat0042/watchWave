package org.learn.watchwave.auth.service.interfaces;

public interface EmailVerificationService {

    void verifyEmail(String token);

    void resendVerification(String email);
}
