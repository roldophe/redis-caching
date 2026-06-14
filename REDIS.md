**Basic deployment:**

```bash
docker run --name redis -d -p 6379:6379 redis:latest redis-server
```

**With persistence:**

```bash
docker run --name redis-persistent -d \
  -v redis-data:/data \
  -p 6379:6379 \
  redis:latest redis-server --appendonly yes
```

The `-v redis-data:/data` volume stores data on disk. The `--appendonly yes` flag enables AOF persistence (writes each command to a log).

**Docker Compose (for multi-container setups):**

```yaml
services:
  redis:
    image: redis:latest
    container_name: redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    command: redis-server --appendonly yes

volumes:
  redis-data:
```

Run with `docker compose up -d`.

**Common flags:**
- `-d` — run in background
- `-p 6379:6379` — expose Redis port
- `-v <volume>:/data` — persist data
- `--appendonly yes` — enable AOF logging
- `--requirepass <password>` — set password

Sources:
- https://hub.docker.com/_/redis
- https://docs.docker.com/reference/cli/docker/container/run/

Let me know if you have any questions!