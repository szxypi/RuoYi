package com.zjjh.fdtemp.service;

import com.zjjh.fdtemp.common.utils.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 定时任务调度示例
 *
 * @author szx
 */
@Component("demoTask")
public class DemoTask {
    public void multipleParams(String s, Boolean b, Long l, Double d, Integer i) {
        System.out.println(StringUtils.format("执行多参方法： 字符串类型{}，布尔类型{}，长整型{}，浮点型{}，整形{}", s, b, l, d, i));
    }

    public void withParams(String params) {
        System.out.println("执行有参方法：" + params);
    }

    public void noParams() {
        System.out.println("执行无参方法");
    }
}
