package com.zjjh.fdtemp.filter;

import com.zjjh.fdtemp.common.utils.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * 自定义xss校验注解实现
 *
 * @author szx
 */
public class XssValidator implements ConstraintValidator<Xss, String> {
    private static final Pattern HTML_PATTERN = Pattern.compile("<(\\S*?)[^>]*>.*?|<.*? />");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(value)) {
            return true;
        }
        return !containsHtml(value);
    }

    public static boolean containsHtml(String value) {
        return HTML_PATTERN.matcher(value).find();
    }
}