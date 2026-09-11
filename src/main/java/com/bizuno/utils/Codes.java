package com.bizuno.utils;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class Codes {

    public String generateBusinessCode(String schoolName) {
        // 1️⃣ Extract meaningful acronym (max 3–4 letters)
        String acronym = Arrays.stream(schoolName.trim().split("\\s+"))
                .filter(word -> word.length() > 2)      // skip small words like "of", "the"
                .limit(3)
                .map(word -> word.substring(0, 1).toUpperCase())
                .collect(Collectors.joining());

        if (acronym.isBlank()) {
            acronym = schoolName.substring(0, Math.min(3, schoolName.length())).toUpperCase();
        }

        // 2️⃣ SmartAcad essence
        String productCode = "SA";

        // 3️⃣ Random 3-digit number for uniqueness
        int random = ThreadLocalRandom.current().nextInt(100, 999);

        return acronym + "_" + productCode + "_" + random;
    }
}

