package com.zjjh.fdtemp.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ScheduleConstants 常量类单元测试
 */
@DisplayName("ScheduleConstants 常量类测试")
class ScheduleConstantsTest {

    @Test
    @DisplayName("测试 TASK_CLASS_NAME 常量")
    void testTaskClassName() {
        assertEquals("TASK_CLASS_NAME", ScheduleConstants.TASK_CLASS_NAME);
    }

    @Test
    @DisplayName("测试 TASK_PROPERTIES 常量")
    void testTaskProperties() {
        assertEquals("TASK_PROPERTIES", ScheduleConstants.TASK_PROPERTIES);
    }

    @Test
    @DisplayName("测试 MISFIRE_DEFAULT 常量")
    void testMisfireDefault() {
        assertEquals("0", ScheduleConstants.MISFIRE_DEFAULT);
    }

    @Test
    @DisplayName("测试 MISFIRE_IGNORE_MISFIRES 常量")
    void testMisfireIgnoreMisfires() {
        assertEquals("1", ScheduleConstants.MISFIRE_IGNORE_MISFIRES);
    }

    @Test
    @DisplayName("测试 MISFIRE_FIRE_AND_PROCEED 常量")
    void testMisfireFireAndProceed() {
        assertEquals("2", ScheduleConstants.MISFIRE_FIRE_AND_PROCEED);
    }

    @Test
    @DisplayName("测试 MISFIRE_DO_NOTHING 常量")
    void testMisfireDoNothing() {
        assertEquals("3", ScheduleConstants.MISFIRE_DO_NOTHING);
    }

    @Test
    @DisplayName("测试 Status 枚举 - NORMAL")
    void testStatusEnumNormal() {
        ScheduleConstants.Status normal = ScheduleConstants.Status.NORMAL;
        assertEquals("0", normal.getValue());
        assertEquals("0", normal.value);
    }

    @Test
    @DisplayName("测试 Status 枚举 - PAUSE")
    void testStatusEnumPause() {
        ScheduleConstants.Status pause = ScheduleConstants.Status.PAUSE;
        assertEquals("1", pause.getValue());
        assertEquals("1", pause.value);
    }

    @Test
    @DisplayName("测试所有 misfire 策略值唯一性")
    void testMisfirePoliciesUnique() {
        String[] policies = {
                ScheduleConstants.MISFIRE_DEFAULT,
                ScheduleConstants.MISFIRE_IGNORE_MISFIRES,
                ScheduleConstants.MISFIRE_FIRE_AND_PROCEED,
                ScheduleConstants.MISFIRE_DO_NOTHING
        };

        // 验证所有策略值不同
        for (int i = 0; i < policies.length; i++) {
            for (int j = i + 1; j < policies.length; j++) {
                assertNotEquals(policies[i], policies[j],
                        "Misfire policies should have unique values");
            }
        }
    }

    @Test
    @DisplayName("测试 Status 枚举值唯一性")
    void testStatusEnumValuesUnique() {
        assertNotEquals(
                ScheduleConstants.Status.NORMAL.getValue(),
                ScheduleConstants.Status.PAUSE.getValue()
        );
    }

    @Test
    @DisplayName("测试 Status 枚举包含所有状态")
    void testStatusEnumContainsAllStatuses() {
        ScheduleConstants.Status[] statuses = ScheduleConstants.Status.values();
        assertEquals(2, statuses.length);

        // 验证包含 NORMAL 和 PAUSE
        boolean hasNormal = false;
        boolean hasPause = false;
        for (ScheduleConstants.Status status : statuses) {
            if (status == ScheduleConstants.Status.NORMAL) {
                hasNormal = true;
            }
            if (status == ScheduleConstants.Status.PAUSE) {
                hasPause = true;
            }
        }
        assertTrue(hasNormal, "Should contain NORMAL status");
        assertTrue(hasPause, "Should contain PAUSE status");
    }

    @Test
    @DisplayName("测试 Status 枚举 valueOf")
    void testStatusEnumValueOf() {
        assertEquals(ScheduleConstants.Status.NORMAL,
                ScheduleConstants.Status.valueOf("NORMAL"));
        assertEquals(ScheduleConstants.Status.PAUSE,
                ScheduleConstants.Status.valueOf("PAUSE"));
    }

    @Test
    @DisplayName("测试常量值与数据库存储格式一致")
    void testConstantValuesMatchDatabaseFormat() {
        // 确保常量值是字符串形式的数字，与数据库存储一致
        assertTrue(ScheduleConstants.MISFIRE_DEFAULT.matches("\\d"));
        assertTrue(ScheduleConstants.MISFIRE_IGNORE_MISFIRES.matches("\\d"));
        assertTrue(ScheduleConstants.MISFIRE_FIRE_AND_PROCEED.matches("\\d"));
        assertTrue(ScheduleConstants.MISFIRE_DO_NOTHING.matches("\\d"));
        assertTrue(ScheduleConstants.Status.NORMAL.getValue().matches("\\d"));
        assertTrue(ScheduleConstants.Status.PAUSE.getValue().matches("\\d"));
    }
}
