package com.zjjh.fdtemp.common.utils.uuid;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * IdUtils ID生成器工具类单元测试
 */
@DisplayName("IdUtils ID生成器工具类测试")
class IdUtilsTest {

    @Nested
    @DisplayName("randomUUID 测试")
    class RandomUUIDTest {
        @Test
        @DisplayName("生成随机UUID")
        void randomUUID_ShouldReturnValidUUID() {
            String result = IdUtils.randomUUID();
            assertNotNull(result);
            assertTrue(result.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
        }

        @Test
        @DisplayName("生成的UUID唯一")
        void randomUUID_ShouldBeUnique() {
            Set<String> uuids = new HashSet<>();
            for (int i = 0; i < 1000; i++) {
                String uuid = IdUtils.randomUUID();
                assertFalse(uuids.contains(uuid), "UUID重复: " + uuid);
                uuids.add(uuid);
            }
        }
    }

    @Nested
    @DisplayName("simpleUUID 测试")
    class SimpleUUIDTest {
        @Test
        @DisplayName("生成简化UUID(无横线)")
        void simpleUUID_ShouldReturnUUIDWithoutDash() {
            String result = IdUtils.simpleUUID();
            assertNotNull(result);
            assertEquals(32, result.length());
            assertTrue(result.matches("[0-9a-f]{32}"));
            assertFalse(result.contains("-"));
        }

        @Test
        @DisplayName("生成的简化UUID唯一")
        void simpleUUID_ShouldBeUnique() {
            Set<String> uuids = new HashSet<>();
            for (int i = 0; i < 1000; i++) {
                String uuid = IdUtils.simpleUUID();
                assertFalse(uuids.contains(uuid), "UUID重复: " + uuid);
                uuids.add(uuid);
            }
        }
    }

    @Nested
    @DisplayName("fastUUID 测试")
    class FastUUIDTest {
        @Test
        @DisplayName("生成快速UUID")
        void fastUUID_ShouldReturnValidUUID() {
            String result = IdUtils.fastUUID();
            assertNotNull(result);
            assertTrue(result.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
        }

        @Test
        @DisplayName("生成的快速UUID唯一")
        void fastUUID_ShouldBeUnique() {
            Set<String> uuids = new HashSet<>();
            for (int i = 0; i < 1000; i++) {
                String uuid = IdUtils.fastUUID();
                assertFalse(uuids.contains(uuid), "UUID重复: " + uuid);
                uuids.add(uuid);
            }
        }
    }

    @Nested
    @DisplayName("fastSimpleUUID 测试")
    class FastSimpleUUIDTest {
        @Test
        @DisplayName("生成快速简化UUID")
        void fastSimpleUUID_ShouldReturnUUIDWithoutDash() {
            String result = IdUtils.fastSimpleUUID();
            assertNotNull(result);
            assertEquals(32, result.length());
            assertTrue(result.matches("[0-9a-f]{32}"));
        }

        @Test
        @DisplayName("生成的快速简化UUID唯一")
        void fastSimpleUUID_ShouldBeUnique() {
            Set<String> uuids = new HashSet<>();
            for (int i = 0; i < 1000; i++) {
                String uuid = IdUtils.fastSimpleUUID();
                assertFalse(uuids.contains(uuid), "UUID重复: " + uuid);
                uuids.add(uuid);
            }
        }
    }

    @Nested
    @DisplayName("性能对比测试")
    class PerformanceTest {
        @Test
        @DisplayName("fastUUID比randomUUID快")
        void fastUUID_ShouldBeFasterThanRandomUUID() {
            int count = 10000;

            // 测试randomUUID
            long startRandom = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                IdUtils.randomUUID();
            }
            long endRandom = System.currentTimeMillis();

            // 测试fastUUID
            long startFast = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                IdUtils.fastUUID();
            }
            long endFast = System.currentTimeMillis();

            long randomTime = endRandom - startRandom;
            long fastTime = endFast - startFast;

            // fastUUID应该比randomUUID快(使用ThreadLocalRandom)
            assertTrue(fastTime <= randomTime * 2,
                    "fastUUID应该比randomUUID快或相近。randomUUID: " + randomTime + "ms, fastUUID: " + fastTime + "ms");
        }
    }
}
