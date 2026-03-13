
# Spring Boot Performance Test Report

## Benchmark Summary

| Experiment | Config Change | RPS (req/sec) | Avg Latency | p95 Latency | Errors | Observation |
|---|---|---|---|---|---|---|
| Baseline (sleep + small load) | Thread.sleep(10) | ~11.5K | ~269 ms | ~608 ms | 0% | Blocking thread caused queueing |
| Removed sleep | No delay in controller | ~36.6K | ~64 ms | ~181 ms | 0.01% | Throughput improved significantly |
| Ramp test | Staged load (0 → 1000 → 0 VUs) | ~9.3K | ~42 ms | ~81 ms | 0% | Lower RPS because load ramped gradually |
| Stress test | vus=8000 | ~39.8K | ~121 ms | ~282 ms | ~0.88% | Near system throughput limit |
| Tomcat tuning | threads=500, connections=30000 | ~37.3K | ~127 ms | ~267 ms | ~0.84% | Minimal improvement |
| JVM tuning | -Xms2g -Xmx2g -XX:+UseG1GC -XX:+AlwaysPreTouch | ~34.3K | ~150 ms | ~311 ms | ~0.73% | Slight decrease in throughput |

---

## Key Lessons

| Area | What Happened | Reason | Lesson |
|---|---|---|---|
| Blocking Code | Throughput dropped significantly with Thread.sleep(10) | Threads remained occupied longer | Avoid blocking operations |
| Thread-per-request | Removing delay increased RPS dramatically | Threads free faster | Faster responses increase throughput |
| Tomcat Tuning | Increasing thread pool didn't help much | Threads were not the bottleneck | Tune threads only when blocking I/O exists |
| JVM Tuning | Minimal impact on RPS | GC not a bottleneck | JVM tuning helps when GC or memory is the bottleneck, not CPU (JVM tuning matters more when there is heavy allocation, large heaps, or frequent GC pauses) |
| Hardware Limits | Throughput plateaued around ~35K–40K RPS | CPU & networking limits | Hardware eventually becomes the limit |
| Load Generator | k6 and app ran on same machine | Shared CPU resources | Use separate machines for accurate tests |

---

## JVM Configurations

Run application with JVM options:

java -Xms2g -Xmx2g -XX:+UseG1GC -XX:+AlwaysPreTouch -jar springboot-performance-test.jar

In IntelliJ:

IntelliJ IDE → Edit Run Configuration → VM Options

-Xms2g -Xmx2g -XX:+UseG1GC -XX:+AlwaysPreTouch

### JVM Flags Explained

| JVM Option | Description |
|---|---|
| -Xms2g | Initial heap size = 2 GB |
| -Xmx2g | Maximum heap size = 2 GB |
| -XX:+UseG1GC | Use G1 Garbage Collector |
| -XX:+AlwaysPreTouch | Allocate heap memory at JVM startup |

These settings help stabilize memory usage and reduce heap resizing during runtime.

---

## Approximate Capacity Observed

| Scenario | Capacity |
|---|---|
| Blocking endpoint | ~10–12K RPS |
| Simple endpoint | ~35–40K RPS |
| Ramp load | ~9K RPS average |

---

## Core Performance Principle

Throughput ≈ concurrency / response_time

Reducing response time directly increases how many requests per second the system can handle.

---

## Request Flow

Client (k6)
   ↓
TCP Connection
   ↓
Tomcat Accept Queue
   ↓
Tomcat Worker Thread Pool
   ↓
Spring Controller
   ↓
Response
