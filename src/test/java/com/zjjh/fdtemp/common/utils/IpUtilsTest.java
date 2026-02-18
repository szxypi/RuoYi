package com.zjjh.fdtemp.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("IpUtils 测试")
class IpUtilsTest {

    @Test
    @DisplayName("textToNumericFormatV4 - null 输入返回 null")
    void testTextToNumericFormatV4_NullInput() {
        byte[] result = assertDoesNotThrow(() -> IpUtils.textToNumericFormatV4(null));
        assertNull(result);
    }

    @Test
    @DisplayName("internalIp - null 输入不抛异常")
    void testInternalIp_NullInput() {
        boolean internal = assertDoesNotThrow(() -> IpUtils.internalIp(null));
        assertTrue(internal);
    }

    @Test
    @DisplayName("getMultistageReverseProxyIp - 取首个非 unknown 地址")
    void testGetMultistageReverseProxyIp_FirstKnown() {
        String ip = IpUtils.getMultistageReverseProxyIp("unknown,10.0.0.1,1.1.1.1");
        assertEquals("10.0.0.1", ip);
    }
}
