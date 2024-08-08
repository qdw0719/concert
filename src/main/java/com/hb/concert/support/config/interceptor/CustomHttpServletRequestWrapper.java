package com.hb.concert.support.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.HashMap;
import java.util.Map;

public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final Map<String, String[]> customParams;

    public CustomHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        customParams = new HashMap<>(request.getParameterMap());
    }

    public void addParameter(String name, String value) {
        customParams.put(name, new String[]{value});
    }

    @Override
    public String getParameter(String name) {
        String[] values = customParams.get(name);
        return values != null && values.length > 0 ? values[0] : null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return customParams;
    }

    @Override
    public String[] getParameterValues(String name) {
        return customParams.get(name);
    }
}
