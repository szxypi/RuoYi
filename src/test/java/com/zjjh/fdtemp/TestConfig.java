package com.zjjh.fdtemp;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 测试环境配置
 */
@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public Scheduler scheduler() throws SchedulerException {
        // 创建内存模式的Scheduler用于测试
        StdSchedulerFactory factory = new StdSchedulerFactory();
        return factory.getScheduler();
    }
}
