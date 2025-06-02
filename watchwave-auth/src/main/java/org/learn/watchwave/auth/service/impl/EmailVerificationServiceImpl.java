package org.learn.watchwave.auth.service.impl;

import org.learn.watchwave.auth.model.entity.User;
import org.learn.watchwave.auth.repository.UserRepository;
import org.learn.watchwave.auth.service.interfaces.EmailVerificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final UserRepository userRepository;

    public EmailVerificationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        user.setVerified(true);
        user.setVerificationToken(null);
        user.setUpdatedAt(Timestamp.from(Instant.now()));

        userRepository.save(user);
    }

    @Override
    public void resendVerification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isVerified()) {
            throw new RuntimeException("Email is already verified");
        }

        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setUpdatedAt(Timestamp.from(Instant.now()));

        userRepository.save(user);
    }
}
