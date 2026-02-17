package com.zjjh.fdtemp.common.utils.bean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * BeanValidators bean对象属性验证工具类测试
 */
@DisplayName("BeanValidators bean对象属性验证测试")
class BeanValidatorsTest {

    // 测试用的简单类
    static class TestBean {
        private String name;
        private Integer age;

        public TestBean(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }
    }

    @Nested
    @DisplayName("validateWithException 测试")
    class ValidateWithExceptionTest {
        @Test
        @DisplayName("验证通过不抛出异常")
        void validateWithException_WhenValid_ShouldNotThrow() throws ConstraintViolationException {
            Validator validator = mock(Validator.class);
            TestBean bean = new TestBean("test", 25);
            Set<ConstraintViolation<TestBean>> violations = new HashSet<>();

            when(validator.validate(bean)).thenReturn(violations);

            assertDoesNotThrow(() -> BeanValidators.validateWithException(validator, bean));
        }

        @Test
        @DisplayName("验证失败抛出异常")
        void validateWithException_WhenInvalid_ShouldThrow() {
            Validator validator = mock(Validator.class);
            TestBean bean = new TestBean(null, null);
            Set<ConstraintViolation<TestBean>> violations = new HashSet<>();
            ConstraintViolation<TestBean> violation = mock(ConstraintViolation.class);
            violations.add(violation);

            when(validator.validate(bean)).thenReturn(violations);

            assertThrows(ConstraintViolationException.class,
                    () -> BeanValidators.validateWithException(validator, bean));
        }

        @Test
        @DisplayName("带groups参数验证")
        void validateWithException_WithGroups_ShouldUseGroups() throws ConstraintViolationException {
            Validator validator = mock(Validator.class);
            TestBean bean = new TestBean("test", 25);
            Set<ConstraintViolation<TestBean>> violations = new HashSet<>();

            when(validator.validate(bean, TestGroup.class)).thenReturn(violations);

            assertDoesNotThrow(() -> BeanValidators.validateWithException(validator, bean, TestGroup.class));
            verify(validator).validate(bean, TestGroup.class);
        }
    }

    // 测试用的分组接口
    interface TestGroup {}
}
