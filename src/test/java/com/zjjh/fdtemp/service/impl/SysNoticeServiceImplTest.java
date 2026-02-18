package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysNotice;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.dao.SysNoticeDao;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SysNoticeService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SysNoticeServiceImplTest {

    @Mock
    private SysNoticeDao noticeMapper;

    @InjectMocks
    private SysNoticeServiceImpl noticeService;

    private MockedStatic<Convert> convertMock;

    @BeforeEach
    void setUp() {
        convertMock = mockStatic(Convert.class);
    }

    @AfterEach
    void tearDown() {
        convertMock.close();
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

    // ==================== selectNoticeById 测试 ====================

    @Test
    @Order(1)
    @DisplayName("根据ID查询公告-正常情况")
    void testSelectNoticeById_Success() {
        SysNotice expected = createTestNotice("1", "测试公告", "1", "公告内容", "0");
        when(noticeMapper.selectNoticeById("1")).thenReturn(expected);

        SysNotice result = noticeService.selectNoticeById("1");

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("测试公告", result.getNoticeTitle());
        assertEquals("1", result.getNoticeType());
    }

    @Test
    @Order(2)
    @DisplayName("根据ID查询公告-不存在")
    void testSelectNoticeById_NotFound() {
        when(noticeMapper.selectNoticeById("nonexistent")).thenReturn(null);

        SysNotice result = noticeService.selectNoticeById("nonexistent");

        assertNull(result);
    }

    // ==================== selectNoticeList 测试 ====================

    @Test
    @Order(10)
    @DisplayName("查询公告列表-正常情况")
    void testSelectNoticeList_Success() {
        List<SysNotice> expectedList = new ArrayList<>();
        expectedList.add(createTestNotice("1", "公告1", "1", "内容1", "0"));
        expectedList.add(createTestNotice("2", "公告2", "2", "内容2", "0"));

        when(noticeMapper.selectNoticeList(any(SysNotice.class))).thenReturn(expectedList);

        List<SysNotice> result = noticeService.selectNoticeList(new SysNotice());

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @Order(11)
    @DisplayName("查询公告列表-带条件查询")
    void testSelectNoticeList_WithCondition() {
        SysNotice query = new SysNotice();
        query.setNoticeType("1");
        query.setStatus("0");

        List<SysNotice> expectedList = new ArrayList<>();
        expectedList.add(createTestNotice("1", "通知1", "1", "内容1", "0"));

        when(noticeMapper.selectNoticeList(any(SysNotice.class))).thenReturn(expectedList);

        List<SysNotice> result = noticeService.selectNoticeList(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getNoticeType());
        verify(noticeMapper).selectNoticeList(query);
    }

    @Test
    @Order(12)
    @DisplayName("查询公告列表-空列表")
    void testSelectNoticeList_EmptyList() {
        when(noticeMapper.selectNoticeList(any(SysNotice.class))).thenReturn(new ArrayList<>());

        List<SysNotice> result = noticeService.selectNoticeList(new SysNotice());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== insertNotice 测试 ====================

    @Test
    @Order(20)
    @DisplayName("新增公告-成功")
    void testInsertNotice_Success() {
        SysNotice notice = createTestNotice(null, "新公告", "1", "新内容", "0");
        when(noticeMapper.insertNotice(any(SysNotice.class))).thenReturn(1);

        int result = noticeService.insertNotice(notice);

        assertEquals(1, result);
        verify(noticeMapper).insertNotice(notice);
    }

    @Test
    @Order(21)
    @DisplayName("新增公告-失败")
    void testInsertNotice_Failed() {
        SysNotice notice = createTestNotice(null, "新公告", "1", "新内容", "0");
        when(noticeMapper.insertNotice(any(SysNotice.class))).thenReturn(0);

        int result = noticeService.insertNotice(notice);

        assertEquals(0, result);
    }

    // ==================== updateNotice 测试 ====================

    @Test
    @Order(30)
    @DisplayName("更新公告-成功")
    void testUpdateNotice_Success() {
        SysNotice notice = createTestNotice("1", "更新后的公告", "1", "更新后的内容", "0");
        when(noticeMapper.updateNotice(any(SysNotice.class))).thenReturn(1);

        int result = noticeService.updateNotice(notice);

        assertEquals(1, result);
        verify(noticeMapper).updateNotice(notice);
    }

    @Test
    @Order(31)
    @DisplayName("更新公告-失败")
    void testUpdateNotice_Failed() {
        SysNotice notice = createTestNotice("999", "更新公告", "1", "内容", "0");
        when(noticeMapper.updateNotice(any(SysNotice.class))).thenReturn(0);

        int result = noticeService.updateNotice(notice);

        assertEquals(0, result);
    }

    // ==================== deleteNoticeByIds 测试 ====================

    @Test
    @Order(40)
    @DisplayName("删除公告-单个删除成功")
    void testDeleteNoticeByIds_Single() {
        String[] ids = {"1"};
        convertMock.when(() -> Convert.toStrArray("1")).thenReturn(ids);
        when(noticeMapper.deleteNoticeByIds(ids)).thenReturn(1);

        int result = noticeService.deleteNoticeByIds("1");

        assertEquals(1, result);
        verify(noticeMapper).deleteNoticeByIds(ids);
    }

    @Test
    @Order(41)
    @DisplayName("删除公告-批量删除成功")
    void testDeleteNoticeByIds_Multiple() {
        String[] ids = {"1", "2", "3"};
        convertMock.when(() -> Convert.toStrArray("1,2,3")).thenReturn(ids);
        when(noticeMapper.deleteNoticeByIds(ids)).thenReturn(3);

        int result = noticeService.deleteNoticeByIds("1,2,3");

        assertEquals(3, result);
        verify(noticeMapper).deleteNoticeByIds(ids);
    }

    @Test
    @Order(42)
    @DisplayName("删除公告-删除不存在的记录")
    void testDeleteNoticeByIds_NotExist() {
        String[] ids = {"999"};
        convertMock.when(() -> Convert.toStrArray("999")).thenReturn(ids);
        when(noticeMapper.deleteNoticeByIds(ids)).thenReturn(0);

        int result = noticeService.deleteNoticeByIds("999");

        assertEquals(0, result);
    }

    // ==================== 边界条件测试 ====================

    @Test
    @Order(50)
    @DisplayName("边界条件-标题超长")
    void testNotice_TitleTooLong() {
        String longTitle = "a".repeat(100);
        SysNotice notice = createTestNotice(null, longTitle, "1", "内容", "0");
        when(noticeMapper.insertNotice(any(SysNotice.class))).thenReturn(1);

        int result = noticeService.insertNotice(notice);

        assertEquals(1, result);
    }

    @Test
    @Order(51)
    @DisplayName("边界条件-内容为HTML")
    void testNotice_HtmlContent() {
        String htmlContent = "<html><body><h1>公告</h1><p>内容</p></body></html>";
        SysNotice notice = createTestNotice(null, "HTML公告", "1", htmlContent, "0");
        when(noticeMapper.insertNotice(any(SysNotice.class))).thenReturn(1);

        int result = noticeService.insertNotice(notice);

        assertEquals(1, result);
    }

    @Test
    @Order(52)
    @DisplayName("边界条件-空内容")
    void testNotice_EmptyContent() {
        SysNotice notice = createTestNotice(null, "空内容公告", "1", "", "0");
        when(noticeMapper.insertNotice(any(SysNotice.class))).thenReturn(1);

        int result = noticeService.insertNotice(notice);

        assertEquals(1, result);
    }

    @Test
    @Order(53)
    @DisplayName("边界条件-null内容")
    void testNotice_NullContent() {
        SysNotice notice = createTestNotice(null, "公告", "1", null, "0");
        when(noticeMapper.insertNotice(any(SysNotice.class))).thenReturn(1);

        int result = noticeService.insertNotice(notice);

        assertEquals(1, result);
    }

    // ==================== 状态相关测试 ====================

    @Test
    @Order(60)
    @DisplayName("状态测试-正常状态")
    void testNoticeStatus_Normal() {
        SysNotice notice = createTestNotice("1", "正常公告", "1", "内容", "0");
        when(noticeMapper.selectNoticeById("1")).thenReturn(notice);

        SysNotice result = noticeService.selectNoticeById("1");

        assertEquals("0", result.getStatus());
    }

    @Test
    @Order(61)
    @DisplayName("状态测试-关闭状态")
    void testNoticeStatus_Closed() {
        SysNotice notice = createTestNotice("1", "关闭公告", "1", "内容", "1");
        when(noticeMapper.selectNoticeById("1")).thenReturn(notice);

        SysNotice result = noticeService.selectNoticeById("1");

        assertEquals("1", result.getStatus());
    }
}
