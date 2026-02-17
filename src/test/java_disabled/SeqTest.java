package com.zjjh.fdtemp.common.utils.uuid;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Seq 序列生成类单元测试
 */
@DisplayName("Seq 序列生成类测试")
class SeqTest {

    @Nested
    @DisplayName("getId 默认序列测试")
    class GetIdDefaultTest {
        @Test
        @DisplayName("获取默认序列号")
        void getId_ShouldReturnValidId() {
            String result = Seq.getId();
            assertNotNull(result);
            assertEquals(16, result.length());
        }

        @Test
        @DisplayName("序列号包含日期时间部分")
        void getId_ShouldContainDateTime() {
            String result = Seq.getId();
            // 前14位应该是日期时间 yyyyMMddHHmmss
            String dateTime = result.substring(0, 14);
            assertTrue(dateTime.matches("\\d{14}"));
        }

        @Test
        @DisplayName("序列号包含机器标识")
        void getId_ShouldContainMachineCode() {
            String result = Seq.getId();
            // 第15位应该是机器标识
            char machineCode = result.charAt(14);
            assertEquals('A', machineCode);
        }

        @Test
        @DisplayName("序列号包含递增序列")
        void getId_ShouldContainSequence() {
            String result = Seq.getId();
            // 最后3位应该是序列号
            String sequence = result.substring(15);
            assertTrue(sequence.matches("\\d{3}"));
        }
    }

    @Nested
    @DisplayName("getId 带类型参数测试")
    class GetIdWithTypeTest {
        @Test
        @DisplayName("获取通用类型序列号")
        void getId_WhenCommonType_ShouldReturnValidId() {
            String result = Seq.getId(Seq.commSeqType);
            assertNotNull(result);
            assertEquals(16, result.length());
        }

        @Test
        @DisplayName("获取上传类型序列号")
        void getId_WhenUploadType_ShouldReturnValidId() {
            String result = Seq.getId(Seq.uploadSeqType);
            assertNotNull(result);
            assertEquals(16, result.length());
        }
    }

    @Nested
    @DisplayName("getId 带AtomicInteger和长度参数测试")
    class GetIdWithAtomicIntegerTest {
        @Test
        @DisplayName("自定义序列长度")
        void getId_WhenCustomLength_ShouldReturnCorrectLength() {
            AtomicInteger atomicInt = new AtomicInteger(1);
            String result = Seq.getId(atomicInt, 5);
            assertNotNull(result);
            // 14位日期 + 1位机器标识 + 5位序列 = 20位
            assertEquals(20, result.length());
        }

        @Test
        @DisplayName("序列号递增")
        void getId_WhenCalledMultipleTimes_ShouldIncrement() {
            AtomicInteger atomicInt = new AtomicInteger(1);
            String result1 = Seq.getId(atomicInt, 3);
            String result2 = Seq.getId(atomicInt, 3);

            // 提取序列号部分
            String seq1 = result1.substring(15);
            String seq2 = result2.substring(15);

            int seqNum1 = Integer.parseInt(seq1);
            int seqNum2 = Integer.parseInt(seq2);

            assertEquals(1, seqNum2 - seqNum1);
        }
    }

    @Nested
    @DisplayName("序列号格式测试")
    class IdFormatTest {
        @Test
        @DisplayName("序列号格式正确")
        void getId_ShouldMatchFormat() {
            String result = Seq.getId();
            // 格式: yyMMddHHmmss + 机器标识(1位) + 序列号(3位)
            assertTrue(result.matches("\\d{14}[A-Z]\\d{3}"));
        }
    }

    @Nested
    @DisplayName("常量测试")
    class ConstantsTest {
        @Test
        @DisplayName("通用序列类型常量")
        void commSeqType_ShouldBeCorrect() {
            assertEquals("COMMON", Seq.commSeqType);
        }

        @Test
        @DisplayName("上传序列类型常量")
        void uploadSeqType_ShouldBeCorrect() {
            assertEquals("UPLOAD", Seq.uploadSeqType);
        }
    }
}
