package com.zjjh.fdtemp.controller.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjjh.fdtemp.beans.entity.SysNotice;
import com.zjjh.fdtemp.service.SysNoticeService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SysNoticeController 单元测试
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SysNoticeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SysNoticeService noticeService;

    @InjectMocks
    private SysNoticeController noticeController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(noticeController).build();
        objectMapper = new ObjectMapper();
    }

    private SysNotice createTestNotice(String id, String title, String type, String content, String status) {
        SysNotice notice = new SysNotice();
        notice.setId(id);
        notice.setNoticeTitle(title);
        notice.setNoticeType(type);
        notice.setNoticeContent(content);
        notice.setStatus(status);
        return notice;
    }

    // ==================== list 测试 ====================

    @Test
    @Order(1)
    @DisplayName("查询公告列表-成功")
    void testList_Success() throws Exception {
        // Arrange
        List<SysNotice> notices = new ArrayList<>();
        notices.add(createTestNotice("1", "系统通知", "1", "通知内容", "0"));
        notices.add(createTestNotice("2", "系统公告", "2", "公告内容", "0"));

        when(noticeService.selectNoticeList(any(SysNotice.class))).thenReturn(notices);

        // Act & Assert
        mockMvc.perform(post("/system/notice/list")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows", hasSize(2)));
    }

    @Test
    @Order(2)
    @DisplayName("查询公告列表-带条件查询")
    void testList_WithCondition() throws Exception {
        // Arrange
        List<SysNotice> notices = new ArrayList<>();
        notices.add(createTestNotice("1", "系统通知", "1", "通知内容", "0"));

        when(noticeService.selectNoticeList(any(SysNotice.class))).thenReturn(notices);

        // Act & Assert
        mockMvc.perform(post("/system/notice/list")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("noticeTitle", "通知")
                        .param("noticeType", "1")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows", hasSize(1)));
    }

    @Test
    @Order(3)
    @DisplayName("查询公告列表-空列表")
    void testList_Empty() throws Exception {
        // Arrange
        when(noticeService.selectNoticeList(any(SysNotice.class))).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(post("/system/notice/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows", hasSize(0)));
    }

    @Test
    @Order(4)
    @DisplayName("查询公告列表-按类型查询")
    void testList_ByType() throws Exception {
        // Arrange
        List<SysNotice> notices = new ArrayList<>();
        notices.add(createTestNotice("1", "通知1", "1", "内容1", "0"));
        notices.add(createTestNotice("2", "通知2", "1", "内容2", "0"));

        when(noticeService.selectNoticeList(any(SysNotice.class))).thenReturn(notices);

        // Act & Assert
        mockMvc.perform(post("/system/notice/list")
                        .param("noticeType", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows", hasSize(2)));
    }

    @Test
    @Order(5)
    @DisplayName("查询公告列表-按状态查询")
    void testList_ByStatus() throws Exception {
        // Arrange
        List<SysNotice> notices = new ArrayList<>();
        notices.add(createTestNotice("1", "正常公告", "1", "内容", "0"));

        when(noticeService.selectNoticeList(any(SysNotice.class))).thenReturn(notices);

        // Act & Assert
        mockMvc.perform(post("/system/notice/list")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows", hasSize(1)));
    }

    // ==================== addSave 测试 ====================

    @Test
    @Order(10)
    @DisplayName("新增公告-成功(通知类型)")
    void testAddSave_NoticeType_Success() throws Exception {
        // Arrange
        when(noticeService.insertNotice(any(SysNotice.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/notice/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("noticeTitle", "新通知")
                        .param("noticeType", "1")
                        .param("noticeContent", "通知内容")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(11)
    @DisplayName("新增公告-成功(公告类型)")
    void testAddSave_AnnouncementType_Success() throws Exception {
        // Arrange
        when(noticeService.insertNotice(any(SysNotice.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/notice/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("noticeTitle", "新公告")
                        .param("noticeType", "2")
                        .param("noticeContent", "公告内容")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(12)
    @DisplayName("新增公告-插入失败")
    void testAddSave_InsertFailed() throws Exception {
        // Arrange
        when(noticeService.insertNotice(any(SysNotice.class))).thenReturn(0);

        // Act & Assert
        mockMvc.perform(post("/system/notice/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("noticeTitle", "新通知")
                        .param("noticeType", "1")
                        .param("noticeContent", "通知内容"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(13)
    @DisplayName("新增公告-带HTML内容")
    void testAddSave_HtmlContent() throws Exception {
        // Arrange
        when(noticeService.insertNotice(any(SysNotice.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/notice/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("noticeTitle", "HTML通知")
                        .param("noticeType", "1")
                        .param("noticeContent", "<p>这是<strong>HTML</strong>内容</p>"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    // ==================== editSave 测试 ====================

    @Test
    @Order(20)
    @DisplayName("修改公告-成功")
    void testEditSave_Success() throws Exception {
        // Arrange
        when(noticeService.updateNotice(any(SysNotice.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/notice/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("noticeTitle", "修改后的通知")
                        .param("noticeType", "1")
                        .param("noticeContent", "修改后的内容")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(21)
    @DisplayName("修改公告-更新失败")
    void testEditSave_UpdateFailed() throws Exception {
        // Arrange
        when(noticeService.updateNotice(any(SysNotice.class))).thenReturn(0);

        // Act & Assert
        mockMvc.perform(post("/system/notice/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "999")
                        .param("noticeTitle", "修改后的通知")
                        .param("noticeType", "1")
                        .param("noticeContent", "修改后的内容"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(22)
    @DisplayName("修改公告-修改状态为关闭")
    void testEditSave_CloseStatus() throws Exception {
        // Arrange
        when(noticeService.updateNotice(any(SysNotice.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/notice/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("noticeTitle", "关闭的通知")
                        .param("noticeType", "1")
                        .param("noticeContent", "内容")
                        .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(23)
    @DisplayName("修改公告-从通知改为公告")
    void testEditSave_ChangeType() throws Exception {
        // Arrange
        when(noticeService.updateNotice(any(SysNotice.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/notice/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("noticeTitle", "改为公告")
                        .param("noticeType", "2")  // 从1改为2
                        .param("noticeContent", "内容"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    // ==================== remove 测试 ====================

    @Test
    @Order(30)
    @DisplayName("删除公告-成功")
    void testRemove_Success() throws Exception {
        // Arrange
        when(noticeService.deleteNoticeByIds("1,2")).thenReturn(2);

        // Act & Assert
        mockMvc.perform(post("/system/notice/remove")
                        .param("ids", "1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(noticeService).deleteNoticeByIds("1,2");
    }

    @Test
    @Order(31)
    @DisplayName("删除公告-单个ID")
    void testRemove_SingleId() throws Exception {
        // Arrange
        when(noticeService.deleteNoticeByIds("1")).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/notice/remove")
                        .param("ids", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(32)
    @DisplayName("删除公告-删除失败")
    void testRemove_Failed() throws Exception {
        // Arrange
        when(noticeService.deleteNoticeByIds("999")).thenReturn(0);

        // Act & Assert
        mockMvc.perform(post("/system/notice/remove")
                        .param("ids", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(33)
    @DisplayName("删除公告-多个ID")
    void testRemove_MultipleIds() throws Exception {
        // Arrange
        when(noticeService.deleteNoticeByIds("1,2,3,4,5")).thenReturn(5);

        // Act & Assert
        mockMvc.perform(post("/system/notice/remove")
                        .param("ids", "1,2,3,4,5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    // ==================== 边界条件测试 ====================

    @Test
    @Order(40)
    @DisplayName("边界条件-公告标题超长")
    void testAddSave_TitleTooLong() throws Exception {
        // Arrange
        when(noticeService.insertNotice(any(SysNotice.class))).thenReturn(1);
        String longTitle = "a".repeat(50); // 50个字符

        // Act & Assert
        mockMvc.perform(post("/system/notice/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("noticeTitle", longTitle)
                        .param("noticeType", "1")
                        .param("noticeContent", "内容"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(41)
    @DisplayName("边界条件-公告标题包含特殊字符")
    void testAddSave_SpecialCharacters() throws Exception {
        // Arrange
        when(noticeService.insertNotice(any(SysNotice.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/notice/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("noticeTitle", "<script>alert('xss')</script>")
                        .param("noticeType", "1")
                        .param("noticeContent", "内容"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(42)
    @DisplayName("边界条件-公告内容包含脚本")
    void testAddSave_ScriptContent() throws Exception {
        // Arrange
        when(noticeService.insertNotice(any(SysNotice.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/notice/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("noticeTitle", "测试公告")
                        .param("noticeType", "1")
                        .param("noticeContent", "<script>alert('xss')</script>公告内容"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(43)
    @DisplayName("边界条件-空内容")
    void testAddSave_EmptyContent() throws Exception {
        // Arrange
        when(noticeService.insertNotice(any(SysNotice.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/notice/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("noticeTitle", "空内容公告")
                        .param("noticeType", "1")
                        .param("noticeContent", ""))
                .andExpect(status().isOk());
    }

    @Test
    @Order(44)
    @DisplayName("边界条件-删除空字符串ID")
    void testRemove_EmptyString() throws Exception {
        // Arrange
        when(noticeService.deleteNoticeByIds("")).thenReturn(0);

        // Act & Assert
        mockMvc.perform(post("/system/notice/remove")
                        .param("ids", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(45)
    @DisplayName("边界条件-通知类型为空")
    void testAddSave_EmptyType() throws Exception {
        // Arrange
        when(noticeService.insertNotice(any(SysNotice.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/notice/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("noticeTitle", "无类型公告")
                        .param("noticeType", "")
                        .param("noticeContent", "内容"))
                .andExpect(status().isOk());
    }
}
