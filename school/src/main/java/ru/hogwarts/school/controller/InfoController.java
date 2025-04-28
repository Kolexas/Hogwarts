package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/port")
@Profile("portchange")
public class InfoController {

    @Value("${server.port}")
    private String port;

    @GetMapping
    public ResponseEntity<String> getPort() {
        return ResponseEntity.ok(port);
    }
}
