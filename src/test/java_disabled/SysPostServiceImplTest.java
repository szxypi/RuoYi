package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.FdtempApplication;
import com.zjjh.fdtemp.TestConfig;
import com.zjjh.fdtemp.beans.entity.SysPost;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.service.SysPostService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysPostService 单元测试类
 */
@SpringBootTest(classes = FdtempApplication.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class SysPostServiceImplTest {

    @Autowired
    private SysPostService postService;

    private static final String CEO_POST_ID = "1";       // 董事长
    private static final String PM_POST_ID = "2";        // 项目经理
    private static final String HR_POST_ID = "3";        // 人力资源
    private static final String USER_POST_ID = "4";      // 普通员工

    // ============================================
    // 查询测试
    // ============================================

    @Test
    @Order(1)
    @DisplayName("1.1 查询所有岗位列表")
    void testSelectPostList() {
        SysPost query = new SysPost();
        List<SysPost> list = postService.selectPostList(query);
        assertNotNull(list);
        assertTrue(list.size() >= 4, "应该至少有4个测试岗位");
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

        // 验证用户拥有的岗位被标记
        boolean hasCeoPost = posts.stream()
                .filter(p -> CEO_POST_ID.equals(p.getId()))
                .findFirst()
                .map(SysPost::isFlag)
                .orElse(false);
        assertTrue(hasCeoPost, "admin用户应该拥有董事长岗位");
    }

    @Test
    @Order(5)
    @DisplayName("1.5 查询岗位使用数量")
    void testCountUserPostById() {
        int count = postService.countUserPostById(CEO_POST_ID);
        assertTrue(count >= 1, "董事长岗位应该有用户使用");
    }

    // ============================================
    // 唯一性校验测试
    // ============================================

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
    @DisplayName("2.3 校验岗位名称唯一性 - 修改时保持自己的名字")
    void testCheckPostNameUnique_Self() {
        SysPost post = new SysPost();
        post.setId(CEO_POST_ID);
        post.setPostName("董事长");
        boolean result = postService.checkPostNameUnique(post);
        assertEquals(UserConstants.UNIQUE, result);
    }

    @Test
    @Order(13)
    @DisplayName("2.4 校验岗位编码唯一性 - 已存在")
    void testCheckPostCodeUnique_Existing() {
        SysPost post = new SysPost();
        post.setPostCode("ceo");
        boolean result = postService.checkPostCodeUnique(post);
        assertEquals(UserConstants.NOT_UNIQUE, result);
    }

    @Test
    @Order(14)
    @DisplayName("2.5 校验岗位编码唯一性 - 不存在")
    void testCheckPostCodeUnique_New() {
        SysPost post = new SysPost();
        post.setPostCode("new_post_code_test");
        boolean result = postService.checkPostCodeUnique(post);
        assertEquals(UserConstants.UNIQUE, result);
    }

    @Test
    @Order(15)
    @DisplayName("2.6 校验岗位编码唯一性 - 修改时保持自己的编码")
    void testCheckPostCodeUnique_Self() {
        SysPost post = new SysPost();
        post.setId(CEO_POST_ID);
        post.setPostCode("ceo");
        boolean result = postService.checkPostCodeUnique(post);
        assertEquals(UserConstants.UNIQUE, result);
    }

    // ============================================
    // 新增测试
    // ============================================

    @Test
    @Order(20)
    @DisplayName("3.1 新增岗位成功")
    void testInsertPost() {
        SysPost post = createTestPost("测试岗位", "test_post");

        int result = postService.insertPost(post);
        assertTrue(result > 0);
    }

    @Test
    @Order(21)
    @DisplayName("3.2 新增多个岗位")
    void testInsertMultiplePosts() {
        SysPost post1 = createTestPost("测试岗位A", "test_post_a");
        SysPost post2 = createTestPost("测试岗位B", "test_post_b");

        int result1 = postService.insertPost(post1);
        int result2 = postService.insertPost(post2);

        assertTrue(result1 > 0);
        assertTrue(result2 > 0);
    }

    // ============================================
    // 修改测试
    // ============================================

    @Test
    @Order(30)
    @DisplayName("4.1 修改岗位成功")
    void testUpdatePost() {
        SysPost post = postService.selectPostById(PM_POST_ID);
        assertNotNull(post);

        String newPostName = "修改后的项目经理";
        post.setPostName(newPostName);

        int result = postService.updatePost(post);
        assertTrue(result >= 0);

        SysPost updated = postService.selectPostById(PM_POST_ID);
        assertEquals(newPostName, updated.getPostName());
    }

    @Test
    @Order(31)
    @DisplayName("4.2 修改岗位状态")
    void testUpdatePostStatus() {
        SysPost post = postService.selectPostById(USER_POST_ID);
        post.setStatus("1"); // 停用

        int result = postService.updatePost(post);
        assertTrue(result >= 0);

        SysPost updated = postService.selectPostById(USER_POST_ID);
        assertEquals("1", updated.getStatus());
    }

    // ============================================
    // 删除测试
    // ============================================

    @Test
    @Order(40)
    @DisplayName("5.1 删除无用户的岗位成功")
    void testDeletePostByIds_Success() {
        // 先新增一个岗位用于删除
        SysPost post = createTestPost("待删除岗位", "to_be_deleted");
        postService.insertPost(post);

        // 查找刚创建的岗位
        List<SysPost> posts = postService.selectPostList(new SysPost());
        SysPost created = posts.stream()
                .filter(p -> "待删除岗位".equals(p.getPostName()))
                .findFirst()
                .orElse(null);
        assertNotNull(created);

        int result = postService.deletePostByIds(created.getId());
        assertTrue(result >= 0);
    }

    @Test
    @Order(41)
    @DisplayName("5.2 删除已分配用户的岗位 - 抛出异常")
    void testDeletePostByIds_Allocated() {
        // 董事长岗位已分配给用户
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            postService.deletePostByIds(CEO_POST_ID);
        });
        assertTrue(exception.getMessage().contains("已分配"));
    }

    @Test
    @Order(42)
    @DisplayName("5.3 批量删除岗位")
    void testDeletePostByIds_Batch() {
        // 先新增两个岗位用于删除
        SysPost post1 = createTestPost("批量删除岗位1", "batch_delete_1");
        SysPost post2 = createTestPost("批量删除岗位2", "batch_delete_2");
        postService.insertPost(post1);
        postService.insertPost(post2);

        // 查找刚创建的岗位
        List<SysPost> posts = postService.selectPostList(new SysPost());
        String id1 = posts.stream()
                .filter(p -> "批量删除岗位1".equals(p.getPostName()))
                .findFirst()
                .map(SysPost::getId)
                .orElse(null);
        String id2 = posts.stream()
                .filter(p -> "批量删除岗位2".equals(p.getPostName()))
                .findFirst()
                .map(SysPost::getId)
                .orElse(null);

        assertNotNull(id1);
        assertNotNull(id2);

        int result = postService.deletePostByIds(id1 + "," + id2);
        assertTrue(result >= 0);
    }

    // ============================================
    // 排序测试
    // ============================================

    @Test
    @Order(50)
    @DisplayName("6.1 验证岗位排序")
    void testPostSortOrder() {
        List<SysPost> posts = postService.selectPostAll();

        // 验证排序正确
        for (int i = 0; i < posts.size() - 1; i++) {
            SysPost current = posts.get(i);
            SysPost next = posts.get(i + 1);

            // 排序应该是递增的
            int currentSort = Integer.parseInt(current.getPostSort());
            int nextSort = Integer.parseInt(next.getPostSort());
            assertTrue(currentSort <= nextSort,
                    String.format("岗位 %s 的排序(%d) 应该 <= 岗位 %s 的排序(%d)",
                            current.getPostName(), currentSort,
                            next.getPostName(), nextSort));
        }
    }

    // ============================================
    // 边界条件测试
    // ============================================

    @Test
    @Order(60)
    @DisplayName("7.1 查询不存在的岗位ID")
    void testSelectPostById_NotExist() {
        SysPost post = postService.selectPostById("999999");
        assertNull(post);
    }

    @Test
    @Order(61)
    @DisplayName("7.2 查询没有岗位的用户")
    void testSelectPostsByUserId_NoPosts() {
        // 创建一个新用户，没有岗位
        List<SysPost> posts = postService.selectPostsByUserId("999");
        assertNotNull(posts);
    }

    // ============================================
    // 辅助方法
    // ============================================

    private SysPost createTestPost(String postName, String postCode) {
        SysPost post = new SysPost();
        post.setPostName(postName);
        post.setPostCode(postCode);
        post.setPostSort("99");
        post.setStatus("0");
        return post;
    }
}
