package com.zjjh.fdtemp.common.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Arith 精确浮点数运算工具类单元测试
 */
@DisplayName("Arith 精确浮点数运算测试")
class ArithTest {

    private static final double DELTA = 0.0000001;

    @Nested
    @DisplayName("add 加法测试")
    class AddTest {
        @Test
        @DisplayName("正常加法运算")
        void add_WhenNormalCase_ShouldReturnCorrectResult() {
            double result = Arith.add(0.1, 0.2);
            assertEquals(0.3, result, DELTA);
        }

        @Test
        @DisplayName("加法运算-负数")
        void add_WhenNegativeNumbers_ShouldReturnCorrectResult() {
            double result = Arith.add(-0.1, -0.2);
            assertEquals(-0.3, result, DELTA);
        }

        @Test
        @DisplayName("加法运算-零")
        void add_WhenZero_ShouldReturnOtherNumber() {
            double result = Arith.add(0, 5.5);
            assertEquals(5.5, result, DELTA);
        }

        @Test
        @DisplayName("加法运算-大数")
        void add_WhenLargeNumbers_ShouldReturnCorrectResult() {
            double result = Arith.add(1000000.123456, 2000000.654321);
            assertEquals(3000000.777777, result, DELTA);
        }
    }

    @Nested
    @DisplayName("sub 减法测试")
    class SubTest {
        @Test
        @DisplayName("正常减法运算")
        void sub_WhenNormalCase_ShouldReturnCorrectResult() {
            double result = Arith.sub(0.3, 0.1);
            assertEquals(0.2, result, DELTA);
        }

        @Test
        @DisplayName("减法运算-结果为负")
        void sub_WhenResultNegative_ShouldReturnCorrectResult() {
            double result = Arith.sub(0.1, 0.3);
            assertEquals(-0.2, result, DELTA);
        }

        @Test
        @DisplayName("减法运算-零")
        void sub_WhenZero_ShouldReturnOtherNumber() {
            double result = Arith.sub(5.5, 0);
            assertEquals(5.5, result, DELTA);
        }
    }

    @Nested
    @DisplayName("mul 乘法测试")
    class MulTest {
        @Test
        @DisplayName("正常乘法运算")
        void mul_WhenNormalCase_ShouldReturnCorrectResult() {
            double result = Arith.mul(0.1, 0.2);
            assertEquals(0.02, result, DELTA);
        }

        @Test
        @DisplayName("乘法运算-负数")
        void mul_WhenNegativeNumbers_ShouldReturnCorrectResult() {
            double result = Arith.mul(-2, 3);
            assertEquals(-6, result, DELTA);
        }

        @Test
        @DisplayName("乘法运算-零")
        void mul_WhenZero_ShouldReturnZero() {
            double result = Arith.mul(0, 5.5);
            assertEquals(0, result, DELTA);
        }

        @Test
        @DisplayName("乘法运算-大数")
        void mul_WhenLargeNumbers_ShouldReturnCorrectResult() {
            double result = Arith.mul(1000.5, 2000.5);
            assertEquals(2001500.25, result, DELTA);
        }
    }

    @Nested
    @DisplayName("div 除法测试")
    class DivTest {
        @Test
        @DisplayName("正常除法运算")
        void div_WhenNormalCase_ShouldReturnCorrectResult() {
            double result = Arith.div(0.6, 0.2);
            assertEquals(3, result, DELTA);
        }

        @Test
        @DisplayName("除法运算-指定精度")
        void div_WhenWithScale_ShouldReturnCorrectResult() {
            double result = Arith.div(1, 3, 2);
            assertEquals(0.33, result, DELTA);
        }

        @Test
        @DisplayName("除法运算-被除数为零")
        void div_WhenDividendIsZero_ShouldReturnZero() {
            double result = Arith.div(0, 5);
            assertEquals(0, result, DELTA);
        }

        @Test
        @DisplayName("除法运算-精度为负数时抛出异常")
        void div_WhenScaleIsNegative_ShouldThrowException() {
            assertThrows(IllegalArgumentException.class, () -> Arith.div(1, 3, -1));
        }

        @Test
        @DisplayName("除法运算-默认精度")
        void div_WhenDefaultScale_ShouldReturnCorrectResult() {
            double result = Arith.div(1, 6);
            assertEquals(0.1666666667, result, DELTA);
        }
    }

    @Nested
    @DisplayName("round 四舍五入测试")
    class RoundTest {
        @Test
        @DisplayName("正常四舍五入")
        void round_WhenNormalCase_ShouldReturnCorrectResult() {
            double result = Arith.round(0.555, 2);
            assertEquals(0.56, result, DELTA);
        }

        @Test
        @DisplayName("四舍五入-精度为0")
        void round_WhenScaleIsZero_ShouldReturnInteger() {
            double result = Arith.round(0.5, 0);
            assertEquals(1, result, DELTA);
        }

        @Test
        @DisplayName("四舍五入-精度为负数时抛出异常")
        void round_WhenScaleIsNegative_ShouldThrowException() {
            assertThrows(IllegalArgumentException.class, () -> Arith.round(0.5, -1));
        }

        @Test
        @DisplayName("四舍五入-负数")
        void round_WhenNegativeNumber_ShouldReturnCorrectResult() {
            double result = Arith.round(-0.555, 2);
            assertEquals(-0.56, result, DELTA);
        }
    }

    @Nested
    @DisplayName("综合运算测试")
    class CombinedOperationsTest {
        @Test
        @DisplayName("混合运算")
        void combinedOperations_ShouldReturnCorrectResult() {
            // (0.1 + 0.2) * 3 - 0.5 / 2 = 0.3 * 3 - 0.25 = 0.9 - 0.25 = 0.65
            double step1 = Arith.add(0.1, 0.2);
            double step2 = Arith.mul(step1, 3);
            double step3 = Arith.div(0.5, 2);
            double result = Arith.sub(step2, step3);
            assertEquals(0.65, result, DELTA);
        }
    }
}
