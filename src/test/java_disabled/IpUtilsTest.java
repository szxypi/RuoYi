package com.zjjh.fdtemp.common.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * IpUtils IP工具类单元测试
 */
@DisplayName("IpUtils IP工具类测试")
class IpUtilsTest {

    @Nested
    @DisplayName("getIpAddr 测试")
    class GetIpAddrTest {
        @Test
        @DisplayName("当request为null时返回unknown")
        void getIpAddr_WhenRequestIsNull_ShouldReturnUnknown() {
            assertEquals("unknown", IpUtils.getIpAddr(null));
        }

        @Test
        @DisplayName("从X-Forwarded-For获取IP")
        void getIpAddr_WhenXForwardedFor_ShouldReturnIp() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getHeader("x-forwarded-for")).thenReturn("192.168.1.1");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");

            String result = IpUtils.getIpAddr(request);
            assertEquals("192.168.1.1", result);
        }

        @Test
        @DisplayName("从Proxy-Client-IP获取IP")
        void getIpAddr_WhenProxyClientIP_ShouldReturnIp() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getHeader("x-forwarded-for")).thenReturn(null);
            when(request.getHeader("Proxy-Client-IP")).thenReturn("192.168.1.2");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");

            String result = IpUtils.getIpAddr(request);
            assertEquals("192.168.1.2", result);
        }

        @Test
        @DisplayName("从X-Real-IP获取IP")
        void getIpAddr_WhenXRealIP_ShouldReturnIp() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getHeader("x-forwarded-for")).thenReturn("unknown");
            when(request.getHeader("Proxy-Client-IP")).thenReturn("unknown");
            when(request.getHeader("X-Forwarded-For")).thenReturn("unknown");
            when(request.getHeader("WL-Proxy-Client-IP")).thenReturn("unknown");
            when(request.getHeader("X-Real-IP")).thenReturn("192.168.1.3");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");

            String result = IpUtils.getIpAddr(request);
            assertEquals("192.168.1.3", result);
        }

        @Test
        @DisplayName("从RemoteAddr获取IP")
        void getIpAddr_WhenRemoteAddr_ShouldReturnIp() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getHeader(anyString())).thenReturn(null);
            when(request.getRemoteAddr()).thenReturn("192.168.1.4");

            String result = IpUtils.getIpAddr(request);
            assertEquals("192.168.1.4", result);
        }

        @Test
        @DisplayName("IPv6本地地址转换为IPv4")
        void getIpAddr_WhenIPv6Localhost_ShouldConvertToIPv4() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getHeader(anyString())).thenReturn(null);
            when(request.getRemoteAddr()).thenReturn("0:0:0:0:0:0:0:1");

            String result = IpUtils.getIpAddr(request);
            assertEquals("127.0.0.1", result);
        }

        @Test
        @DisplayName("多级代理获取第一个非unknownIP")
        void getIpAddr_WhenMultipleProxy_ShouldReturnFirstValidIp() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getHeader("x-forwarded-for")).thenReturn("unknown, 192.168.1.5, 192.168.1.6");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");

            String result = IpUtils.getIpAddr(request);
            assertEquals("192.168.1.5", result);
        }
    }

    @Nested
    @DisplayName("internalIp 内网IP检测测试")
    class InternalIpTest {
        @Test
        @DisplayName("10.x.x.x是内网IP")
        void internalIp_When10Range_ShouldReturnTrue() {
            assertTrue(IpUtils.internalIp("10.0.0.1"));
            assertTrue(IpUtils.internalIp("10.255.255.255"));
        }

        @Test
        @DisplayName("172.16.x.x-172.31.x.x是内网IP")
        void internalIp_When172Range_ShouldReturnTrue() {
            assertTrue(IpUtils.internalIp("172.16.0.1"));
            assertTrue(IpUtils.internalIp("172.31.255.255"));
        }

        @Test
        @DisplayName("192.168.x.x是内网IP")
        void internalIp_When192Range_ShouldReturnTrue() {
            assertTrue(IpUtils.internalIp("192.168.0.1"));
            assertTrue(IpUtils.internalIp("192.168.255.255"));
        }

        @Test
        @DisplayName("127.0.0.1是内网IP")
        void internalIp_WhenLocalhost_ShouldReturnTrue() {
            assertTrue(IpUtils.internalIp("127.0.0.1"));
        }

        @Test
        @DisplayName("公网IP不是内网IP")
        void internalIp_WhenPublicIp_ShouldReturnFalse() {
            assertFalse(IpUtils.internalIp("8.8.8.8"));
            assertFalse(IpUtils.internalIp("114.114.114.114"));
        }
    }

    @Nested
    @DisplayName("textToNumericFormatV4 IP转换测试")
    class TextToNumericFormatV4Test {
        @Test
        @DisplayName("标准IPv4地址转换")
        void textToNumericFormatV4_WhenValidIPv4_ShouldReturnBytes() {
            byte[] result = IpUtils.textToNumericFormatV4("192.168.1.1");
            assertNotNull(result);
            assertEquals(4, result.length);
        }

        @Test
        @DisplayName("空字符串返回null")
        void textToNumericFormatV4_WhenEmpty_ShouldReturnNull() {
            assertNull(IpUtils.textToNumericFormatV4(""));
        }

        @Test
        @DisplayName("无效IP返回null")
        void textToNumericFormatV4_WhenInvalid_ShouldReturnNull() {
            assertNull(IpUtils.textToNumericFormatV4("invalid"));
            assertNull(IpUtils.textToNumericFormatV4("256.256.256.256"));
        }
    }

    @Nested
    @DisplayName("getHostIp/getHostName 测试")
    class GetHostInfoTest {
        @Test
        @DisplayName("获取本机IP")
        void getHostIp_ShouldReturnValidIp() {
            String result = IpUtils.getHostIp();
            assertNotNull(result);
            assertTrue(IpUtils.isIP(result) || result.equals("127.0.0.1"));
        }

        @Test
        @DisplayName("获取主机名")
        void getHostName_ShouldReturnValidName() {
            String result = IpUtils.getHostName();
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("getMultistageReverseProxyIp 多级代理测试")
    class GetMultistageReverseProxyIpTest {
        @Test
        @DisplayName("多IP时返回第一个非unknownIP")
        void getMultistageReverseProxyIp_WhenMultipleIps_ShouldReturnFirstValid() {
            String result = IpUtils.getMultistageReverseProxyIp("unknown, 192.168.1.1, 192.168.1.2");
            assertEquals("192.168.1.1", result);
        }

        @Test
        @DisplayName("单IP时原样返回")
        void getMultistageReverseProxyIp_WhenSingleIp_ShouldReturnSame() {
            String result = IpUtils.getMultistageReverseProxyIp("192.168.1.1");
            assertEquals("192.168.1.1", result);
        }

        @Test
        @DisplayName("超长IP截取前255字符")
        void getMultistageReverseProxyIp_WhenTooLong_ShouldTruncate() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 300; i++) {
                sb.append("a");
            }
            String result = IpUtils.getMultistageReverseProxyIp(sb.toString());
            assertEquals(255, result.length());
        }
    }

    @Nested
    @DisplayName("isUnknown 测试")
    class IsUnknownTest {
        @Test
        @DisplayName("null返回true")
        void isUnknown_WhenNull_ShouldReturnTrue() {
            assertTrue(IpUtils.isUnknown(null));
        }

        @Test
        @DisplayName("空字符串返回true")
        void isUnknown_WhenEmpty_ShouldReturnTrue() {
            assertTrue(IpUtils.isUnknown(""));
        }

        @Test
        @DisplayName("unknown返回true")
        void isUnknown_WhenUnknownString_ShouldReturnTrue() {
            assertTrue(IpUtils.isUnknown("unknown"));
            assertTrue(IpUtils.isUnknown("UNKNOWN"));
        }

        @Test
        @DisplayName("有效字符串返回false")
        void isUnknown_WhenValid_ShouldReturnFalse() {
            assertFalse(IpUtils.isUnknown("192.168.1.1"));
        }
    }

    @Nested
    @DisplayName("isIP 测试")
    class IsIPTest {
        @Test
        @DisplayName("有效IPv4地址返回true")
        void isIP_WhenValidIPv4_ShouldReturnTrue() {
            assertTrue(IpUtils.isIP("192.168.1.1"));
            assertTrue(IpUtils.isIP("0.0.0.0"));
            assertTrue(IpUtils.isIP("255.255.255.255"));
        }

        @Test
        @DisplayName("无效IP返回false")
        void isIP_WhenInvalid_ShouldReturnFalse() {
            assertFalse(IpUtils.isIP(null));
            assertFalse(IpUtils.isIP(""));
            assertFalse(IpUtils.isIP("256.1.1.1"));
            assertFalse(IpUtils.isIP("invalid"));
        }
    }

    @Nested
    @DisplayName("isIpWildCard 通配符IP测试")
    class IsIpWildCardTest {
        @Test
        @DisplayName("有效通配符IP返回true")
        void isIpWildCard_WhenValid_ShouldReturnTrue() {
            assertTrue(IpUtils.isIpWildCard("*.*.*.*"));
            assertTrue(IpUtils.isIpWildCard("192.168.*.*"));
            assertTrue(IpUtils.isIpWildCard("192.168.1.*"));
        }

        @Test
        @DisplayName("无效通配符IP返回false")
        void isIpWildCard_WhenInvalid_ShouldReturnFalse() {
            assertFalse(IpUtils.isIpWildCard(null));
            assertFalse(IpUtils.isIpWildCard(""));
            assertFalse(IpUtils.isIpWildCard("192.168.1.1"));
        }
    }

    @Nested
    @DisplayName("ipIsInWildCardNoCheck 测试")
    class IpIsInWildCardNoCheckTest {
        @Test
        @DisplayName("IP匹配通配符")
        void ipIsInWildCardNoCheck_WhenMatch_ShouldReturnTrue() {
            assertTrue(IpUtils.ipIsInWildCardNoCheck("192.168.*.*", "192.168.1.1"));
            assertTrue(IpUtils.ipIsInWildCardNoCheck("192.168.1.*", "192.168.1.100"));
        }

        @Test
        @DisplayName("IP不匹配通配符")
        void ipIsInWildCardNoCheck_WhenNotMatch_ShouldReturnFalse() {
            assertFalse(IpUtils.ipIsInWildCardNoCheck("192.168.*.*", "10.0.0.1"));
            assertFalse(IpUtils.ipIsInWildCardNoCheck("192.168.1.*", "192.168.2.1"));
        }
    }

    @Nested
    @DisplayName("isIPSegment IP段测试")
    class IsIPSegmentTest {
        @Test
        @DisplayName("有效IP段返回true")
        void isIPSegment_WhenValid_ShouldReturnTrue() {
            assertTrue(IpUtils.isIPSegment("10.10.10.1-10.10.10.99"));
            assertTrue(IpUtils.isIPSegment("192.168.1.1-192.168.1.255"));
        }

        @Test
        @DisplayName("无效IP段返回false")
        void isIPSegment_WhenInvalid_ShouldReturnFalse() {
            assertFalse(IpUtils.isIPSegment(null));
            assertFalse(IpUtils.isIPSegment(""));
            assertFalse(IpUtils.isIPSegment("192.168.1.1"));
        }
    }

    @Nested
    @DisplayName("ipIsInNetNoCheck IP段包含测试")
    class IpIsInNetNoCheckTest {
        @Test
        @DisplayName("IP在段内返回true")
        void ipIsInNetNoCheck_WhenInSegment_ShouldReturnTrue() {
            assertTrue(IpUtils.ipIsInNetNoCheck("10.10.10.1-10.10.10.99", "10.10.10.50"));
        }

        @Test
        @DisplayName("IP不在段内返回false")
        void ipIsInNetNoCheck_WhenNotInSegment_ShouldReturnFalse() {
            assertFalse(IpUtils.ipIsInNetNoCheck("10.10.10.1-10.10.10.99", "10.10.10.100"));
        }

        @Test
        @DisplayName("反向IP段也能正确判断")
        void ipIsInNetNoCheck_WhenReversedSegment_ShouldWork() {
            assertTrue(IpUtils.ipIsInNetNoCheck("10.10.10.99-10.10.10.1", "10.10.10.50"));
        }
    }

    @Nested
    @DisplayName("isMatchedIp 综合IP匹配测试")
    class IsMatchedIpTest {
        @Test
        @DisplayName("精确匹配")
        void isMatchedIp_WhenExactMatch_ShouldReturnTrue() {
            assertTrue(IpUtils.isMatchedIp("192.168.1.1", "192.168.1.1"));
        }

        @Test
        @DisplayName("通配符匹配")
        void isMatchedIp_WhenWildCardMatch_ShouldReturnTrue() {
            assertTrue(IpUtils.isMatchedIp("192.168.*.*", "192.168.1.1"));
        }

        @Test
        @DisplayName("IP段匹配")
        void isMatchedIp_WhenSegmentMatch_ShouldReturnTrue() {
            assertTrue(IpUtils.isMatchedIp("10.10.10.1-10.10.10.99", "10.10.10.50"));
        }

        @Test
        @DisplayName("多规则分号分隔")
        void isMatchedIp_WhenMultipleRules_ShouldMatchAny() {
            assertTrue(IpUtils.isMatchedIp("192.168.1.1;10.*.*.*", "10.0.0.1"));
        }

        @Test
        @DisplayName("不匹配返回false")
        void isMatchedIp_WhenNoMatch_ShouldReturnFalse() {
            assertFalse(IpUtils.isMatchedIp("192.168.1.1", "192.168.1.2"));
        }

        @Test
        @DisplayName("null或空字符串返回false")
        void isMatchedIp_WhenNullOrEmpty_ShouldReturnFalse() {
            assertFalse(IpUtils.isMatchedIp(null, "192.168.1.1"));
            assertFalse(IpUtils.isMatchedIp("192.168.1.1", null));
            assertFalse(IpUtils.isMatchedIp("", "192.168.1.1"));
        }
    }
}
