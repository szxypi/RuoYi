package com.zjjh.fdtemp.common.exception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.zjjh.fdtemp.common.exception.base.BaseException;
import com.zjjh.fdtemp.common.exception.file.FileException;
import com.zjjh.fdtemp.common.exception.file.FileNameLengthLimitExceededException;
import com.zjjh.fdtemp.common.exception.file.FileSizeLimitExceededException;
import com.zjjh.fdtemp.common.exception.file.FileUploadException;
import com.zjjh.fdtemp.common.exception.file.InvalidExtensionException;
import com.zjjh.fdtemp.common.exception.file.InvalidExtensionException.InvalidFlashExtensionException;
import com.zjjh.fdtemp.common.exception.file.InvalidExtensionException.InvalidImageExtensionException;
import com.zjjh.fdtemp.common.exception.file.InvalidExtensionException.InvalidMediaExtensionException;
import com.zjjh.fdtemp.common.exception.file.InvalidExtensionException.InvalidVideoExtensionException;
import com.zjjh.fdtemp.common.exception.job.TaskException;
import com.zjjh.fdtemp.common.exception.user.BlackListException;
import com.zjjh.fdtemp.common.exception.user.CaptchaException;
import com.zjjh.fdtemp.common.exception.user.RoleBlockedException;
import com.zjjh.fdtemp.common.exception.user.UserBlockedException;
import com.zjjh.fdtemp.common.exception.user.UserDeleteException;
import com.zjjh.fdtemp.common.exception.user.UserException;
import com.zjjh.fdtemp.common.exception.user.UserNotExistsException;
import com.zjjh.fdtemp.common.exception.user.UserPasswordNotMatchException;
import com.zjjh.fdtemp.common.exception.user.UserPasswordRetryLimitCountException;
import com.zjjh.fdtemp.common.exception.user.UserPasswordRetryLimitExceedException;
import com.zjjh.fdtemp.common.utils.MessageUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 所有异常类的单元测试
 */
@ExtendWith(MockitoExtension.class)
class AllExceptionsTest
{
    // ==================== BaseException ====================

    @Test
    void testBaseException_fullConstructor()
    {
        Object[] args = new Object[] { "arg1", "arg2" };
        BaseException ex = new BaseException("testModule", "test.code", args, "default msg");

        assertEquals("testModule", ex.getModule());
        assertEquals("test.code", ex.getCode());
        assertArrayEquals(args, ex.getArgs());
        assertEquals("default msg", ex.getDefaultMessage());
    }

    @Test
    void testBaseException_moduleCodeArgs()
    {
        Object[] args = new Object[] { "a" };
        BaseException ex = new BaseException("mod", "code1", args);

        assertEquals("mod", ex.getModule());
        assertEquals("code1", ex.getCode());
        assertArrayEquals(args, ex.getArgs());
        assertNull(ex.getDefaultMessage());
    }

    @Test
    void testBaseException_moduleDefaultMessage()
    {
        BaseException ex = new BaseException("mod", "some default");

        assertEquals("mod", ex.getModule());
        assertNull(ex.getCode());
        assertNull(ex.getArgs());
        assertEquals("some default", ex.getDefaultMessage());
    }

    @Test
    void testBaseException_codeArgs()
    {
        Object[] args = new Object[] { 1 };
        BaseException ex = new BaseException("myCode", args);

        assertNull(ex.getModule());
        assertEquals("myCode", ex.getCode());
        assertArrayEquals(args, ex.getArgs());
        assertNull(ex.getDefaultMessage());
    }

    @Test
    void testBaseException_defaultMessageOnly()
    {
        BaseException ex = new BaseException("just a message");

        assertNull(ex.getModule());
        assertNull(ex.getCode());
        assertNull(ex.getArgs());
        assertEquals("just a message", ex.getDefaultMessage());
    }

    @Test
    void testBaseException_getMessage_withCode_messageResolved()
    {
        try (MockedStatic<MessageUtils> mocked = Mockito.mockStatic(MessageUtils.class))
        {
            mocked.when(() -> MessageUtils.message("test.code", (Object[]) null))
                    .thenReturn("resolved message");

            BaseException ex = new BaseException(null, "test.code", null, "fallback");
            assertEquals("resolved message", ex.getMessage());
        }
    }

    @Test
    void testBaseException_getMessage_withCode_messageNull_fallsBackToDefault()
    {
        try (MockedStatic<MessageUtils> mocked = Mockito.mockStatic(MessageUtils.class))
        {
            mocked.when(() -> MessageUtils.message("test.code", (Object[]) null))
                    .thenReturn(null);

            BaseException ex = new BaseException(null, "test.code", null, "fallback msg");
            assertEquals("fallback msg", ex.getMessage());
        }
    }

    @Test
    void testBaseException_getMessage_noCode_returnsDefaultMessage()
    {
        BaseException ex = new BaseException(null, null, null, "default only");
        assertEquals("default only", ex.getMessage());
    }

    @Test
    void testBaseException_getMessage_noCode_emptyCode_returnsDefaultMessage()
    {
        BaseException ex = new BaseException(null, "", null, "default only");
        assertEquals("default only", ex.getMessage());
    }

    @Test
    void testBaseException_getMessage_noCodeNoDefault_returnsNull()
    {
        BaseException ex = new BaseException(null, null, null, null);
        assertNull(ex.getMessage());
    }

    @Test
    void testBaseException_isRuntimeException()
    {
        BaseException ex = new BaseException("msg");
        assertInstanceOf(RuntimeException.class, ex);
    }

    // ==================== ServiceException ====================

    @Test
    void testServiceException_noArgConstructor()
    {
        ServiceException ex = new ServiceException();
        assertNull(ex.getMessage());
        assertNull(ex.getDetailMessage());
    }

    @Test
    void testServiceException_messageConstructor()
    {
        ServiceException ex = new ServiceException("error occurred");
        assertEquals("error occurred", ex.getMessage());
    }

    @Test
    void testServiceException_setDetailMessage()
    {
        ServiceException ex = new ServiceException("msg");
        ServiceException returned = ex.setDetailMessage("detail info");

        assertSame(ex, returned);
        assertEquals("detail info", ex.getDetailMessage());
    }

    @Test
    void testServiceException_setMessage()
    {
        ServiceException ex = new ServiceException();
        ServiceException returned = ex.setMessage("new message");

        assertSame(ex, returned);
        assertEquals("new message", ex.getMessage());
    }

    @Test
    void testServiceException_isRuntimeException()
    {
        assertInstanceOf(RuntimeException.class, new ServiceException());
    }

    @Test
    void testServiceException_isFinal()
    {
        assertTrue(java.lang.reflect.Modifier.isFinal(ServiceException.class.getModifiers()));
    }

    // ==================== GlobalException ====================

    @Test
    void testGlobalException_noArgConstructor()
    {
        GlobalException ex = new GlobalException();
        assertNull(ex.getMessage());
        assertNull(ex.getDetailMessage());
    }

    @Test
    void testGlobalException_messageConstructor()
    {
        GlobalException ex = new GlobalException("global error");
        assertEquals("global error", ex.getMessage());
    }

    @Test
    void testGlobalException_setDetailMessage()
    {
        GlobalException ex = new GlobalException("msg");
        GlobalException returned = ex.setDetailMessage("detail");

        assertSame(ex, returned);
        assertEquals("detail", ex.getDetailMessage());
    }

    @Test
    void testGlobalException_setMessage()
    {
        GlobalException ex = new GlobalException();
        GlobalException returned = ex.setMessage("updated");

        assertSame(ex, returned);
        assertEquals("updated", ex.getMessage());
    }

    @Test
    void testGlobalException_isRuntimeException()
    {
        assertInstanceOf(RuntimeException.class, new GlobalException());
    }

    // ==================== UtilException ====================

    @Test
    void testUtilException_throwableConstructor()
    {
        Throwable cause = new RuntimeException("root cause");
        UtilException ex = new UtilException(cause);

        assertEquals("root cause", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void testUtilException_messageConstructor()
    {
        UtilException ex = new UtilException("util error");
        assertEquals("util error", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void testUtilException_messageAndThrowableConstructor()
    {
        Throwable cause = new IllegalArgumentException("bad arg");
        UtilException ex = new UtilException("wrapper msg", cause);

        assertEquals("wrapper msg", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void testUtilException_isRuntimeException()
    {
        assertInstanceOf(RuntimeException.class, new UtilException("test"));
    }

    // ==================== FileException ====================

    @Test
    void testFileException_constructor()
    {
        Object[] args = new Object[] { "file.txt" };
        FileException ex = new FileException("file.code", args);

        assertEquals("file", ex.getModule());
        assertEquals("file.code", ex.getCode());
        assertArrayEquals(args, ex.getArgs());
        assertNull(ex.getDefaultMessage());
    }

    @Test
    void testFileException_extendsBaseException()
    {
        assertInstanceOf(BaseException.class, new FileException("code", null));
    }

    // ==================== FileSizeLimitExceededException ====================

    @Test
    void testFileSizeLimitExceededException_constructor()
    {
        FileSizeLimitExceededException ex = new FileSizeLimitExceededException(50L);

        assertEquals("file", ex.getModule());
        assertEquals("upload.exceed.maxSize", ex.getCode());
        assertArrayEquals(new Object[] { 50L }, ex.getArgs());
    }

    @Test
    void testFileSizeLimitExceededException_extendsFileException()
    {
        assertInstanceOf(FileException.class, new FileSizeLimitExceededException(100L));
    }

    // ==================== FileNameLengthLimitExceededException ====================

    @Test
    void testFileNameLengthLimitExceededException_constructor()
    {
        FileNameLengthLimitExceededException ex = new FileNameLengthLimitExceededException(255);

        assertEquals("file", ex.getModule());
        assertEquals("upload.filename.exceed.length", ex.getCode());
        assertArrayEquals(new Object[] { 255 }, ex.getArgs());
    }

    @Test
    void testFileNameLengthLimitExceededException_extendsFileException()
    {
        assertInstanceOf(FileException.class, new FileNameLengthLimitExceededException(100));
    }

    // ==================== FileUploadException ====================

    @Test
    void testFileUploadException_noArgConstructor()
    {
        FileUploadException ex = new FileUploadException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void testFileUploadException_messageConstructor()
    {
        FileUploadException ex = new FileUploadException("upload failed");
        assertEquals("upload failed", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void testFileUploadException_messageAndCauseConstructor()
    {
        Throwable cause = new RuntimeException("io error");
        FileUploadException ex = new FileUploadException("upload failed", cause);

        assertEquals("upload failed", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void testFileUploadException_isException()
    {
        assertInstanceOf(Exception.class, new FileUploadException());
    }

    @Test
    void testFileUploadException_printStackTrace_printStream_withCause()
    {
        Throwable cause = new RuntimeException("io error");
        FileUploadException ex = new FileUploadException("upload failed", cause);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ex.printStackTrace(ps);
        ps.flush();

        String output = baos.toString();
        assertTrue(output.contains("upload failed"));
        assertTrue(output.contains("Caused by:"));
        assertTrue(output.contains("io error"));
    }

    @Test
    void testFileUploadException_printStackTrace_printStream_withoutCause()
    {
        FileUploadException ex = new FileUploadException("no cause");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ex.printStackTrace(ps);
        ps.flush();

        String output = baos.toString();
        assertTrue(output.contains("no cause"));
        assertFalse(output.contains("Caused by:"));
    }

    @Test
    void testFileUploadException_printStackTrace_printWriter_withCause()
    {
        Throwable cause = new RuntimeException("writer cause");
        FileUploadException ex = new FileUploadException("writer fail", cause);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        pw.flush();

        String output = sw.toString();
        assertTrue(output.contains("writer fail"));
        assertTrue(output.contains("Caused by:"));
        assertTrue(output.contains("writer cause"));
    }

    @Test
    void testFileUploadException_printStackTrace_printWriter_withoutCause()
    {
        FileUploadException ex = new FileUploadException("writer no cause");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        pw.flush();

        String output = sw.toString();
        assertTrue(output.contains("writer no cause"));
        assertFalse(output.contains("Caused by:"));
    }

    // ==================== InvalidExtensionException ====================

    @Test
    void testInvalidExtensionException_constructor()
    {
        String[] allowed = new String[] { "jpg", "png" };
        InvalidExtensionException ex = new InvalidExtensionException(allowed, "exe", "malware.exe");

        assertArrayEquals(allowed, ex.getAllowedExtension());
        assertEquals("exe", ex.getExtension());
        assertEquals("malware.exe", ex.getFilename());
        assertTrue(ex.getMessage().contains("malware.exe"));
        assertTrue(ex.getMessage().contains("exe"));
        assertTrue(ex.getMessage().contains("[jpg, png]"));
    }

    @Test
    void testInvalidExtensionException_extendsFileUploadException()
    {
        assertInstanceOf(FileUploadException.class,
                new InvalidExtensionException(new String[] { "txt" }, "bin", "test.bin"));
    }

    @Test
    void testInvalidImageExtensionException()
    {
        String[] allowed = new String[] { "jpg", "png" };
        InvalidImageExtensionException ex = new InvalidImageExtensionException(allowed, "bmp", "photo.bmp");

        assertInstanceOf(InvalidExtensionException.class, ex);
        assertArrayEquals(allowed, ex.getAllowedExtension());
        assertEquals("bmp", ex.getExtension());
        assertEquals("photo.bmp", ex.getFilename());
    }

    @Test
    void testInvalidFlashExtensionException()
    {
        String[] allowed = new String[] { "swf" };
        InvalidFlashExtensionException ex = new InvalidFlashExtensionException(allowed, "exe", "flash.exe");

        assertInstanceOf(InvalidExtensionException.class, ex);
        assertArrayEquals(allowed, ex.getAllowedExtension());
        assertEquals("exe", ex.getExtension());
        assertEquals("flash.exe", ex.getFilename());
    }

    @Test
    void testInvalidMediaExtensionException()
    {
        String[] allowed = new String[] { "mp3", "wav" };
        InvalidMediaExtensionException ex = new InvalidMediaExtensionException(allowed, "flac", "song.flac");

        assertInstanceOf(InvalidExtensionException.class, ex);
        assertArrayEquals(allowed, ex.getAllowedExtension());
        assertEquals("flac", ex.getExtension());
        assertEquals("song.flac", ex.getFilename());
    }

    @Test
    void testInvalidVideoExtensionException()
    {
        String[] allowed = new String[] { "mp4", "avi" };
        InvalidVideoExtensionException ex = new InvalidVideoExtensionException(allowed, "mkv", "video.mkv");

        assertInstanceOf(InvalidExtensionException.class, ex);
        assertArrayEquals(allowed, ex.getAllowedExtension());
        assertEquals("mkv", ex.getExtension());
        assertEquals("video.mkv", ex.getFilename());
    }

    // ==================== TaskException ====================

    @Test
    void testTaskException_twoArgConstructor()
    {
        TaskException ex = new TaskException("task failed", TaskException.Code.TASK_EXISTS);

        assertEquals("task failed", ex.getMessage());
        assertEquals(TaskException.Code.TASK_EXISTS, ex.getCode());
        assertNull(ex.getCause());
    }

    @Test
    void testTaskException_threeArgConstructor()
    {
        Exception nested = new RuntimeException("nested");
        TaskException ex = new TaskException("task error", TaskException.Code.CONFIG_ERROR, nested);

        assertEquals("task error", ex.getMessage());
        assertEquals(TaskException.Code.CONFIG_ERROR, ex.getCode());
        assertSame(nested, ex.getCause());
    }

    @Test
    void testTaskException_isException()
    {
        assertInstanceOf(Exception.class,
                new TaskException("msg", TaskException.Code.UNKNOWN));
    }

    @Test
    void testTaskException_allCodeEnumValues()
    {
        TaskException.Code[] values = TaskException.Code.values();
        assertEquals(6, values.length);

        assertEquals(TaskException.Code.TASK_EXISTS, TaskException.Code.valueOf("TASK_EXISTS"));
        assertEquals(TaskException.Code.NO_TASK_EXISTS, TaskException.Code.valueOf("NO_TASK_EXISTS"));
        assertEquals(TaskException.Code.TASK_ALREADY_STARTED, TaskException.Code.valueOf("TASK_ALREADY_STARTED"));
        assertEquals(TaskException.Code.UNKNOWN, TaskException.Code.valueOf("UNKNOWN"));
        assertEquals(TaskException.Code.CONFIG_ERROR, TaskException.Code.valueOf("CONFIG_ERROR"));
        assertEquals(TaskException.Code.TASK_NODE_NOT_AVAILABLE, TaskException.Code.valueOf("TASK_NODE_NOT_AVAILABLE"));
    }

    // ==================== UserException ====================

    @Test
    void testUserException_constructor()
    {
        Object[] args = new Object[] { "admin" };
        UserException ex = new UserException("user.code", args);

        assertEquals("user", ex.getModule());
        assertEquals("user.code", ex.getCode());
        assertArrayEquals(args, ex.getArgs());
        assertNull(ex.getDefaultMessage());
    }

    @Test
    void testUserException_extendsBaseException()
    {
        assertInstanceOf(BaseException.class, new UserException("code", null));
    }

    // ==================== BlackListException ====================

    @Test
    void testBlackListException_constructor()
    {
        BlackListException ex = new BlackListException();

        assertEquals("user", ex.getModule());
        assertEquals("login.blocked", ex.getCode());
        assertNull(ex.getArgs());
    }

    @Test
    void testBlackListException_extendsUserException()
    {
        assertInstanceOf(UserException.class, new BlackListException());
    }

    // ==================== CaptchaException ====================

    @Test
    void testCaptchaException_constructor()
    {
        CaptchaException ex = new CaptchaException();

        assertEquals("user", ex.getModule());
        assertEquals("user.jcaptcha.error", ex.getCode());
        assertNull(ex.getArgs());
    }

    @Test
    void testCaptchaException_extendsUserException()
    {
        assertInstanceOf(UserException.class, new CaptchaException());
    }

    // ==================== UserBlockedException ====================

    @Test
    void testUserBlockedException_constructor()
    {
        UserBlockedException ex = new UserBlockedException();

        assertEquals("user", ex.getModule());
        assertEquals("user.blocked", ex.getCode());
        assertNull(ex.getArgs());
    }

    @Test
    void testUserBlockedException_extendsUserException()
    {
        assertInstanceOf(UserException.class, new UserBlockedException());
    }

    // ==================== UserDeleteException ====================

    @Test
    void testUserDeleteException_constructor()
    {
        UserDeleteException ex = new UserDeleteException();

        assertEquals("user", ex.getModule());
        assertEquals("user.password.delete", ex.getCode());
        assertNull(ex.getArgs());
    }

    @Test
    void testUserDeleteException_extendsUserException()
    {
        assertInstanceOf(UserException.class, new UserDeleteException());
    }

    // ==================== UserNotExistsException ====================

    @Test
    void testUserNotExistsException_constructor()
    {
        UserNotExistsException ex = new UserNotExistsException();

        assertEquals("user", ex.getModule());
        assertEquals("user.not.exists", ex.getCode());
        assertNull(ex.getArgs());
    }

    @Test
    void testUserNotExistsException_extendsUserException()
    {
        assertInstanceOf(UserException.class, new UserNotExistsException());
    }

    // ==================== UserPasswordNotMatchException ====================

    @Test
    void testUserPasswordNotMatchException_constructor()
    {
        UserPasswordNotMatchException ex = new UserPasswordNotMatchException();

        assertEquals("user", ex.getModule());
        assertEquals("user.password.not.match", ex.getCode());
        assertNull(ex.getArgs());
    }

    @Test
    void testUserPasswordNotMatchException_extendsUserException()
    {
        assertInstanceOf(UserException.class, new UserPasswordNotMatchException());
    }

    // ==================== UserPasswordRetryLimitExceedException ====================

    @Test
    void testUserPasswordRetryLimitExceedException_constructor()
    {
        UserPasswordRetryLimitExceedException ex = new UserPasswordRetryLimitExceedException(5);

        assertEquals("user", ex.getModule());
        assertEquals("user.password.retry.limit.exceed", ex.getCode());
        assertArrayEquals(new Object[] { 5 }, ex.getArgs());
    }

    @Test
    void testUserPasswordRetryLimitExceedException_extendsUserException()
    {
        assertInstanceOf(UserException.class, new UserPasswordRetryLimitExceedException(3));
    }

    // ==================== UserPasswordRetryLimitCountException ====================

    @Test
    void testUserPasswordRetryLimitCountException_constructor()
    {
        UserPasswordRetryLimitCountException ex = new UserPasswordRetryLimitCountException(3);

        assertEquals("user", ex.getModule());
        assertEquals("user.password.retry.limit.count", ex.getCode());
        assertArrayEquals(new Object[] { 3 }, ex.getArgs());
    }

    @Test
    void testUserPasswordRetryLimitCountException_extendsUserException()
    {
        assertInstanceOf(UserException.class, new UserPasswordRetryLimitCountException(1));
    }

    // ==================== RoleBlockedException ====================

    @Test
    void testRoleBlockedException_constructor()
    {
        RoleBlockedException ex = new RoleBlockedException();

        assertEquals("user", ex.getModule());
        assertEquals("role.blocked", ex.getCode());
        assertNull(ex.getArgs());
    }

    @Test
    void testRoleBlockedException_extendsUserException()
    {
        assertInstanceOf(UserException.class, new RoleBlockedException());
    }
}
