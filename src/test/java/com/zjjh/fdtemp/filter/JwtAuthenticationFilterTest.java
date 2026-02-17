package com.zjjh.fdtemp.filter;

import com.zjjh.fdtemp.beans.LoginUser;
import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.utils.security.JwtUtils;
import com.zjjh.fdtemp.common.utils.security.TokenBlacklist;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * JwtAuthenticationFilter 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter 测试")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private TokenBlacklist tokenBlacklist;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private LoginUser testLoginUser;
    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        // 创建测试用户
        SysUser user = new SysUser();
        user.setId("1");
        user.setLoginName("testuser");
        user.setUserName("测试用户");
        user.setPassword("encodedPassword");
        user.setStatus("0");
        user.setYn("0");

        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");
        testLoginUser = new LoginUser(user, permissions);

        // 设置私有字段值
        ReflectionTestUtils.setField(jwtAuthenticationFilter, "tokenHeader", TOKEN_HEADER);
        ReflectionTestUtils.setField(jwtAuthenticationFilter, "tokenPrefix", TOKEN_PREFIX);
    }

    @Test
    @DisplayName("doFilterInternal - 有效token正常认证")
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        when(request.getHeader(TOKEN_HEADER)).thenReturn(TOKEN_PREFIX + VALID_TOKEN);
        when(tokenBlacklist.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(jwtUtils.extractUsername(VALID_TOKEN)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testLoginUser);
        when(jwtUtils.validateToken(VALID_TOKEN, testLoginUser)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    @DisplayName("doFilterInternal - 无Authorization header")
    void testDoFilterInternal_NoAuthHeader() throws ServletException, IOException {
        when(request.getHeader(TOKEN_HEADER)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("doFilterInternal - 空Authorization header")
    void testDoFilterInternal_EmptyAuthHeader() throws ServletException, IOException {
        when(request.getHeader(TOKEN_HEADER)).thenReturn("");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("doFilterInternal - 错误的header前缀")
    void testDoFilterInternal_WrongPrefix() throws ServletException, IOException {
        when(request.getHeader(TOKEN_HEADER)).thenReturn("Basic somecredentials");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("doFilterInternal - token在黑名单中")
    void testDoFilterInternal_TokenBlacklisted() throws ServletException, IOException {
        when(request.getHeader(TOKEN_HEADER)).thenReturn(TOKEN_PREFIX + VALID_TOKEN);
        when(tokenBlacklist.isBlacklisted(VALID_TOKEN)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtils, never()).extractUsername(anyString());
    }

    @Test
    @DisplayName("doFilterInternal - token解析异常")
    void testDoFilterInternal_TokenParseError() throws ServletException, IOException {
        when(request.getHeader(TOKEN_HEADER)).thenReturn(TOKEN_PREFIX + "invalid.token");
        when(tokenBlacklist.isBlacklisted("invalid.token")).thenReturn(false);
        when(jwtUtils.extractUsername("invalid.token")).thenThrow(new RuntimeException("Invalid token"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("doFilterInternal - token中用户名为空")
    void testDoFilterInternal_EmptyUsername() throws ServletException, IOException {
        when(request.getHeader(TOKEN_HEADER)).thenReturn(TOKEN_PREFIX + VALID_TOKEN);
        when(tokenBlacklist.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(jwtUtils.extractUsername(VALID_TOKEN)).thenReturn("");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("doFilterInternal - token中用户名为null")
    void testDoFilterInternal_NullUsername() throws ServletException, IOException {
        when(request.getHeader(TOKEN_HEADER)).thenReturn(TOKEN_PREFIX + VALID_TOKEN);
        when(tokenBlacklist.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(jwtUtils.extractUsername(VALID_TOKEN)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("doFilterInternal - token验证失败")
    void testDoFilterInternal_TokenValidationFailed() throws ServletException, IOException {
        when(request.getHeader(TOKEN_HEADER)).thenReturn(TOKEN_PREFIX + VALID_TOKEN);
        when(tokenBlacklist.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(jwtUtils.extractUsername(VALID_TOKEN)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testLoginUser);
        when(jwtUtils.validateToken(VALID_TOKEN, testLoginUser)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("doFilterInternal - 已有认证信息跳过")
    void testDoFilterInternal_AlreadyAuthenticated() throws ServletException, IOException {
        // 先设置一个已存在的认证
        SecurityContextHolder.getContext().setAuthentication(
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                testLoginUser, null, testLoginUser.getAuthorities())
        );

        when(request.getHeader(TOKEN_HEADER)).thenReturn(TOKEN_PREFIX + VALID_TOKEN);
        when(tokenBlacklist.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(jwtUtils.extractUsername(VALID_TOKEN)).thenReturn("testuser");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        // 已有认证信息不应被覆盖
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("doFilterInternal - 用户不存在")
    void testDoFilterInternal_UserNotFound() throws ServletException, IOException {
        when(request.getHeader(TOKEN_HEADER)).thenReturn(TOKEN_PREFIX + VALID_TOKEN);
        when(tokenBlacklist.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(jwtUtils.extractUsername(VALID_TOKEN)).thenReturn("nonexistent");
        when(userDetailsService.loadUserByUsername("nonexistent"))
            .thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("doFilterInternal - 只有Bearer前缀没有token")
    void testDoFilterInternal_OnlyPrefixNoToken() throws ServletException, IOException {
        when(request.getHeader(TOKEN_HEADER)).thenReturn(TOKEN_PREFIX);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        // 空字符串的token应该被处理
        verify(tokenBlacklist).isBlacklisted("");
    }

    @Test
    @DisplayName("doFilterInternal - token包含空格")
    void testDoFilterInternal_TokenWithSpaces() throws ServletException, IOException {
        String tokenWithSpaces = "  token  ";
        when(request.getHeader(TOKEN_HEADER)).thenReturn(TOKEN_PREFIX + tokenWithSpaces);
        when(tokenBlacklist.isBlacklisted("token")).thenReturn(false);
        when(jwtUtils.extractUsername("token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testLoginUser);
        when(jwtUtils.validateToken("token", testLoginUser)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        // trim()后的token应该被使用
        verify(tokenBlacklist).isBlacklisted("token");
    }
}
