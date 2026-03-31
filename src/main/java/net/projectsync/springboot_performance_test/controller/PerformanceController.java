package net.projectsync.springboot_performance_test.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/performancetest")
public class PerformanceController {

    @GetMapping
    public ResponseEntity<String> performanceTest() throws InterruptedException{

        // Thread.sleep(10);
        return new ResponseEntity<>("CurrentTime: " + LocalDateTime.now(), HttpStatus.OK);
    }

    /**
     * http://localhost:8080/api/v1/performancetest/gc
     * http://localhost:8080/actuator/metrics/jvm.memory.used
     * http://localhost:8080/actuator/metrics/jvm.memory.max
     * http://localhost:8080/actuator/metrics/jvm.gc.pause
     */
    @GetMapping("/gc")
    public String gcTest() {

        List<byte[]> temp = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {

            // Allocate 1 MB each iteration -> 1 GB
            // temp.add(new byte[10 * 1024 * 1024]);

            // Allocate 10 MB each iteration -> 10 GB
            temp.add(new byte[10 * 1024 * 1024]);

            // 100 GB
            // temp.add(new byte[100 * 1024 * 1024]); -> java.lang.OutOfMemoryError: Java heap space
        }

        return "Allocated objects at " + LocalDateTime.now();
    }
}
