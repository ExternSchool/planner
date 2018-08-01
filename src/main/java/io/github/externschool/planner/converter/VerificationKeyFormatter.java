package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.service.VerificationKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Locale;

@Service
public class VerificationKeyFormatter implements Formatter<VerificationKey> {
    @Autowired private VerificationKeyService service;

    @Override
    public VerificationKey parse(final String s, final Locale locale) throws ParseException {
        return service.findKeyByValue(s);
    }

    @Override
    public String print(final VerificationKey key, final Locale locale) {
        return key.getValue();
    }
}
