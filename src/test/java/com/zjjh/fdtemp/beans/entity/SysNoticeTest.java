package com.zjjh.fdtemp.beans.entity;

import org.junit.jupiter.api.*;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysNotice 实体类单元测试
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SysNoticeTest {

    private SysNotice notice;

    @BeforeEach
    void setUp() {
        notice = new SysNotice();
    }

    @Test
    @Order(1)
    @DisplayName("设置和获取ID")
    void testId() {
        notice.setId("test-id-123");
        assertEquals("test-id-123", notice.getId());
    }

    @Test
    @Order(2)
    @DisplayName("设置和获取公告标题")
    void testNoticeTitle() {
        notice.setNoticeTitle("系统公告");
        assertEquals("系统公告", notice.getNoticeTitle());
    }

    @Test
    @Order(3)
    @DisplayName("设置和获取公告类型")
    void testNoticeType() {
        notice.setNoticeType("1");
        assertEquals("1", notice.getNoticeType());
        notice.setNoticeType("2");
        assertEquals("2", notice.getNoticeType());
    }

    @Test
    @Order(4)
    @DisplayName("设置和获取公告内容")
    void testNoticeContent() {
        notice.setNoticeContent("这是公告内容");
        assertEquals("这是公告内容", notice.getNoticeContent());
    }

    @Test
    @Order(5)
    @DisplayName("设置和获取状态")
    void testStatus() {
        notice.setStatus("0");
        assertEquals("0", notice.getStatus());
        notice.setStatus("1");
        assertEquals("1", notice.getStatus());
    }

    @Test
    @Order(10)
    @DisplayName("设置和获取创建用户")
    void testCreateUser() {
        notice.setCreateUser("admin");
        assertEquals("admin", notice.getCreateUser());
    }

    @Test
    @Order(11)
    @DisplayName("设置和获取创建时间")
    void testCreateTime() {
        Date now = new Date();
        notice.setCreateTime(now);
        assertEquals(now, notice.getCreateTime());
    }

    @Test
    @Order(20)
    @DisplayName("toString方法包含所有属性")
    void testToString() {
        notice.setId("1");
        notice.setNoticeTitle("系统通知");
        notice.setNoticeType("1");
        notice.setNoticeContent("通知内容");
        notice.setStatus("0");
        String str = notice.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("系统通知"));
        assertTrue(str.contains("通知内容"));
    }

    @Test
    @Order(30)
    @DisplayName("空值测试-null公告标题")
    void testNullNoticeTitle() {
        notice.setNoticeTitle(null);
        assertNull(notice.getNoticeTitle());
    }

    @Test
    @Order(31)
    @DisplayName("空值测试-null公告内容")
    void testNullNoticeContent() {
        notice.setNoticeContent(null);
        assertNull(notice.getNoticeContent());
    }

    @Test
    @Order(32)
    @DisplayName("空字符串测试")
    void testEmptyStrings() {
        notice.setNoticeTitle("");
        notice.setNoticeType("");
        notice.setNoticeContent("");
        assertEquals("", notice.getNoticeTitle());
        assertEquals("", notice.getNoticeType());
        assertEquals("", notice.getNoticeContent());
    }

    @Test
    @Order(33)
    @DisplayName("长内容测试")
    void testLongContent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("这是第").append(i).append("行内容。");
        }
        String longContent = sb.toString();
        notice.setNoticeContent(longContent);
        assertEquals(longContent, notice.getNoticeContent());
    }

    @Test
    @Order(34)
    @DisplayName("特殊字符测试-内容")
    void testSpecialCharactersInContent() {
        String specialChars = "<script>alert('xss')</script>&\"'{}[]\\";
        notice.setNoticeContent(specialChars);
        assertEquals(specialChars, notice.getNoticeContent());
    }

    @Test
    @Order(40)
    @DisplayName("公告类型-通知(1)")
    void testNoticeTypeNotification() {
        notice.setNoticeType("1");
        assertEquals("1", notice.getNoticeType());
    }

    @Test
    @Order(41)
    @DisplayName("公告类型-公告(2)")
    void testNoticeTypeAnnouncement() {
        notice.setNoticeType("2");
        assertEquals("2", notice.getNoticeType());
    }

    @Test
    @Order(50)
    @DisplayName("HTML内容-简单标签")
    void testHtmlContentSimple() {
        String htmlContent = "<p>这是段落</p>";
        notice.setNoticeContent(htmlContent);
        assertEquals(htmlContent, notice.getNoticeContent());
    }

    @Test
    @Order(51)
    @DisplayName("HTML内容-复杂标签")
    void testHtmlContentComplex() {
        String htmlContent = "<div><h1>标题</h1><p>段落<strong>加粗</strong></p></div>";
        notice.setNoticeContent(htmlContent);
        assertEquals(htmlContent, notice.getNoticeContent());
    }

    @Test
    @Order(60)
    @DisplayName("params参数初始化测试")
    void testParamsInitialization() {
        assertNotNull(notice.getParams());
        assertTrue(notice.getParams().isEmpty());
    }
}
