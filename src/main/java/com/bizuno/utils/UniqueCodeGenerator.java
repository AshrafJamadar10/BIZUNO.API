package com.bizuno.utils;

import java.util.UUID;

public class UniqueCodeGenerator {
    public static String generateShortUuid() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 4).toUpperCase() + uuid.substring(20, 22); // 4 letters + 2 numbers
    }
}
