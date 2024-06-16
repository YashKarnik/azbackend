package com.azbackend.azbackend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/application-insights")
@RequiredArgsConstructor
public class AppInsightsController {

    @GetMapping(value = "/common-log")
    public void commonLog() {
        log.warn("Called enqueue method");
    }

    @GetMapping(value = "/exception-raise")
    public void exceptionRaise() throws FileNotFoundException {
        log.error("LOGGER ERRRO: hello file not fount");
        throw new FileNotFoundException("hello file not fount");
    }

}
