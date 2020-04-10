package com.cds.mini.batch.user;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;

@Component
public class UserSkipPolicy implements SkipPolicy {
    private static final int MAX_SKIP = 10;
    private final boolean skipEnabled;

    public UserSkipPolicy(@Value("${user.batch.skip.enabled}") boolean skipEnabled) {
        this.skipEnabled = skipEnabled;
    }

    @Override
    public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {
        return skipEnabled && ((t instanceof FlatFileParseException || t instanceof ValidationException) && skipCount < MAX_SKIP);
    }

}
