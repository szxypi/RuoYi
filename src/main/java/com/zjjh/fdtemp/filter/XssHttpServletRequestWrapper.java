package com.zjjh.fdtemp.filter;

import com.zjjh.fdtemp.common.utils.html.EscapeUtil;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * XSS 过滤处理：覆盖 getParameterValues、getParameter 和请求体
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value != null) {
            return EscapeUtil.clean(value);
        }
        return value;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            int length = values.length;
            String[] escapedValues = new String[length];
            for (int i = 0; i < length; i++) {
                escapedValues[i] = EscapeUtil.clean(values[i]);
            }
            return escapedValues;
        }
        return values;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 仅对 JSON 请求体做 XSS 清理
        if (!isJsonRequest()) {
            return super.getInputStream();
        }
        // 读取原始请求体
        String body = readBody();
        if (body == null || body.isEmpty()) {
            return super.getInputStream();
        }
        // 对请求体做 XSS 清理
        String cleanBody = EscapeUtil.clean(body);
        byte[] bytes = cleanBody.getBytes(StandardCharsets.UTF_8);
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return bais.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // no-op
            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    /**
     * 判断是否为 JSON Content-Type
     */
    private boolean isJsonRequest() {
        String contentType = super.getHeader(HttpHeaders.CONTENT_TYPE);
        return contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE);
    }

    /**
     * 从原始 InputStream 读取请求体
     */
    private String readBody() throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(super.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
}