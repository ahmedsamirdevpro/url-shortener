import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        cache_hit: {
            executor: 'constant-arrival-rate',
            rate: 5000,
            timeUnit: '1s',
            duration: '30s',
            preAllocatedVUs: 50,
            maxVUs: 500,
        },
    },

    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<100', 'p(99)<200'],
    },
};

export default function () {
    const response = http.get(
        'http://localhost:8080/BDFM9tY',
        {
            redirects: 0,
        }
    );

    check(response, {
        'status is 302': (r) => r.status === 302,
        'has Location header': (r) => r.headers['Location'] !== undefined,
    });
}