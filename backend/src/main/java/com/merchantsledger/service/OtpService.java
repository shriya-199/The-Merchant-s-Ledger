package com.merchantsledger.service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.merchantsledger.dto.OtpChallengeResponse;
import com.merchantsledger.dto.OtpPurpose;
import com.merchantsledger.exception.BadRequestException;

@Service
public class OtpService {
  private final Map<String, Challenge> challenges = new ConcurrentHashMap<>();
  private final boolean exposeCodes;
  private final long expiryMinutes;

  public OtpService(@Value("${app.otp.exposeCodes:true}") boolean exposeCodes,
                    @Value("${app.otp.expiryMinutes:5}") long expiryMinutes) {
    this.exposeCodes = exposeCodes;
    this.expiryMinutes = expiryMinutes;
  }

  public OtpChallengeResponse sendPair(String email, String phone, OtpPurpose purpose) {
    cleanupExpired();
    Challenge emailChallenge = issueChallenge("EMAIL", email, purpose);
    Challenge phoneChallenge = issueChallenge("PHONE", phone, purpose);
    return new OtpChallengeResponse(
        emailChallenge.id(),
        phoneChallenge.id(),
        maskEmail(email),
        maskPhone(phone),
        expiryMinutes * 60,
        exposeCodes ? emailChallenge.code() : null,
        exposeCodes ? phoneChallenge.code() : null
    );
  }

  public OtpChallengeResponse sendPhoneOnly(String phone, OtpPurpose purpose) {
    cleanupExpired();
    Challenge phoneChallenge = issueChallenge("PHONE", phone, purpose);
    return new OtpChallengeResponse(
        null,
        phoneChallenge.id(),
        null,
        maskPhone(phone),
        expiryMinutes * 60,
        null,
        exposeCodes ? phoneChallenge.code() : null
    );
  }

  public void verify(String challengeId, String code, String target, OtpPurpose purpose, String channel) {
    if (challengeId == null || challengeId.isBlank() || code == null || code.isBlank()) {
      throw new BadRequestException(channel + " OTP and challenge are required");
    }
    cleanupExpired();
    Challenge challenge = challenges.get(challengeId);
    if (challenge == null) {
      throw new BadRequestException(channel + " OTP challenge not found or expired");
    }
    if (challenge.used()) {
      throw new BadRequestException(channel + " OTP already used");
    }
    if (!challenge.target().equalsIgnoreCase(target.trim())) {
      throw new BadRequestException(channel + " OTP target mismatch");
    }
    if (challenge.purpose() != purpose) {
      throw new BadRequestException(channel + " OTP purpose mismatch");
    }
    if (!challenge.channel().equals(channel)) {
      throw new BadRequestException(channel + " OTP channel mismatch");
    }
    if (challenge.expiresAt().isBefore(Instant.now())) {
      challenges.remove(challengeId);
      throw new BadRequestException(channel + " OTP expired");
    }
    if (!challenge.code().equals(code.trim())) {
      throw new BadRequestException("Invalid " + channel + " OTP");
    }
    challenges.put(challengeId, challenge.markUsed());
  }

  private Challenge issueChallenge(String channel, String target, OtpPurpose purpose) {
    if (target == null || target.isBlank()) {
      throw new BadRequestException(channel + " target is required");
    }
    String challengeId = UUID.randomUUID().toString();
    String code = generateCode();
    Challenge challenge = new Challenge(
        challengeId,
        channel,
        target.trim(),
        purpose,
        code,
        Instant.now().plusSeconds(expiryMinutes * 60),
        false
    );
    challenges.put(challengeId, challenge);
    return challenge;
  }

  private String generateCode() {
    int random = (int) (Math.random() * 900000) + 100000;
    return String.valueOf(random);
  }

  private void cleanupExpired() {
    Instant now = Instant.now();
    challenges.entrySet().removeIf(entry -> entry.getValue().expiresAt().isBefore(now));
  }

  private String maskEmail(String email) {
    int at = email.indexOf('@');
    if (at <= 1) {
      return "***" + email.substring(Math.max(0, at));
    }
    return email.substring(0, 1) + "***" + email.substring(at - 1);
  }

  private String maskPhone(String phone) {
    String trimmed = phone.trim();
    if (trimmed.length() <= 4) {
      return "****";
    }
    return "******" + trimmed.substring(trimmed.length() - 4);
  }

  private record Challenge(
      String id,
      String channel,
      String target,
      OtpPurpose purpose,
      String code,
      Instant expiresAt,
      boolean used
  ) {
    private Challenge markUsed() {
      return new Challenge(id, channel, target, purpose, code, expiresAt, true);
    }
  }
}
