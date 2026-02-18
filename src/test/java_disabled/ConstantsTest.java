package com.zjjh.fdtemp.constants;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Constants 通用常量信息测试
 */
@DisplayName("Constants 通用常量信息测试")
class ConstantsTest {

    @Nested
    @DisplayName("字符集常量测试")
    class CharsetConstantsTest {
        @Test
        @DisplayName("UTF-8字符集")
        void utf8_ShouldBeCorrect() {
            assertEquals("UTF-8", Constants.UTF8);
        }

        @Test
        @DisplayName("GBK字符集")
        void gbk_ShouldBeCorrect() {
            assertEquals("GBK", Constants.GBK);
        }
    }

    @Nested
    @DisplayName("语言常量测试")
    class LocaleConstantsTest {
        @Test
        @DisplayName("默认语言为简体中文")
        void defaultLocale_ShouldBeSimplifiedChinese() {
            assertEquals(Locale.SIMPLIFIED_CHINESE, Constants.DEFAULT_LOCALE);
        }
    }

    @Nested
    @DisplayName("HTTP协议常量测试")
    class HttpConstantsTest {
        @Test
        @DisplayName("HTTP协议前缀")
        void http_ShouldBeCorrect() {
            assertEquals("http://", Constants.HTTP);
        }

        @Test
        @DisplayName("HTTPS协议前缀")
        void https_ShouldBeCorrect() {
            assertEquals("https://", Constants.HTTPS);
        }
    }

    @Nested
    @DisplayName("通用标识常量测试")
    class CommonConstantsTest {
        @Test
        @DisplayName("成功标识为0")
        void success_ShouldBe0() {
            assertEquals("0", Constants.SUCCESS);
        }

        @Test
        @DisplayName("失败标识为1")
        void fail_ShouldBe1() {
            assertEquals("1", Constants.FAIL);
        }
    }

    @Nested
    @DisplayName("登录相关常量测试")
    class LoginConstantsTest {
        @Test
        @DisplayName("登录成功标识")
        void loginSuccess_ShouldBeCorrect() {
            assertEquals("Success", Constants.LOGIN_SUCCESS);
        }

        @Test
        @DisplayName("注销标识")
        void logout_ShouldBeCorrect() {
            assertEquals("Logout", Constants.LOGOUT);
        }

        @Test
        @DisplayName("注册标识")
        void register_ShouldBeCorrect() {
            assertEquals("Register", Constants.REGISTER);
        }

        @Test
        @DisplayName("登录失败标识")
        void loginFail_ShouldBeCorrect() {
            assertEquals("Error", Constants.LOGIN_FAIL);
        }
    }

    @Nested
    @DisplayName("缓存常量测试")
    class CacheConstantsTest {
        @Test
        @DisplayName("授权缓存名称")
        void sysAuthCache_ShouldBeCorrect() {
            assertEquals("sys-authCache", Constants.SYS_AUTH_CACHE);
        }

        @Test
        @DisplayName("配置缓存名称")
        void sysConfigCache_ShouldBeCorrect() {
            assertEquals("sys-config", Constants.SYS_CONFIG_CACHE);
        }

        @Test
        @DisplayName("配置缓存Key")
        void sysConfigKey_ShouldBeCorrect() {
            assertEquals("sys_config:", Constants.SYS_CONFIG_KEY);
        }

        @Test
        @DisplayName("字典缓存名称")
        void sysDictCache_ShouldBeCorrect() {
            assertEquals("sys-dict", Constants.SYS_DICT_CACHE);
        }

        @Test
        @DisplayName("字典缓存Key")
        void sysDictKey_ShouldBeCorrect() {
            assertEquals("sys_dict:", Constants.SYS_DICT_KEY);
        }
    }

    @Nested
    @DisplayName("资源路径常量测试")
    class ResourceConstantsTest {
        @Test
        @DisplayName("资源映射路径前缀")
        void resourcePrefix_ShouldBeCorrect() {
            assertEquals("/profile", Constants.RESOURCE_PREFIX);
        }
    }

    @Nested
    @DisplayName("远程调用常量测试")
    class LookupConstantsTest {
        @Test
        @DisplayName("RMI前缀")
        void lookupRmi_ShouldBeCorrect() {
            assertEquals("rmi:", Constants.LOOKUP_RMI);
        }

        @Test
        @DisplayName("LDAP前缀")
        void lookupLdap_ShouldBeCorrect() {
            assertEquals("ldap:", Constants.LOOKUP_LDAP);
        }

        @Test
        @DisplayName("LDAPS前缀")
        void lookupLdaps_ShouldBeCorrect() {
            assertEquals("ldaps:", Constants.LOOKUP_LDAPS);
        }
    }

    @Nested
    @DisplayName("定时任务常量测试")
    class JobConstantsTest {
        @Test
        @DisplayName("定时任务白名单包含service包")
        void jobWhitelist_ShouldContainServicePackage() {
            assertEquals(1, Constants.JOB_WHITELIST_STR.length);
            assertEquals("com.zjjh.fdtemp.service", Constants.JOB_WHITELIST_STR[0]);
        }

        @Test
        @DisplayName("定时任务违规字符列表")
        void jobErrorStr_ShouldContainDangerousClasses() {
            assertTrue(Constants.JOB_ERROR_STR.length > 0);
            // 验证包含一些危险类
            boolean containsUrl = false;
            boolean containsSpring = false;
            for (String str : Constants.JOB_ERROR_STR) {
                if (str.contains("URL")) containsUrl = true;
                if (str.contains("springframework")) containsSpring = true;
            }
            assertTrue(containsUrl || containsSpring);
        }
    }
}
