package com.starterpack.hashtag.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.regex.Pattern;

public class HashtagValidator implements ConstraintValidator<HashtagCheck, List<String>> {
    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣]+$");
    private static final int MAX_LENGTH = 20;

    @Override
    public void initialize(HashtagCheck constraintAnnotation) {}

    @Override
    public boolean isValid(List<String> hashtagNames,
            ConstraintValidatorContext constraintValidatorContext) {
        if (hashtagNames == null || hashtagNames.isEmpty()) {
            return true;
        }

        for (String hashtagName : hashtagNames) {
            if (hashtagName == null || hashtagName.isBlank()) {
                return false;
            }
            if (hashtagName.length() > MAX_LENGTH) {
                return false;
            }
            if (!PATTERN.matcher(hashtagName).matches()) {
                return false;
            }
        }

        return true;
    }
}
