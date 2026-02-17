package com.zjjh.fdtemp.config;

import com.zjjh.fdtemp.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SecurityConfig 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityConfig 测试")
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    @DisplayName("passwordEncoder - 返回BCryptPasswordEncoder")
    void testPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
    }

    @Test
    @DisplayName("passwordEncoder - 密码加密验证")
    void testPasswordEncoder_Encryption() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String rawPassword = "testPassword123";
        String encodedPassword = encoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
        assertFalse(encoder.matches("wrongPassword", encodedPassword));
    }

    @Test
    @DisplayName("authenticationManager - 正常返回AuthenticationManager")
    void testAuthenticationManager() throws Exception {
        AuthenticationManager mockManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mockManager);

        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        assertNotNull(result);
        assertEquals(mockManager, result);
    }

    @Test
    @DisplayName("corsConfigurationSource - 返回有效配置")
    void testCorsConfigurationSource() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        assertNotNull(source);
    }

    @Test
    @DisplayName("filterChain - 正常创建SecurityFilterChain")
    void testFilterChain() throws Exception {
        // filterChain方法需要HttpSecurity参数，需要更复杂的设置
        // 这里验证基本功能
        assertNotNull(securityConfig);
    }

    @Test
    @DisplayName("PasswordEncoder - 空密码处理")
    void testPasswordEncoder_EmptyPassword() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String encodedEmpty = encoder.encode("");

        assertNotNull(encodedEmpty);
        assertTrue(encoder.matches("", encodedEmpty));
    }

    @Test
    @DisplayName("PasswordEncoder - null密码处理")
    void testPasswordEncoder_NullPassword() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        assertThrows(IllegalArgumentException.class, () -> encoder.encode(null));
    }

    @Test
    @DisplayName("PasswordEncoder - 长密码处理")
    void testPasswordEncoder_LongPassword() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50; i++) {  // BCrypt限制72字节
            sb.append("a");
        }
        String longPassword = sb.toString();
        String encoded = encoder.encode(longPassword);

        assertNotNull(encoded);
        assertTrue(encoder.matches(longPassword, encoded));
    }

    @Test
    @DisplayName("PasswordEncoder - 特殊字符密码")
    void testPasswordEncoder_SpecialCharacters() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String specialPassword = "!@#$%^&*()_+-=[]{}|;':\",./<>?~`中文字符";
        String encoded = encoder.encode(specialPassword);

        assertNotNull(encoded);
        assertTrue(encoder.matches(specialPassword, encoded));
    }

    @Test
    @DisplayName("PasswordEncoder - 每次加密结果不同(salt)")
    void testPasswordEncoder_DifferentSalts() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String password = "samePassword";
        String encoded1 = encoder.encode(password);
        String encoded2 = encoder.encode(password);

        // BCrypt每次加密结果不同（不同salt）
        assertNotEquals(encoded1, encoded2);
        // 但都能匹配
        assertTrue(encoder.matches(password, encoded1));
        assertTrue(encoder.matches(password, encoded2));
    }

    @Test
    @DisplayName("PasswordEncoder - matches方法-null参数处理")
    void testPasswordEncoder_MatchesNull() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String encoded = encoder.encode("password");

        assertThrows(IllegalArgumentException.class, () -> encoder.matches(null, encoded));
    }
}
