package com.zjjh.fdtemp.common.utils.quartz;

import com.zjjh.fdtemp.common.utils.JobInvokeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JobInvokeUtil 工具类单元测试
 */
@DisplayName("JobInvokeUtil 工具类测试")
class JobInvokeUtilTest {

    @Test
    @DisplayName("测试 getBeanName - 带 bean 名称")
    void testGetBeanName_WithBeanName() {
        String invokeTarget = "testService.testMethod()";
        String beanName = JobInvokeUtil.getBeanName(invokeTarget);
        assertEquals("testService", beanName);
    }

    @Test
    @DisplayName("测试 getBeanName - 带完整类名")
    void testGetBeanName_WithFullClassName() {
        String invokeTarget = "com.example.service.TestService.testMethod()";
        String beanName = JobInvokeUtil.getBeanName(invokeTarget);
        assertEquals("com.example.service.TestService", beanName);
    }

    @Test
    @DisplayName("测试 getBeanName - 带参数的方法调用")
    void testGetBeanName_WithParams() {
        String invokeTarget = "testService.testMethod('param', 123)";
        String beanName = JobInvokeUtil.getBeanName(invokeTarget);
        assertEquals("testService", beanName);
    }

    @Test
    @DisplayName("测试 getMethodName - 简单方法调用")
    void testGetMethodName_Simple() {
        String invokeTarget = "testService.testMethod()";
        String methodName = JobInvokeUtil.getMethodName(invokeTarget);
        assertEquals("testMethod", methodName);
    }

    @Test
    @DisplayName("测试 getMethodName - 带完整类名的方法调用")
    void testGetMethodName_WithFullClassName() {
        String invokeTarget = "com.example.service.TestService.testMethod()";
        String methodName = JobInvokeUtil.getMethodName(invokeTarget);
        assertEquals("testMethod", methodName);
    }

    @Test
    @DisplayName("测试 getMethodName - 带参数的方法调用")
    void testGetMethodName_WithParams() {
        String invokeTarget = "testService.testMethod('param', 123)";
        String methodName = JobInvokeUtil.getMethodName(invokeTarget);
        assertEquals("testMethod", methodName);
    }

    @Test
    @DisplayName("测试 isValidClassName - bean 名称 (返回 false)")
    void testIsValidClassName_BeanName() {
        assertFalse(JobInvokeUtil.isValidClassName("testService"));
        assertFalse(JobInvokeUtil.isValidClassName("myBean"));
    }

    @Test
    @DisplayName("测试 isValidClassName - 完整类名 (返回 true)")
    void testIsValidClassName_FullClassName() {
        assertTrue(JobInvokeUtil.isValidClassName("com.example.TestService"));
        assertTrue(JobInvokeUtil.isValidClassName("com.zjjh.fdtemp.service.TestService"));
    }

    @Test
    @DisplayName("测试 isValidClassName - 边界情况")
    void testIsValidClassName_EdgeCases() {
        // 只有一个点，不算完整类名
        assertFalse(JobInvokeUtil.isValidClassName("test.Service"));
    }

    @Test
    @DisplayName("测试 getMethodParams - 无参数")
    void testGetMethodParams_NoParams() {
        String invokeTarget = "testService.testMethod()";
        List<Object[]> params = JobInvokeUtil.getMethodParams(invokeTarget);
        assertNull(params);
    }

    @Test
    @DisplayName("测试 getMethodParams - 字符串参数")
    void testGetMethodParams_StringParam() {
        String invokeTarget = "testService.testMethod('hello')";
        List<Object[]> params = JobInvokeUtil.getMethodParams(invokeTarget);

        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals("hello", params.get(0)[0]);
        assertEquals(String.class, params.get(0)[1]);
    }

    @Test
    @DisplayName("测试 getMethodParams - 双引号字符串参数")
    void testGetMethodParams_DoubleQuotedString() {
        String invokeTarget = "testService.testMethod(\"hello\")";
        List<Object[]> params = JobInvokeUtil.getMethodParams(invokeTarget);

        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals("hello", params.get(0)[0]);
        assertEquals(String.class, params.get(0)[1]);
    }

    @Test
    @DisplayName("测试 getMethodParams - 整数参数")
    void testGetMethodParams_IntegerParam() {
        String invokeTarget = "testService.testMethod(123)";
        List<Object[]> params = JobInvokeUtil.getMethodParams(invokeTarget);

        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals(123, params.get(0)[0]);
        assertEquals(Integer.class, params.get(0)[1]);
    }

    @Test
    @DisplayName("测试 getMethodParams - Long 参数")
    void testGetMethodParams_LongParam() {
        String invokeTarget = "testService.testMethod(123L)";
        List<Object[]> params = JobInvokeUtil.getMethodParams(invokeTarget);

        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals(123L, params.get(0)[0]);
        assertEquals(Long.class, params.get(0)[1]);
    }

    @Test
    @DisplayName("测试 getMethodParams - Double 参数")
    void testGetMethodParams_DoubleParam() {
        String invokeTarget = "testService.testMethod(3.14D)";
        List<Object[]> params = JobInvokeUtil.getMethodParams(invokeTarget);

        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals(3.14, (Double) params.get(0)[0], 0.001);
        assertEquals(Double.class, params.get(0)[1]);
    }

    @Test
    @DisplayName("测试 getMethodParams - Boolean 参数")
    void testGetMethodParams_BooleanParam() {
        String invokeTarget = "testService.testMethod(true)";
        List<Object[]> params = JobInvokeUtil.getMethodParams(invokeTarget);

        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals(true, params.get(0)[0]);
        assertEquals(Boolean.class, params.get(0)[1]);
    }

    @Test
    @DisplayName("测试 getMethodParams - false 布尔参数")
    void testGetMethodParams_FalseBooleanParam() {
        String invokeTarget = "testService.testMethod(false)";
        List<Object[]> params = JobInvokeUtil.getMethodParams(invokeTarget);

        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals(false, params.get(0)[0]);
        assertEquals(Boolean.class, params.get(0)[1]);
    }

    @Test
    @DisplayName("测试 getMethodParams - 多个参数")
    void testGetMethodParams_MultipleParams() {
        String invokeTarget = "testService.testMethod('hello', 123, true, 456L, 3.14D)";
        List<Object[]> params = JobInvokeUtil.getMethodParams(invokeTarget);

        assertNotNull(params);
        assertEquals(5, params.size());

        assertEquals("hello", params.get(0)[0]);
        assertEquals(String.class, params.get(0)[1]);

        assertEquals(123, params.get(1)[0]);
        assertEquals(Integer.class, params.get(1)[1]);

        assertEquals(true, params.get(2)[0]);
        assertEquals(Boolean.class, params.get(2)[1]);

        assertEquals(456L, params.get(3)[0]);
        assertEquals(Long.class, params.get(3)[1]);

        assertEquals(3.14, (Double) params.get(4)[0], 0.001);
        assertEquals(Double.class, params.get(4)[1]);
    }

    @Test
    @DisplayName("测试 getMethodParams - 包含逗号的字符串")
    void testGetMethodParams_StringWithComma() {
        String invokeTarget = "testService.testMethod('a,b,c')";
        List<Object[]> params = JobInvokeUtil.getMethodParams(invokeTarget);

        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals("a,b,c", params.get(0)[0]);
    }

    @Test
    @DisplayName("测试 getMethodParamsType")
    void testGetMethodParamsType() {
        String invokeTarget = "testService.testMethod('hello', 123, true)";
        List<Object[]> methodParams = JobInvokeUtil.getMethodParams(invokeTarget);
        Class<?>[] types = JobInvokeUtil.getMethodParamsType(methodParams);

        assertEquals(3, types.length);
        assertEquals(String.class, types[0]);
        assertEquals(Integer.class, types[1]);
        assertEquals(Boolean.class, types[2]);
    }

    @Test
    @DisplayName("测试 getMethodParamsValue")
    void testGetMethodParamsValue() {
        String invokeTarget = "testService.testMethod('hello', 123, true)";
        List<Object[]> methodParams = JobInvokeUtil.getMethodParams(invokeTarget);
        Object[] values = JobInvokeUtil.getMethodParamsValue(methodParams);

        assertEquals(3, values.length);
        assertEquals("hello", values[0]);
        assertEquals(123, values[1]);
        assertEquals(true, values[2]);
    }

    @ParameterizedTest
    @CsvSource({
            "testService.method(), testService, method",
            "service.method('param'), service, method",
            "com.example.Service.method(), com.example.Service, method"
    })
    @DisplayName("测试 getBeanName 和 getMethodName 组合")
    void testBeanNameAndMethodExtraction(String invokeTarget, String expectedBeanName, String expectedMethodName) {
        assertEquals(expectedBeanName, JobInvokeUtil.getBeanName(invokeTarget));
        assertEquals(expectedMethodName, JobInvokeUtil.getMethodName(invokeTarget));
    }

    @Test
    @DisplayName("测试复杂参数组合")
    void testComplexParamCombination() {
        String invokeTarget = "testService.complexMethod('string with spaces', 42, false, 999L, 2.5D)";
        List<Object[]> params = JobInvokeUtil.getMethodParams(invokeTarget);

        assertNotNull(params);
        assertEquals(5, params.size());
        assertEquals("string with spaces", params.get(0)[0]);
        assertEquals(42, params.get(1)[0]);
        assertEquals(false, params.get(2)[0]);
        assertEquals(999L, params.get(3)[0]);
        assertEquals(2.5D, (Double) params.get(4)[0], 0.001);
    }

    @Test
    @DisplayName("测试空括号")
    void testEmptyParentheses() {
        String invokeTarget = "testService.method()";
        List<Object[]> params = JobInvokeUtil.getMethodParams(invokeTarget);
        assertNull(params);
    }

    @Test
    @DisplayName("测试 TRUE 大写布尔值")
    void testUppercaseBooleanTrue() {
        String invokeTarget = "testService.method(TRUE)";
        List<Object[]> params = JobInvokeUtil.getMethodParams(invokeTarget);

        assertNotNull(params);
        assertEquals(true, params.get(0)[0]);
        assertEquals(Boolean.class, params.get(0)[1]);
    }

    @Test
    @DisplayName("测试 FALSE 大写布尔值")
    void testUppercaseBooleanFalse() {
        String invokeTarget = "testService.method(FALSE)";
        List<Object[]> params = JobInvokeUtil.getMethodParams(invokeTarget);

        assertNotNull(params);
        assertEquals(false, params.get(0)[0]);
        assertEquals(Boolean.class, params.get(0)[1]);
    }
}
