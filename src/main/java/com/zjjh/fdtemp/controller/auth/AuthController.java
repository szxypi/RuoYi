package com.zjjh.fdtemp.controller.auth;

import com.zjjh.fdtemp.beans.LoginUser;
import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.common.utils.StringUtils;
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

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtils.generateToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("refreshToken", refreshToken);
        return AjaxResult.success("登录成功", data);
    }

    @PostMapping("/logout")
    public AjaxResult logout(HttpServletRequest request, @RequestBody(required = false) Map<String, String> body) {
        String token = jwtUtils.extractTokenFromRequest(request);
        blacklistTokenIfValid(token, false);

        if (body != null && body.containsKey("refreshToken")) {
            blacklistTokenIfValid(body.get("refreshToken"), true);
        }
        return AjaxResult.success("退出成功");
    }

    @PostMapping("/refresh")
    public AjaxResult refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (StringUtils.isNotEmpty(refreshToken) && jwtUtils.validateToken(refreshToken) && jwtUtils.isRefreshToken(refreshToken)) {
            if (!tokenBlacklist.isBlacklisted(refreshToken)) {
                String username = jwtUtils.extractUsername(refreshToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                String newAccessToken = jwtUtils.generateToken(userDetails);
                String newRefreshToken = jwtUtils.generateRefreshToken(userDetails);

                // 将旧的 refreshToken 加入黑名单
                blacklistTokenIfValid(refreshToken, true);

                Map<String, Object> data = new HashMap<>();
                data.put("token", newAccessToken);
                data.put("refreshToken", newRefreshToken);
                return AjaxResult.success("刷新成功", data);
            }
        }
        return AjaxResult.error("刷新Token无效");
    }

    @GetMapping("/info")
    public AjaxResult getInfo() {
        SysUser user = SecurityUtils.getSysUser();
        Set<String> roles = roleService.selectRoleKeys(user.getId());

        // 修复：保持与 UserDetailsServiceImpl 一致的权限获取逻辑
        // 管理员获取所有权限，非管理员通过 userId 查询
        Set<String> permissions;
        if (user.isAdmin()) {
            permissions = menuService.selectPermsAll();
        } else {
            permissions = menuService.selectPermsByUserId(user.getId());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("roles", roles);
        data.put("permissions", permissions);
        return AjaxResult.success(data);
    }

    private void blacklistTokenIfValid(String token, boolean requireRefreshToken) {
        if (StringUtils.isEmpty(token) || !jwtUtils.validateToken(token)) {
            return;
        }
        if (requireRefreshToken && !jwtUtils.isRefreshToken(token)) {
            return;
        }
        tokenBlacklist.addToBlacklist(token, jwtUtils.extractExpiration(token).getTime());
    }
}
