package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.FdtempApplication;
import com.zjjh.fdtemp.TestConfig;
import com.zjjh.fdtemp.beans.LoginUser;
import com.zjjh.fdtemp.beans.entity.SysPost;
import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.service.SysPostService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FdtempApplication.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class SysPostServiceImplTest {

    @Autowired
    private SysPostService postService;

    private static final String CEO_POST_ID = "1";
    private static final String PM_POST_ID = "2";
    private static final String USER_POST_ID = "4";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        SysUser adminUser = new SysUser();
        adminUser.setId("1");
        adminUser.setLoginName("admin");
        adminUser.setUserName("超级管理员");
        adminUser.setStatus("0");

        Set<String> permissions = new HashSet<>();
        permissions.add("*:*:*");
        LoginUser loginUser = new LoginUser(adminUser, permissions);

        Authentication auth = new UsernamePasswordAuthenticationToken(
            loginUser, null, loginUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @Order(1)
    @DisplayName("1.1 查询所有岗位列表")
    void testSelectPostList() {
        SysPost query = new SysPost();
        List<SysPost> list = postService.selectPostList(query);
        assertNotNull(list);
        assertTrue(list.size() >= 4);
    }

    @Test
    @Order(2)
    @DisplayName("1.2 根据ID查询岗位")
    void testSelectPostById() {
        SysPost post = postService.selectPostById(CEO_POST_ID);
        assertNotNull(post);
        assertEquals("董事长", post.getPostName());
        assertEquals("ceo", post.getPostCode());
    }

    @Test
    @Order(3)
    @DisplayName("1.3 查询所有岗位")
    void testSelectPostAll() {
        List<SysPost> list = postService.selectPostAll();
        assertNotNull(list);
        assertTrue(list.size() >= 4);
    }

    @Test
    @Order(4)
    @DisplayName("1.4 根据用户ID查询岗位")
    void testSelectPostsByUserId() {
        List<SysPost> posts = postService.selectPostsByUserId("1");
        assertNotNull(posts);
        boolean hasCeoPost = posts.stream()
                .filter(p -> CEO_POST_ID.equals(p.getId()))
                .findFirst()
                .map(SysPost::isFlag)
                .orElse(false);
        assertTrue(hasCeoPost);
    }

    @Test
    @Order(5)
    @DisplayName("1.5 查询岗位使用数量")
    void testCountUserPostById() {
        int count = postService.countUserPostById(CEO_POST_ID);
        assertTrue(count >= 1);
    }

    @Test
    @Order(10)
    @DisplayName("2.1 校验岗位名称唯一性 - 已存在")
    void testCheckPostNameUnique_Existing() {
        SysPost post = new SysPost();
        post.setPostName("董事长");
        boolean result = postService.checkPostNameUnique(post);
        assertEquals(UserConstants.NOT_UNIQUE, result);
    }

    @Test
    @Order(11)
    @DisplayName("2.2 校验岗位名称唯一性 - 不存在")
    void testCheckPostNameUnique_New() {
        SysPost post = new SysPost();
        post.setPostName("新岗位测试名称");
        boolean result = postService.checkPostNameUnique(post);
        assertEquals(UserConstants.UNIQUE, result);
    }

    @Test
    @Order(12)
    @DisplayName("2.3 校验岗位编码唯一性 - 已存在")
    void testCheckPostCodeUnique_Existing() {
        SysPost post = new SysPost();
        post.setPostCode("ceo");
        boolean result = postService.checkPostCodeUnique(post);
        assertEquals(UserConstants.NOT_UNIQUE, result);
    }

    @Test
    @Order(13)
    @DisplayName("2.4 校验岗位编码唯一性 - 不存在")
    void testCheckPostCodeUnique_New() {
        SysPost post = new SysPost();
        post.setPostCode("new_post_code_test");
        boolean result = postService.checkPostCodeUnique(post);
        assertEquals(UserConstants.UNIQUE, result);
    }

    @Test
    @Order(20)
    @DisplayName("3.1 新增岗位成功")
    @Disabled("H2 不支持 MySQL 的 sysdate() 函数")
    void testInsertPost() {
        SysPost post = createTestPost("测试岗位", "test_post");
        int result = postService.insertPost(post);
        assertTrue(result > 0);
    }

    @Test
    @Order(30)
    @DisplayName("4.1 修改岗位成功")
    @Disabled("H2 不支持 MySQL 的 sysdate() 函数")
    void testUpdatePost() {
        SysPost post = postService.selectPostById(PM_POST_ID);
        assertNotNull(post);
        String newPostName = "修改后的项目经理";
        post.setPostName(newPostName);
        int result = postService.updatePost(post);
        assertTrue(result >= 0);
    }

    @Test
    @Order(40)
    @DisplayName("5.1 删除已分配用户的岗位 - 抛出异常")
    void testDeletePostByIds_Allocated() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            postService.deletePostByIds(CEO_POST_ID);
        });
        assertTrue(exception.getMessage().contains("已分配"));
    }

    @Test
    @Order(60)
    @DisplayName("7.1 查询不存在的岗位ID")
    void testSelectPostById_NotExist() {
        SysPost post = postService.selectPostById("999999");
        assertNull(post);
    }

    private SysPost createTestPost(String postName, String postCode) {
        SysPost post = new SysPost();
        post.setPostName(postName);
        post.setPostCode(postCode);
        post.setPostSort("99");
        post.setStatus("0");
        return post;
    }
}
