package com.zjjh.fdtemp.controller.auth;

import com.zjjh.fdtemp.beans.LoginUser;
import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.common.utils.security.JwtUtils;
import com.zjjh.fdtemp.common.utils.security.SecurityUtils;
import com.zjjh.fdtemp.common.utils.security.TokenBlacklist;
import com.zjjh.fdtemp.service.SysMenuService;
import com.zjjh.fdtemp.service.SysRoleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    @Autowired
    private SysMenuService menuService;

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public AjaxResult login(@RequestBody Map<String, String> loginBody) {
        String username = loginBody.get("username");
        String password = loginBody.get("password");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String token = jwtUtils.generateToken(loginUser);
        String refreshToken = jwtUtils.generateRefreshToken(loginUser);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("refreshToken", refreshToken);

        return AjaxResult.success("登录成功", data);
    }

    @PostMapping("/logout")
    public AjaxResult logout(HttpServletRequest request) {
        String token = jwtUtils.extractTokenFromRequest(request);
        if (token != null && jwtUtils.validateToken(token)) {
            long expiration = jwtUtils.extractExpiration(token).getTime();
            tokenBlacklist.addToBlacklist(token, expiration);
        }
        return AjaxResult.success("退出成功");
    }

    @PostMapping("/refresh")
    public AjaxResult refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return AjaxResult.error("刷新Token不能为空");
        }
        if (jwtUtils.validateToken(refreshToken) && jwtUtils.isRefreshToken(refreshToken)) {
            String username = jwtUtils.extractUsername(refreshToken);
            // 从 refresh token 重新加载用户信息，而不是从 SecurityContextHolder 获取
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails instanceof LoginUser loginUser) {
                String newToken = jwtUtils.generateToken(loginUser);
                Map<String, Object> data = new HashMap<>();
                data.put("token", newToken);
                return AjaxResult.success("刷新成功", data);
            }
        }
        return AjaxResult.error("刷新Token无效");
    }

    @GetMapping("/info")
    public AjaxResult getInfo() {
        SysUser user = SecurityUtils.getSysUser();
        Set<String> roles = roleService.selectRoleKeys(user.getId());
        Set<String> permissions = menuService.selectPermsByUserId(user.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("roles", roles);
        data.put("permissions", permissions);
        return AjaxResult.success(data);
    }
}
