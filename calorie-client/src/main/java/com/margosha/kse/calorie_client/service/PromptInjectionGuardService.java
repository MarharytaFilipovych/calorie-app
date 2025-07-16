package com.margosha.kse.calorie_client.service;

import com.margosha.kse.calorie_client.config.PromptInjectionGuardSettings;
import com.margosha.kse.calorie_client.model.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
@Slf4j
public class PromptInjectionGuardService {

    private final Pattern commandInjectionRegex;
    private final Pattern roleInjectionRegex;
    private final PromptInjectionGuardSettings settings;

    public PromptInjectionGuardService(PromptInjectionGuardSettings settings) {
        this.settings = settings;
        this.commandInjectionRegex = Pattern.compile(
                settings.getRegex().getCommandInjection(),
                Pattern.CASE_INSENSITIVE
        );
        this.roleInjectionRegex = Pattern.compile(
                settings.getRegex().getRoleInjection(),
                Pattern.CASE_INSENSITIVE
        );
    }

    public ValidationResult validateInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ValidationResult(false, "Empty input", 1.0);
        }

        String normalized = input.toLowerCase().trim();
        double risk = 0.0;
        StringBuilder reasons = new StringBuilder();

        for (String keyword : settings.getSuspiciousPatterns()) {
            if (normalized.contains(keyword.toLowerCase())) {
                risk += getWeight("suspicious-keyword");
                reasons.append("Contains suspicious keyword: ").append(keyword).append("; ");
                log.warn("Suspicious pattern detected: {}", keyword);
            }
        }

        if (commandInjectionRegex.matcher(input).find()) {
            risk += getWeight("command-injection");
            reasons.append("Command injection pattern detected; ");
            log.warn("Command injection pattern detected");
        }

        if (roleInjectionRegex.matcher(input).find()) {
            risk += getWeight("role-injection");
            reasons.append("Role injection pattern detected; ");
            log.warn("Role injection pattern detected");
        }

        if (input.length() > settings.getThresholds().getLengthLimit()) {
            risk += getWeight("too-long");
            reasons.append("Input too long; ");
        }

        if (hasExcessiveRepetition(normalized)) {
            risk += getWeight("excessive-repetition");
            reasons.append("Excessive repetition detected; ");
        }

        if (hasEncodingAttempts(input)) {
            risk += getWeight("encoding-attempt");
            reasons.append("Encoding attempt detected; ");
        }

        boolean safe = risk < settings.getThresholds().getSafeRiskScoreBoundary();
        if (!safe) {
            log.warn("Input blocked with risk score: {} - {}", risk, reasons);
        }
        return new ValidationResult(safe, !reasons.isEmpty() ? reasons.toString() : "Input appears safe", risk);
    }

    private double getWeight(String key) {
        return settings.getWeights().getOrDefault(key, 0.0);
    }

    private boolean hasExcessiveRepetition(String input) {
        var rep = settings.getThresholds().getRepetition();
        if (input.length() < rep.getMinLength()) return false;
        int chunk = rep.getChunkSize();
        int max = rep.getMaxCount();

        for (int i = 0; i <= input.length() - chunk; i++) {
            String sub = input.substring(i, i + chunk);
            int count = 0;
            int index = input.indexOf(sub);
            while (index != -1) {
                count++;
                if (count > max) return true;
                index = input.indexOf(sub, index + 1);
            }
        }
        return false;
    }

    private boolean hasEncodingAttempts(String input) {
        return input.contains("%") || input.contains("\\x") ||
                input.contains("\\u") || input.contains("&#");
    }

    public String sanitizeInput(String input) {
        if (input == null) return "";
        return input
                .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "")
                .replaceAll("\\\\[nrt]", " ")
                .replaceAll("[`${}]", "")
                .trim();
    }
}
