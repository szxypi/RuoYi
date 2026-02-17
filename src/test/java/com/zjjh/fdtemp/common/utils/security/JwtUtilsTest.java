package com.zjjh.fdtemp.common.utils.security;

import com.zjjh.fdtemp.beans.LoginUser;
import com.zjjh.fdtemp.beans.entity.SysUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JwtUtils 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtils 测试")
class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    private LoginUser testLoginUser;
    private static final String SECRET = "fdtempJwtSecretKeyForTestingPurposesOnlyMinimum256BitsRequired";
    private static final long EXPIRATION = 86400000L; // 1天
    private static final long REFRESH_EXPIRATION = 604800000L; // 7天
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();

        // 设置私有字段
        ReflectionTestUtils.setField(jwtUtils, "secret", Base64.getEncoder().encodeToString(SECRET.getBytes()));
        ReflectionTestUtils.setField(jwtUtils, "expiration", EXPIRATION);
        ReflectionTestUtils.setField(jwtUtils, "refreshExpiration", REFRESH_EXPIRATION);
        ReflectionTestUtils.setField(jwtUtils, "tokenHeader", TOKEN_HEADER);
        ReflectionTestUtils.setField(jwtUtils, "tokenPrefix", TOKEN_PREFIX);

        // 创建测试用户
        SysUser user = new SysUser();
        user.setId("1");
        user.setLoginName("testuser");
        user.setUserName("测试用户");
        user.setPassword("encodedPassword");

        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");
        testLoginUser = new LoginUser(user, permissions);
    }

    @Test
    @DisplayName("generateToken - 正常生成token")
    void testGenerateToken_Success() {
        String token = jwtUtils.generateToken(testLoginUser);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(token.split("\\.").length == 3); // JWT有3部分
    }

    @Test
    @DisplayName("generateRefreshToken - 正常生成刷新token")
    void testGenerateRefreshToken_Success() {
        String refreshToken = jwtUtils.generateRefreshToken(testLoginUser);

        assertNotNull(refreshToken);
        assertTrue(refreshToken.length() > 0);
    }

    @Test
    @DisplayName("extractUsername - 正常提取用户名")
    void testExtractUsername_Success() {
        String token = jwtUtils.generateToken(testLoginUser);
        String username = jwtUtils.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("extractExpiration - 正常提取过期时间")
    void testExtractExpiration_Success() {
        String token = jwtUtils.generateToken(testLoginUser);
        Date expiration = jwtUtils.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    @DisplayName("validateToken(UserDetails) - 有效token验证成功")
    void testValidateTokenWithUserDetails_Success() {
        String token = jwtUtils.generateToken(testLoginUser);

        assertTrue(jwtUtils.validateToken(token, testLoginUser));
    }

    @Test
    @DisplayName("validateToken(UserDetails) - 用户名不匹配验证失败")
    void testValidateTokenWithUserDetails_WrongUser() {
        String token = jwtUtils.generateToken(testLoginUser);

        SysUser otherUser = new SysUser();
        otherUser.setLoginName("otheruser");
        Set<String> permissions = new HashSet<>();
        LoginUser otherLoginUser = new LoginUser(otherUser, permissions);

        assertFalse(jwtUtils.validateToken(token, otherLoginUser));
    }

    @Test
    @DisplayName("validateToken(String) - 有效token返回true")
    void testValidateToken_Success() {
        String token = jwtUtils.generateToken(testLoginUser);

        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    @DisplayName("validateToken(String) - 无效token返回false")
    void testValidateToken_InvalidToken() {
        assertFalse(jwtUtils.validateToken("invalid.token.string"));
    }

    @Test
    @DisplayName("validateToken(String) - 空token返回false")
    void testValidateToken_EmptyToken() {
        assertFalse(jwtUtils.validateToken(""));
    }

    @Test
    @DisplayName("validateToken(String) - null token返回false")
    void testValidateToken_NullToken() {
        assertFalse(jwtUtils.validateToken(null));
    }

    @Test
    @DisplayName("extractTokenFromRequest - 正常提取token")
    void testExtractTokenFromRequest_Success() {
        String token = jwtUtils.generateToken(testLoginUser);
        when(request.getHeader(TOKEN_HEADER)).thenReturn(TOKEN_PREFIX + token);

        String extractedToken = jwtUtils.extractTokenFromRequest(request);

        assertEquals(token, extractedToken);
    }

    @Test
    @DisplayName("extractTokenFromRequest - 无header返回null")
    void testExtractTokenFromRequest_NoHeader() {
        when(request.getHeader(TOKEN_HEADER)).thenReturn(null);

        String extractedToken = jwtUtils.extractTokenFromRequest(request);

        assertNull(extractedToken);
    }

    @Test
    @DisplayName("extractTokenFromRequest - 错误前缀返回null")
    void testExtractTokenFromRequest_WrongPrefix() {
        when(request.getHeader(TOKEN_HEADER)).thenReturn("Basic somecredentials");

        String extractedToken = jwtUtils.extractTokenFromRequest(request);

        assertNull(extractedToken);
    }

    @Test
    @DisplayName("extractTokenFromRequest - 空header返回null")
    void testExtractTokenFromRequest_EmptyHeader() {
        when(request.getHeader(TOKEN_HEADER)).thenReturn("");

        String extractedToken = jwtUtils.extractTokenFromRequest(request);

        assertNull(extractedToken);
    }

    @Test
    @DisplayName("extractTokenFromRequest - 只有前缀返回空字符串")
    void testExtractTokenFromRequest_OnlyPrefix() {
        when(request.getHeader(TOKEN_HEADER)).thenReturn(TOKEN_PREFIX);

        String extractedToken = jwtUtils.extractTokenFromRequest(request);

        assertEquals("", extractedToken);
    }

    @Test
    @DisplayName("extractClaim - 正常提取claim")
    void testExtractClaim_Success() {
        String token = jwtUtils.generateToken(testLoginUser);

        Function<Claims, String> subjectExtractor = Claims::getSubject;
        String subject = jwtUtils.extractClaim(token, subjectExtractor);

        assertEquals("testuser", subject);
    }

    @Test
    @DisplayName("generateToken - 每次生成不同的token")
    void testGenerateToken_DifferentTokens() throws InterruptedException {
        String token1 = jwtUtils.generateToken(testLoginUser);
        Thread.sleep(1000); // 确保时间戳不同
        String token2 = jwtUtils.generateToken(testLoginUser);

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("generateRefreshToken - 包含refresh类型claim")
    void testGenerateRefreshToken_ContainsTypeClaim() {
        String refreshToken = jwtUtils.generateRefreshToken(testLoginUser);

        // 验证可以正常解析
        String username = jwtUtils.extractUsername(refreshToken);
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("isRefreshToken - refresh token 返回 true")
    void testIsRefreshToken_RefreshToken() {
        String refreshToken = jwtUtils.generateRefreshToken(testLoginUser);
        assertTrue(jwtUtils.isRefreshToken(refreshToken));
    }

    @Test
    @DisplayName("isRefreshToken - access token 返回 false")
    void testIsRefreshToken_AccessToken() {
        String token = jwtUtils.generateToken(testLoginUser);
        assertFalse(jwtUtils.isRefreshToken(token));
    }

    @Test
    @DisplayName("isRefreshToken - 非法 token 返回 false")
    void testIsRefreshToken_InvalidToken() {
        assertFalse(jwtUtils.isRefreshToken("invalid.token"));
    }

    @Test
    @DisplayName("extractUsername - 无效token抛出异常")
    void testExtractUsername_InvalidToken() {
        assertThrows(Exception.class, () -> jwtUtils.extractUsername("invalid.token"));
    }

    @Test
    @DisplayName("token签名验证 - 修改后的token验证失败")
    void testTokenSignature_TamperedToken() {
        String token = jwtUtils.generateToken(testLoginUser);
        // 修改token的中间部分（不是简单追加）
        String[] parts = token.split("\\.");
        String tamperedToken = parts[0] + "." + parts[1] + "x." + parts[2];

        // 修改后的token应该验证失败
        assertFalse(jwtUtils.validateToken(tamperedToken));
    }

    @Test
    @DisplayName("token格式验证 - 非JWT格式返回false")
    void testValidateToken_NonJwtFormat() {
        assertFalse(jwtUtils.validateToken("not-a-jwt-token"));
        assertFalse(jwtUtils.validateToken("only.two"));
        assertFalse(jwtUtils.validateToken("too.many.parts.here.four"));
    }

    @Test
    @DisplayName("extractTokenFromRequest - token包含空格trim处理")
    void testExtractTokenFromRequest_TokenWithSpaces() {
        String token = jwtUtils.generateToken(testLoginUser);
        when(request.getHeader(TOKEN_HEADER)).thenReturn(TOKEN_PREFIX + "  " + token + "  ");

        String extractedToken = jwtUtils.extractTokenFromRequest(request);

        // trim应该去掉空格
        assertEquals(token.trim(), extractedToken.trim());
    }

    @Test
    @DisplayName("不同用户生成不同token")
    void testGenerateToken_DifferentUsers() {
        String token1 = jwtUtils.generateToken(testLoginUser);

        SysUser user2 = new SysUser();
        user2.setLoginName("anotheruser");
        Set<String> permissions = new HashSet<>();
        LoginUser loginUser2 = new LoginUser(user2, permissions);

        String token2 = jwtUtils.generateToken(loginUser2);

        assertNotEquals(token1, token2);

        String username1 = jwtUtils.extractUsername(token1);
        String username2 = jwtUtils.extractUsername(token2);

        assertEquals("testuser", username1);
        assertEquals("anotheruser", username2);
    }
}
