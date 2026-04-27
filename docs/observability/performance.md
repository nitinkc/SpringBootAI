# Performance Monitoring

## Key Metrics

```
Latency percentiles (for user-facing APIs):
P50:  100ms  (median, users happy)
P95:  500ms  (95% happy, alert if >2s)
P99:  2000ms (rarely hit)

Throughput:
Requests/second: Monitor for traffic spikes
Failing requests: Alert if >5% error rate

Resource usage:
CPU: Alert if >80%
Memory: Alert if >85%
Database connections: Alert if >90%
```

## Dashboards

### Real-Time Dashboard
```
┌──────────────────────────────────────────┐
│  AI System Health                      live │
├──────────────────────────────────────────┤
│ Latency: 234ms p95          | Cost: $0.45/hr  │
│ Throughput: 1.2K req/s      | Cache Hit: 45%  │
│ Error Rate: 0.2%            | Fallback: 1%    │
│ DB Conn Pool: 42/100        | AI Available: ✓ │
└──────────────────────────────────────────┘
```

### Trends Dashboard
```
Latency trend (24h):
  |         ___
  |      __/   \___  <- P95 trending up (bad!)
300ms
  |
  |   ___________   <- P50 stable (good)
100ms
  |_______________
    0h  6h 12h 18h 24h
```

## SLA Monitoring

```yaml
sla:
  latency:
    p95: 2s
    checking: every 5m
    alert: if 3 checks miss
  
  availability:
    target: 99.9%
    checking: continuous
    alert: if drops below 99.5%
  
  error_rate:
    target: <1%
    alert: if >2% for 5 minutes
```

---
