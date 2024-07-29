package com.hb.concert;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test-api")
public class TestController {

    @GetMapping("test")
    public String test() {
        return "hello world";
    }
}
