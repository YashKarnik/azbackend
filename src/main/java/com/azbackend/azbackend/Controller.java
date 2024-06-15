package com.azbackend.azbackend;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class Controller {

    @GetMapping
    public String echo() {
        return "Echoing...";
    }

    @GetMapping("/getenv")
    public String testEnv() {
        return "TEST_ENVVAR_FROM_AZURE = " + System.getenv("TEST_ENVVAR_FROM_AZURE");
    }

}
