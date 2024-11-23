package com.hb.concert.support.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ErrorInfo {
    private String message;
    private String details;
    private String path;
    private Map<String, String[]> parameters;
    private String userAgent;
    private String method;
    private String className;
}
