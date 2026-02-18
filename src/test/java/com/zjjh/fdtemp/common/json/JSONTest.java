package com.zjjh.fdtemp.common.json;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JSON 解析处理测试
 */
@DisplayName("JSON 解析处理测试")
class JSONTest {

    // 测试用的简单POJO
    public static class TestBean {
        private String name;
        private int age;

        public TestBean() {
        }

        public TestBean(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    @Nested
    @DisplayName("marshal String 方法测试")
    class MarshalStringTest {
        @Test
        @DisplayName("序列化对象为JSON字符串")
        void marshal_WhenObject_ShouldReturnJson() throws Exception {
            TestBean bean = new TestBean("test", 25);
            String result = JSON.marshal(bean);

            assertNotNull(result);
            assertTrue(result.contains("\"name\""));
            assertTrue(result.contains("\"test\""));
            assertTrue(result.contains("\"age\""));
            assertTrue(result.contains("25"));
        }

        @Test
        @DisplayName("序列化null对象")
        void marshal_WhenNull_ShouldReturnNullJson() throws Exception {
            String result = JSON.marshal(null);
            assertNotNull(result);
            assertTrue(result.contains("null"));
        }

        @Test
        @DisplayName("序列化空对象")
        void marshal_WhenEmptyObject_ShouldReturnJson() throws Exception {
            TestBean bean = new TestBean();
            String result = JSON.marshal(bean);

            assertNotNull(result);
            assertTrue(result.contains("\"name\""));
            assertTrue(result.contains("\"age\""));
        }
    }

    @Nested
    @DisplayName("marshalBytes 方法测试")
    class MarshalBytesTest {
        @Test
        @DisplayName("序列化对象为字节数组")
        void marshalBytes_WhenObject_ShouldReturnBytes() throws Exception {
            TestBean bean = new TestBean("test", 25);
            byte[] result = JSON.marshalBytes(bean);

            assertNotNull(result);
            assertTrue(result.length > 0);

            String jsonStr = new String(result);
            assertTrue(jsonStr.contains("\"name\""));
        }
    }

    @Nested
    @DisplayName("marshal OutputStream 方法测试")
    class MarshalOutputStreamTest {
        @Test
        @DisplayName("序列化对象到输出流")
        void marshal_WhenOutputStream_ShouldWrite() throws Exception {
            TestBean bean = new TestBean("test", 25);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            JSON.marshal(os, bean);

            String result = os.toString();
            assertNotNull(result);
            assertTrue(result.contains("\"name\""));
        }
    }

    @Nested
    @DisplayName("marshal File 方法测试")
    class MarshalFileTest {
        @TempDir
        Path tempDir;

        @Test
        @DisplayName("序列化对象到文件")
        void marshal_WhenFile_ShouldWrite() throws Exception {
            TestBean bean = new TestBean("test", 25);
            File file = tempDir.resolve("test.json").toFile();

            JSON.marshal(file, bean);

            assertTrue(file.exists());
            String content = Files.readString(file.toPath());
            assertTrue(content.contains("\"name\""));
        }
    }

    @Nested
    @DisplayName("unmarshal String 方法测试")
    class UnmarshalStringTest {
        @Test
        @DisplayName("从JSON字符串反序列化")
        void unmarshal_WhenJsonString_ShouldReturnObject() throws Exception {
            String json = "{\"name\":\"test\",\"age\":25}";
            TestBean result = JSON.unmarshal(json, TestBean.class);

            assertNotNull(result);
            assertEquals("test", result.getName());
            assertEquals(25, result.getAge());
        }

        @Test
        @DisplayName("从格式化JSON字符串反序列化")
        void unmarshal_WhenFormattedJsonString_ShouldReturnObject() throws Exception {
            String json = "{\n  \"name\" : \"test\",\n  \"age\" : 25\n}";
            TestBean result = JSON.unmarshal(json, TestBean.class);

            assertNotNull(result);
            assertEquals("test", result.getName());
            assertEquals(25, result.getAge());
        }
    }

    @Nested
    @DisplayName("unmarshal InputStream 方法测试")
    class UnmarshalInputStreamTest {
        @Test
        @DisplayName("从输入流反序列化")
        void unmarshal_WhenInputStream_ShouldReturnObject() throws Exception {
            String json = "{\"name\":\"test\",\"age\":25}";
            ByteArrayInputStream is = new ByteArrayInputStream(json.getBytes());

            TestBean result = JSON.unmarshal(is, TestBean.class);

            assertNotNull(result);
            assertEquals("test", result.getName());
            assertEquals(25, result.getAge());
        }
    }

    @Nested
    @DisplayName("unmarshal byte[] 方法测试")
    class UnmarshalBytesTest {
        @Test
        @DisplayName("从字节数组反序列化")
        void unmarshal_WhenBytes_ShouldReturnObject() throws Exception {
            String json = "{\"name\":\"test\",\"age\":25}";
            byte[] bytes = json.getBytes();

            TestBean result = JSON.unmarshal(bytes, TestBean.class);

            assertNotNull(result);
            assertEquals("test", result.getName());
            assertEquals(25, result.getAge());
        }

        @Test
        @DisplayName("null字节数组反序列化")
        void unmarshal_WhenNullBytes_ShouldHandleGracefully() {
            // null字节数组会被转换为空数组，可能导致解析异常
            assertThrows(Exception.class, () -> JSON.unmarshal((byte[]) null, TestBean.class));
        }

        @Test
        @DisplayName("空字节数组反序列化")
        void unmarshal_WhenEmptyBytes_ShouldThrowException() {
            byte[] bytes = new byte[0];
            assertThrows(Exception.class, () -> JSON.unmarshal(bytes, TestBean.class));
        }
    }

    @Nested
    @DisplayName("unmarshal File 方法测试")
    class UnmarshalFileTest {
        @TempDir
        Path tempDir;

        @Test
        @DisplayName("从文件反序列化")
        void unmarshal_WhenFile_ShouldReturnObject() throws Exception {
            String json = "{\"name\":\"test\",\"age\":25}";
            File file = tempDir.resolve("test.json").toFile();
            Files.writeString(file.toPath(), json);

            TestBean result = JSON.unmarshal(file, TestBean.class);

            assertNotNull(result);
            assertEquals("test", result.getName());
            assertEquals(25, result.getAge());
        }

        @Test
        @DisplayName("文件不存在时抛出异常")
        void unmarshal_WhenFileNotExists_ShouldThrowException() {
            File file = tempDir.resolve("notexist.json").toFile();
            assertThrows(Exception.class, () -> JSON.unmarshal(file, TestBean.class));
        }
    }

    @Nested
    @DisplayName("常量测试")
    class ConstantsTest {
        @Test
        @DisplayName("DEFAULT_FAIL常量")
        void defaultFail_ShouldBeCorrect() {
            assertEquals("\"Parse failed\"", JSON.DEFAULT_FAIL);
        }
    }

    @Nested
    @DisplayName("序列化反序列化循环测试")
    class RoundTripTest {
        @Test
        @DisplayName("序列化后反序列化应得到相同对象")
        void roundTrip_ShouldReturnSameObject() throws Exception {
            TestBean original = new TestBean("test", 25);
            String json = JSON.marshal(original);
            TestBean result = JSON.unmarshal(json, TestBean.class);

            assertEquals(original.getName(), result.getName());
            assertEquals(original.getAge(), result.getAge());
        }
    }
}
