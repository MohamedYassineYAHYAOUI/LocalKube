package fr.umlv.LocalKube.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class DemoController {

    private final AtomicInteger demoId = new AtomicInteger();

    @GetMapping("/demo")
    @ResponseBody
    public Demo Hello(@RequestParam(required = false, defaultValue = "user")String name){
        return new Demo(demoId.incrementAndGet(), name );
    }
}
