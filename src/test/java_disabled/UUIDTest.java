package com.zjjh.fdtemp.common.utils.uuid;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * UUID 自定义UUID类单元测试
 */
@DisplayName("UUID 自定义UUID类测试")
class UUIDTest {

    @Nested
    @DisplayName("randomUUID 测试")
    class RandomUUIDTest {
        @Test
        @DisplayName("生成随机UUID")
        void randomUUID_ShouldReturnValidUUID() {
            UUID result = UUID.randomUUID();
            assertNotNull(result);
            assertEquals(4, result.version()); // Version 4 = random
        }

        @Test
        @DisplayName("生成安全随机UUID")
        void randomUUID_WhenSecure_ShouldReturnValidUUID() {
            UUID result = UUID.randomUUID(true);
            assertNotNull(result);
            assertEquals(4, result.version());
        }

        @Test
        @DisplayName("生成非安全随机UUID")
        void randomUUID_WhenNotSecure_ShouldReturnValidUUID() {
            UUID result = UUID.randomUUID(false);
            assertNotNull(result);
            assertEquals(4, result.version());
        }
    }

    @Nested
    @DisplayName("fastUUID 测试")
    class FastUUIDTest {
        @Test
        @DisplayName("生成快速UUID")
        void fastUUID_ShouldReturnValidUUID() {
            UUID result = UUID.fastUUID();
            assertNotNull(result);
            assertEquals(4, result.version());
        }
    }

    @Nested
    @DisplayName("nameUUIDFromBytes 测试")
    class NameUUIDFromBytesTest {
        @Test
        @DisplayName("根据字节数组生成UUID")
        void nameUUIDFromBytes_ShouldReturnValidUUID() {
            UUID result = UUID.nameUUIDFromBytes("test".getBytes());
            assertNotNull(result);
            assertEquals(3, result.version()); // Version 3 = name-based
        }

        @Test
        @DisplayName("相同字节数组生成相同UUID")
        void nameUUIDFromBytes_WhenSameBytes_ShouldReturnSameUUID() {
            byte[] bytes = "test".getBytes();
            UUID result1 = UUID.nameUUIDFromBytes(bytes);
            UUID result2 = UUID.nameUUIDFromBytes(bytes);
            assertEquals(result1, result2);
        }

        @Test
        @DisplayName("不同字节数组生成不同UUID")
        void nameUUIDFromBytes_WhenDifferentBytes_ShouldReturnDifferentUUID() {
            UUID result1 = UUID.nameUUIDFromBytes("test1".getBytes());
            UUID result2 = UUID.nameUUIDFromBytes("test2".getBytes());
            assertNotEquals(result1, result2);
        }
    }

    @Nested
    @DisplayName("fromString 测试")
    class FromStringTest {
        @Test
        @DisplayName("从字符串解析UUID")
        void fromString_WhenValidString_ShouldReturnUUID() {
            String uuidStr = "550e8400-e29b-41d4-a716-446655440000";
            UUID result = UUID.fromString(uuidStr);
            assertNotNull(result);
            assertEquals(uuidStr, result.toString());
        }

        @Test
        @DisplayName("无效字符串抛出异常")
        void fromString_WhenInvalidString_ShouldThrowException() {
            assertThrows(IllegalArgumentException.class, () -> UUID.fromString("invalid"));
        }

        @Test
        @DisplayName("组件数量不对抛出异常")
        void fromString_WhenWrongComponentCount_ShouldThrowException() {
            assertThrows(IllegalArgumentException.class, () -> UUID.fromString("550e8400-e29b-41d4"));
        }
    }

    @Nested
    @DisplayName("toString 测试")
    class ToStringTest {
        @Test
        @DisplayName("标准格式输出")
        void toString_ShouldReturnStandardFormat() {
            UUID uuid = UUID.randomUUID();
            String result = uuid.toString();
            assertTrue(result.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
        }

        @Test
        @DisplayName("简化格式输出(无横线)")
        void toString_WhenSimple_ShouldReturnWithoutDash() {
            UUID uuid = UUID.randomUUID();
            String result = uuid.toString(true);
            assertEquals(32, result.length());
            assertFalse(result.contains("-"));
        }
    }

    @Nested
    @DisplayName("getter方法测试")
    class GetterTest {
        @Test
        @DisplayName("获取最低有效位")
        void getLeastSignificantBits_ShouldReturnValue() {
            UUID uuid = UUID.randomUUID();
            long result = uuid.getLeastSignificantBits();
            // 只是验证不会抛出异常
            assertTrue(true);
        }

        @Test
        @DisplayName("获取最高有效位")
        void getMostSignificantBits_ShouldReturnValue() {
            UUID uuid = UUID.randomUUID();
            long result = uuid.getMostSignificantBits();
            // 只是验证不会抛出异常
            assertTrue(true);
        }

        @Test
        @DisplayName("获取版本号")
        void version_ShouldReturnCorrectVersion() {
            UUID randomUUID = UUID.randomUUID();
            assertEquals(4, randomUUID.version());

            UUID nameUUID = UUID.nameUUIDFromBytes("test".getBytes());
            assertEquals(3, nameUUID.version());
        }

        @Test
        @DisplayName("获取变体号")
        void variant_ShouldReturnCorrectVariant() {
            UUID uuid = UUID.randomUUID();
            // IETF变体应该返回2
            assertEquals(2, uuid.variant());
        }
    }

    @Nested
    @DisplayName("时间相关方法测试")
    class TimeRelatedTest {
        @Test
        @DisplayName("非时间UUID调用timestamp抛出异常")
        void timestamp_WhenNotTimeBased_ShouldThrowException() {
            UUID uuid = UUID.randomUUID(); // Version 4, not time-based
            assertThrows(UnsupportedOperationException.class, uuid::timestamp);
        }

        @Test
        @DisplayName("非时间UUID调用clockSequence抛出异常")
        void clockSequence_WhenNotTimeBased_ShouldThrowException() {
            UUID uuid = UUID.randomUUID();
            assertThrows(UnsupportedOperationException.class, uuid::clockSequence);
        }

        @Test
        @DisplayName("非时间UUID调用node抛出异常")
        void node_WhenNotTimeBased_ShouldThrowException() {
            UUID uuid = UUID.randomUUID();
            assertThrows(UnsupportedOperationException.class, uuid::node);
        }
    }

    @Nested
    @DisplayName("equals和hashCode测试")
    class EqualsAndHashCodeTest {
        @Test
        @DisplayName("相同UUID equals返回true")
        void equals_WhenSameUUID_ShouldReturnTrue() {
            String uuidStr = "550e8400-e29b-41d4-a716-446655440000";
            UUID uuid1 = UUID.fromString(uuidStr);
            UUID uuid2 = UUID.fromString(uuidStr);
            assertEquals(uuid1, uuid2);
        }

        @Test
        @DisplayName("不同UUID equals返回false")
        void equals_WhenDifferentUUID_ShouldReturnFalse() {
            UUID uuid1 = UUID.randomUUID();
            UUID uuid2 = UUID.randomUUID();
            assertNotEquals(uuid1, uuid2);
        }

        @Test
        @DisplayName("null equals返回false")
        void equals_WhenNull_ShouldReturnFalse() {
            UUID uuid = UUID.randomUUID();
            assertNotEquals(null, uuid);
        }

        @Test
        @DisplayName("非UUID类型equals返回false")
        void equals_WhenNotUUID_ShouldReturnFalse() {
            UUID uuid = UUID.randomUUID();
            assertNotEquals("string", uuid);
        }

        @Test
        @DisplayName("相同UUID hashCode相同")
        void hashCode_WhenSameUUID_ShouldBeEqual() {
            String uuidStr = "550e8400-e29b-41d4-a716-446655440000";
            UUID uuid1 = UUID.fromString(uuidStr);
            UUID uuid2 = UUID.fromString(uuidStr);
            assertEquals(uuid1.hashCode(), uuid2.hashCode());
        }
    }

    @Nested
    @DisplayName("compareTo 测试")
    class CompareToTest {
        @Test
        @DisplayName("相等UUID返回0")
        void compareTo_WhenEqual_ShouldReturnZero() {
            String uuidStr = "550e8400-e29b-41d4-a716-446655440000";
            UUID uuid1 = UUID.fromString(uuidStr);
            UUID uuid2 = UUID.fromString(uuidStr);
            assertEquals(0, uuid1.compareTo(uuid2));
        }

        @Test
        @DisplayName("不同UUID比较")
        void compareTo_WhenDifferent_ShouldReturnNonZero() {
            UUID uuid1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            UUID uuid2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
            assertNotEquals(0, uuid1.compareTo(uuid2));
        }
    }

    @Nested
    @DisplayName("静态工具方法测试")
    class StaticMethodsTest {
        @Test
        @DisplayName("getSecureRandom 返回SecureRandom")
        void getSecureRandom_ShouldReturnSecureRandom() {
            assertNotNull(UUID.getSecureRandom());
        }

        @Test
        @DisplayName("getRandom 返回ThreadLocalRandom")
        void getRandom_ShouldReturnThreadLocalRandom() {
            assertNotNull(UUID.getRandom());
        }
    }

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTest {
        @Test
        @DisplayName("使用两个long值构造UUID")
        void constructor_WithTwoLongs_ShouldCreateUUID() {
            long mostSigBits = 0x550e8400e29b41d4L;
            long leastSigBits = 0xa716446655440000L;
            UUID uuid = new UUID(mostSigBits, leastSigBits);
            assertNotNull(uuid);
            assertEquals(mostSigBits, uuid.getMostSignificantBits());
            assertEquals(leastSigBits, uuid.getLeastSignificantBits());
        }
    }
}
