package net.projectsync.springboot_performance_test.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/performancetest")
public class PerformanceController {

    @GetMapping
    public ResponseEntity<String> performanceTest() throws InterruptedException{

        // Thread.sleep(10);
        return new ResponseEntity<>("CurrentTime: " + LocalDateTime.now(), HttpStatus.OK);
    }
}
