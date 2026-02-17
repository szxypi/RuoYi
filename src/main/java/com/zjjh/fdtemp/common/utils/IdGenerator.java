package com.zjjh.fdtemp.common.utils;

import java.util.UUID;

public class IdGenerator {
    /**
     * 生成32位小写无横线UUID
     */
    public static String nextId() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
