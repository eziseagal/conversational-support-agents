# API Rate Limits and Usage

## Standard Limits
Our REST API enforces a strict rate limit to ensure stability across all tenants.
- **Free Tier**: 100 requests per minute.
- **Pro Tier**: 1,000 requests per minute.
- **Enterprise Tier**: 5,000 requests per minute.

## Handling 429 Too Many Requests
If you exceed your rate limit, the API will return a `429 Too Many Requests` status code. The response headers will include a `Retry-After` header indicating how many seconds you must wait before making another request. We strongly recommend implementing an exponential backoff strategy in your HTTP clients.