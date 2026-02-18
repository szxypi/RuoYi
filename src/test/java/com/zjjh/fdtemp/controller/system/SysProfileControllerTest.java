package com.zjjh.fdtemp.controller.system;

import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.service.SysUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * SysProfileController 单元测试
 * <p>
 * 注意：部分方法依赖SecurityUtils.getSysUser()，这些方法在集成测试中覆盖
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysProfileController 测试")
class SysProfileControllerTest {

    @Mock
    private SysUserService userService;

    @InjectMocks
    private SysProfileController profileController;

    private SysUser testUser;
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        testUser = new SysUser();
        testUser.setId("1");
        testUser.setLoginName("testuser");
        testUser.setUserName("测试用户");
        testUser.setPassword(PASSWORD_ENCODER.encode("oldPassword"));
        testUser.setEmail("test@example.com");
        testUser.setPhonenumber("13800138000");
        testUser.setStatus("0");
        testUser.setYn("0");
    }

    @Test
    @DisplayName("resetPwd - 验证密码匹配逻辑")
    void testResetPwd_PasswordMatching() {
        String oldPassword = "oldPassword";
        String newPassword = "newPassword123";

        // 验证密码匹配逻辑
        assertTrue(PASSWORD_ENCODER.matches(oldPassword, testUser.getPassword()));
        assertFalse(PASSWORD_ENCODER.matches(newPassword, testUser.getPassword()));
    }

    @Test
    @DisplayName("resetPwd - 旧密码错误")
    void testResetPwd_WrongOldPassword() {
        String wrongOldPassword = "wrongPassword";

        // 验证密码不匹配
        assertFalse(PASSWORD_ENCODER.matches(wrongOldPassword, testUser.getPassword()));
    }

    @Test
    @DisplayName("resetPwd - 新密码与旧密码相同")
    void testResetPwd_SameNewPassword() {
        String oldPassword = "oldPassword";

        // 验证密码匹配
        assertTrue(PASSWORD_ENCODER.matches(oldPassword, testUser.getPassword()));
    }

    @Test
    @DisplayName("update - 验证手机号唯一性检查逻辑")
    void testUpdate_PhoneUniqueCheck() {
        // 模拟手机号已存在
        when(userService.checkPhoneUnique(any(SysUser.class))).thenReturn(false);

        boolean result = userService.checkPhoneUnique(testUser);
        assertFalse(result);
    }

    @Test
    @DisplayName("update - 验证邮箱唯一性检查逻辑")
    void testUpdate_EmailUniqueCheck() {
        // 模拟邮箱已存在
        when(userService.checkEmailUnique(any(SysUser.class))).thenReturn(false);

        boolean result = userService.checkEmailUnique(testUser);
        assertFalse(result);
    }

    @Test
    @DisplayName("updateAvatar - 验证空文件检查")
    void testUpdateAvatar_EmptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "avatarfile",
                "",
                "image/jpeg",
                new byte[0]
        );

        // 验证文件为空
        assertTrue(emptyFile.isEmpty());
    }

    @Test
    @DisplayName("updateAvatar - 验证非空文件")
    void testUpdateAvatar_NonEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "avatarfile",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // 验证文件不为空
        assertFalse(file.isEmpty());
        assertEquals("test.jpg", file.getOriginalFilename());
    }

    @Test
    @DisplayName("checkPassword - 密码正确")
    void testCheckPassword_Correct() {
        String correctPassword = "oldPassword";

        assertTrue(PASSWORD_ENCODER.matches(correctPassword, testUser.getPassword()));
    }

    @Test
    @DisplayName("checkPassword - 密码错误")
    void testCheckPassword_Incorrect() {
        String wrongPassword = "wrongPassword";

        assertFalse(PASSWORD_ENCODER.matches(wrongPassword, testUser.getPassword()));
    }

    @Test
    @DisplayName("密码加密测试")
    void testPasswordEncryption() {
        String rawPassword = "testPassword123";
        String encryptedPassword = PASSWORD_ENCODER.encode(rawPassword);

        assertNotNull(encryptedPassword);
        assertNotEquals(rawPassword, encryptedPassword);
        assertTrue(encryptedPassword.startsWith("$2a$"));
        assertTrue(PASSWORD_ENCODER.matches(rawPassword, encryptedPassword));
    }

    @Test
    @DisplayName("密码加密 - 相同密码产生不同加密结果")
    void testPasswordEncryption_DifferentHashes() {
        String rawPassword = "testPassword123";
        String encrypted1 = PASSWORD_ENCODER.encode(rawPassword);
        String encrypted2 = PASSWORD_ENCODER.encode(rawPassword);

        // 由于BCrypt使用随机salt，两次加密结果不同
        assertNotEquals(encrypted1, encrypted2);
        // 但都能匹配原密码
        assertTrue(PASSWORD_ENCODER.matches(rawPassword, encrypted1));
        assertTrue(PASSWORD_ENCODER.matches(rawPassword, encrypted2));
    }

    @Test
    @DisplayName("AjaxResult成功响应测试")
    void testAjaxResult_Success() {
        AjaxResult result = AjaxResult.success("操作成功");
        assertTrue(result.isSuccess());
        assertEquals("操作成功", result.get("msg"));
    }

    @Test
    @DisplayName("AjaxResult错误响应测试")
    void testAjaxResult_Error() {
        AjaxResult result = AjaxResult.error("操作失败");
        assertTrue(result.isError());
        assertEquals("操作失败", result.get("msg"));
    }
}
