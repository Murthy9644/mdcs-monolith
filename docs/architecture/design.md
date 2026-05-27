# Master - Workers Models
Main thread assigns jobs to different worker threads and manages the following step from resultant states from workers

## Expected Workers for v1.0.0
- Bootstrap
- Auth
- Command Router

## Transaction Models
### Worker &rarr; Master Report
> May change later as required
- job
- job status
- app state
- summary

### Workers &rarr; Master Request
- requested thread id
- mode (request / serve)
- event (print / scan / instance)
- latch

---
