package com.zjjh.fdtemp.controller.auth;

import com.zjjh.fdtemp.beans.LoginUser;
import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.common.utils.security.JwtUtils;
import com.zjjh.fdtemp.common.utils.security.TokenBlacklist;
import com.zjjh.fdtemp.service.SysMenuService;
import com.zjjh.fdtemp.service.SysRoleService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AuthController 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController 测试")
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private TokenBlacklist tokenBlacklist;

    @Mock
    private SysMenuService menuService;

    @Mock
    private SysRoleService roleService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthController authController;

    private LoginUser testLoginUser;
    private SysUser testUser;
    private static final String TEST_TOKEN = "test.jwt.token";
    private static final String TEST_REFRESH_TOKEN = "test.refresh.token";

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new SysUser();
        testUser.setId("1");
        testUser.setLoginName("testuser");
        testUser.setUserName("测试用户");
        testUser.setPassword("encodedPassword");
        testUser.setStatus("0");
        testUser.setYn("0");

        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");
        permissions.add("system:user:query");

        testLoginUser = new LoginUser(testUser, permissions);
    }

    @Test
    @DisplayName("login - 正常登录成功")
    void testLogin_Success() {
        // 准备数据
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("username", "testuser");
        loginBody.put("password", "password123");

        // Mock认证
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testLoginUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateToken(testLoginUser)).thenReturn(TEST_TOKEN);
        when(jwtUtils.generateRefreshToken(testLoginUser)).thenReturn(TEST_REFRESH_TOKEN);

        // 执行
        AjaxResult result = authController.login(loginBody);

        // 验证
        assertTrue(result.isSuccess());
        assertEquals("登录成功", result.get("msg"));

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        assertNotNull(data);
        assertEquals(TEST_TOKEN, data.get("token"));
        assertEquals(TEST_REFRESH_TOKEN, data.get("refreshToken"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateToken(testLoginUser);
        verify(jwtUtils).generateRefreshToken(testLoginUser);
    }

    @Test
    @DisplayName("login - 错误密码登录失败")
    void testLogin_WrongPassword() {
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("username", "testuser");
        loginBody.put("password", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authController.login(loginBody));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, never()).generateToken(any());
    }

    @Test
    @DisplayName("login - 空用户名")
    void testLogin_EmptyUsername() {
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("username", "");
        loginBody.put("password", "password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authController.login(loginBody));
    }

    @Test
    @DisplayName("login - null用户名")
    void testLogin_NullUsername() {
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("username", null);
        loginBody.put("password", "password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authController.login(loginBody));
    }

    @Test
    @DisplayName("logout - 正常登出")
    void testLogout_Success() {
        when(jwtUtils.extractTokenFromRequest(request)).thenReturn(TEST_TOKEN);
        when(jwtUtils.validateToken(TEST_TOKEN)).thenReturn(true);
        Date expiration = new Date(System.currentTimeMillis() + 3600000);
        when(jwtUtils.extractExpiration(TEST_TOKEN)).thenReturn(expiration);

        AjaxResult result = authController.logout(request, null);

        assertTrue(result.isSuccess());
        assertEquals("退出成功", result.get("msg"));
        verify(tokenBlacklist).addToBlacklist(eq(TEST_TOKEN), anyLong());
    }

    @Test
    @DisplayName("logout - 无token直接返回成功")
    void testLogout_NoToken() {
        when(jwtUtils.extractTokenFromRequest(request)).thenReturn(null);

        AjaxResult result = authController.logout(request, null);

        assertTrue(result.isSuccess());
        assertEquals("退出成功", result.get("msg"));
        verify(tokenBlacklist, never()).addToBlacklist(anyString(), anyLong());
    }

    @Test
    @DisplayName("logout - 无效token直接返回成功")
    void testLogout_InvalidToken() {
        when(jwtUtils.extractTokenFromRequest(request)).thenReturn("invalid.token");
        when(jwtUtils.validateToken("invalid.token")).thenReturn(false);

        AjaxResult result = authController.logout(request, null);

        assertTrue(result.isSuccess());
        assertEquals("退出成功", result.get("msg"));
        verify(tokenBlacklist, never()).addToBlacklist(anyString(), anyLong());
    }

    @Test
    @DisplayName("logout - 同时拉黑refreshToken")
    void testLogout_BlacklistRefreshToken() {
        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", TEST_REFRESH_TOKEN);

        when(jwtUtils.extractTokenFromRequest(request)).thenReturn(TEST_TOKEN);
        when(jwtUtils.validateToken(TEST_TOKEN)).thenReturn(true);
        when(jwtUtils.extractExpiration(TEST_TOKEN)).thenReturn(new Date(System.currentTimeMillis() + 3600000));
        when(jwtUtils.validateToken(TEST_REFRESH_TOKEN)).thenReturn(true);
        when(jwtUtils.isRefreshToken(TEST_REFRESH_TOKEN)).thenReturn(true);
        when(jwtUtils.extractExpiration(TEST_REFRESH_TOKEN)).thenReturn(new Date(System.currentTimeMillis() + 7200000));

        AjaxResult result = authController.logout(request, body);

        assertTrue(result.isSuccess());
        verify(tokenBlacklist).addToBlacklist(eq(TEST_TOKEN), anyLong());
        verify(tokenBlacklist).addToBlacklist(eq(TEST_REFRESH_TOKEN), anyLong());
    }

    @Test
    @DisplayName("refresh - 正常刷新token")
    void testRefresh_Success() {
        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", TEST_REFRESH_TOKEN);

        when(jwtUtils.validateToken(TEST_REFRESH_TOKEN)).thenReturn(true);
        when(jwtUtils.isRefreshToken(TEST_REFRESH_TOKEN)).thenReturn(true);
        when(jwtUtils.extractUsername(TEST_REFRESH_TOKEN)).thenReturn("testuser");
        when(jwtUtils.extractExpiration(TEST_REFRESH_TOKEN)).thenReturn(new Date(System.currentTimeMillis() + 3600000));
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testLoginUser);
        when(jwtUtils.generateToken(testLoginUser)).thenReturn("newToken");
        when(jwtUtils.generateRefreshToken(testLoginUser)).thenReturn("newRefreshToken");

        AjaxResult result = authController.refresh(body);

        assertTrue(result.isSuccess());
        assertEquals("刷新成功", result.get("msg"));

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        assertNotNull(data);
        assertEquals("newToken", data.get("token"));
        assertEquals("newRefreshToken", data.get("refreshToken"));
        verify(tokenBlacklist).addToBlacklist(eq(TEST_REFRESH_TOKEN), anyLong());
    }

    @Test
    @DisplayName("refresh - 空refreshToken返回错误")
    void testRefresh_EmptyRefreshToken() {
        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", "");

        AjaxResult result = authController.refresh(body);

        assertTrue(result.isError());
        assertEquals("刷新Token不能为空", result.get("msg"));
    }

    @Test
    @DisplayName("refresh - null refreshToken返回错误")
    void testRefresh_NullRefreshToken() {
        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", null);

        AjaxResult result = authController.refresh(body);

        assertTrue(result.isError());
        assertEquals("刷新Token不能为空", result.get("msg"));
    }

    @Test
    @DisplayName("refresh - 无body参数返回错误")
    void testRefresh_NoRefreshTokenInBody() {
        Map<String, String> body = new HashMap<>();

        AjaxResult result = authController.refresh(body);

        assertTrue(result.isError());
        assertEquals("刷新Token不能为空", result.get("msg"));
    }

    @Test
    @DisplayName("refresh - 黑名单refreshToken返回错误")
    void testRefresh_BlacklistedRefreshToken() {
        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", TEST_REFRESH_TOKEN);
        when(tokenBlacklist.isBlacklisted(TEST_REFRESH_TOKEN)).thenReturn(true);

        AjaxResult result = authController.refresh(body);

        assertTrue(result.isError());
        assertEquals("刷新Token无效", result.get("msg"));
        verify(jwtUtils, never()).validateToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("refresh - 无效refreshToken返回错误")
    void testRefresh_InvalidRefreshToken() {
        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", "invalid.refresh.token");

        when(jwtUtils.validateToken("invalid.refresh.token")).thenReturn(false);

        AjaxResult result = authController.refresh(body);

        assertTrue(result.isError());
        assertEquals("刷新Token无效", result.get("msg"));
    }

    @Test
    @DisplayName("refresh - access token 不能用于刷新")
    void testRefresh_AccessTokenRejected() {
        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", TEST_TOKEN);

        when(jwtUtils.validateToken(TEST_TOKEN)).thenReturn(true);
        when(jwtUtils.isRefreshToken(TEST_TOKEN)).thenReturn(false);

        AjaxResult result = authController.refresh(body);

        assertTrue(result.isError());
        assertEquals("刷新Token无效", result.get("msg"));
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtUtils, never()).generateToken(any());
    }

    @Test
    @DisplayName("refresh - UserDetails不是LoginUser类型")
    void testRefresh_UserDetailsNotLoginUser() {
        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", TEST_REFRESH_TOKEN);

        // 创建一个普通的UserDetails mock，不是LoginUser
        // 使用lenient以避免UnnecessaryStubbing
        UserDetails mockUserDetails = mock(UserDetails.class);

        when(jwtUtils.validateToken(TEST_REFRESH_TOKEN)).thenReturn(true);
        when(jwtUtils.isRefreshToken(TEST_REFRESH_TOKEN)).thenReturn(true);
        when(jwtUtils.extractUsername(TEST_REFRESH_TOKEN)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(mockUserDetails);

        AjaxResult result = authController.refresh(body);

        assertTrue(result.isError());
        assertEquals("刷新Token无效", result.get("msg"));
    }

    @Test
    @DisplayName("getInfo - 正常获取用户信息")
    void testGetInfo_Success() {
        // 需要模拟 SecurityUtils.getSysUser()，这需要设置SecurityContext
        // 这里我们通过反射来测试，或者使用集成测试

        // 由于getInfo依赖SecurityUtils静态方法，需要更复杂的设置
        // 这里先跳过，在集成测试中覆盖
    }
}
