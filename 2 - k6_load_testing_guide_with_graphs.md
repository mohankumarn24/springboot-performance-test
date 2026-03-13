
# k6 Load Testing Guide

## 1. Install k6

### Windows

```bash
winget install k6 --source winget
```

Verify installation

```bash
k6 version
```

---

# 2. Basic k6 Commands

Run a script

```bash
k6 run test.js
```

Export summary

```bash
k6 run test.js --summary-export=result.json
```

Run with environment variable

```bash
k6 run -e BASE_URL=http://localhost:8080 test.js
```

---

# 3. Load Testing Configurations

## 3.1 Fixed Virtual Users (Basic Load Test)

```javascript
export const options = {
  vus: 100,          // number of concurrent virtual users
  duration: '1m',    // total test duration
};
```

Explanation:

| Property | Meaning |
|---|---|
vus | number of concurrent simulated users |
duration | how long the test runs |

---

## 3.2 Ramp Load (Realistic Traffic Pattern)

```javascript
export const options = {
  stages: [
    { duration: '30s', target: 100 },   // ramp from 0 → 100 users
    { duration: '1m', target: 500 },    // ramp from 100 → 500 users
    { duration: '2m', target: 500 },    // steady traffic
    { duration: '30s', target: 0 },     // ramp down
  ],
};
```

### Traffic Shape (Approximate)

```
Users
500 |           ________
    |          /        \
100 | ________/          \_______
    |
    +--------------------------------
      0s     30s       2m       3m
```

---

## 3.3 Constant Request Rate (Production Style)

```javascript
export const options = {
  scenarios: {
    constant_request_rate: {
      executor: 'constant-arrival-rate',
      rate: 1000,        // requests per second
      timeUnit: '1s',
      duration: '2m',
      preAllocatedVUs: 200,
      maxVUs: 2000,
    },
  },
};
```

Meaning: send **1000 requests per second for 2 minutes**.

---

## 3.4 Stress Test (Find System Limits)

```javascript
export const options = {
  stages: [
    { duration: '1m', target: 500 },
    { duration: '1m', target: 2000 },
    { duration: '1m', target: 5000 },
    { duration: '1m', target: 8000 },
  ],
};
```

### Stress Curve

```
Users
8000 |              ______
     |             /
5000 |           /
     |         /
2000 |       /
     |     /
500  |____/
     |
     +-----------------------------
       time →
```

Goal:

- Identify breaking point
- Observe latency spikes
- Detect failures

---

## 3.5 Spike Test (Traffic Burst)

```javascript
export const options = {
  stages: [
    { duration: '30s', target: 100 },
    { duration: '10s', target: 3000 },
    { duration: '30s', target: 100 },
  ],
};
```

Spike traffic visualization:

```
Users
3000 |        /     |       /  \
100  |______/    \______
     |
     +----------------------
       time →
```

Used to test:

- flash sales
- sudden marketing traffic
- viral spikes

---

## 3.6 Soak Test (Long Duration Stability)

```javascript
export const options = {
  vus: 300,
  duration: '2h',
};
```

Purpose:

- detect memory leaks
- identify resource exhaustion
- test system stability

---

# 4. Example Spring Boot API Test

```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 200,          // concurrent users
  duration: '1m',    // test duration
};

export default function () {

  let res = http.get('http://localhost:8080/api/v1/performancetest');

  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
  });

  sleep(1); // simulate user think time
}
```

---

# 5. Important k6 Metrics

| Metric | Meaning |
|---|---|
http_reqs | total requests sent |
http_req_duration | response time |
p95 | 95% of requests completed within this time |
http_req_failed | percentage of failed requests |
iterations | number of script executions |

---

# 6. Approximate Expected Results (Typical API)

These are **approximate values** for a simple API running locally.

| Load Level | VUs | Expected RPS | Avg Latency |
|---|---|---|---|
Light Load | 50 | 1K–2K | 10–30 ms |
Moderate Load | 200 | 5K–10K | 30–80 ms |
Heavy Load | 1000 | 15K–30K | 80–200 ms |
Extreme Load | 5000+ | 30K–40K | 150–400 ms |

---

# 7. Latency vs Load Relationship

Approximate behavior:

```
Latency
400ms |             ________
      |            /
200ms |          /
      |        /
100ms |      /
      |    /
 50ms |___/
      |
      +------------------------------
            Load (VUs)
```

Explanation:

- As load increases, latency remains stable initially
- When CPU or threads saturate, latency increases rapidly

---

# 8. Best Practices

1. Run **load generator and server on different machines**
2. Monitor **CPU, memory, and GC**
3. Focus on **p95 latency instead of average**
4. Increase load gradually
5. Establish baseline before stress testing

