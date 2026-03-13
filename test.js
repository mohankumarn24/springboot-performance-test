import http from 'k6/http';
import { sleep, check } from 'k6';

/*
export const options = {
  vus: 8000,                // maximum users. K6 max use upto 8000 users. Not guaranteed and depends on system
  duration: '30s',
};
*/

export const options = {
  stages: [
    { duration: '20s', target: 500 },   // 0 - 500  users (each user sending requests back to back for 20s)
    { duration: '20s', target: 2000 },  // 500 - 2000 users (each user sending requests back to back for 20s)
    { duration: '20s', target: 5000 },  // 2000 - 5000 users (each user sending requests back to back for 20s)
    { duration: '20s', target: 8000 },  // 5000 - 8000 users (each user sending requests back to back for 20s)
    { duration: '20s', target: 0 }      // 8000 - 0 (slowly ramp down users for 20s)
  ],
};

export default function() {
  let res = http.get('http://localhost:8080/api/v1/performancetest');
  // check(res, { 'status is 200': (r) => r.status === 200 });
}
