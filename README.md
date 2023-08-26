# Katan

Katan is a hyper fast and secure game server manager that allows developers to operate and scale up their game servers
with low cost while reducing the level of engineering and operational effort.

* [Official Website](https://katan.org)
* [Discord Community](https://discord.gg/qTgBt6xjTT)
* [License](./LICENSE)

## Setup local development environment

```
docker-compose -f docker-compose.dev.yml up -d
```

For consecutive builds
```
docker-compose -f docker-compose.dev.yml up --build -d --force-recreate
```