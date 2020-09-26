# Katan
Katan is a fast, simple with high-availability game server management panel running isolated in [Docker](https://www.docker.com/) containers with a Web server built with [Ktor](https://ktor.io/).

[![Open Source](https://badges.frapsoft.com/os/v1/open-source.svg?v=103)](https://github.com/ellerbrock/open-source-badges/)
[![Stars](https://img.shields.io/github/stars/KatanPanel/Katan.svg?color=1bcc1b)](https://github.com/KatanPanel/Katan/stargazers)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/a9844adeafb449f487368a84f5eb1df5)](https://www.codacy.com/app/KatanPanel/Katan?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=KatanPanel/Katan&amp;utm_campaign=Badge_Grade)

## Contents
  * [Motivation](#motivation)
  * [Features](#features)
  * [Modules](#modules)
  * [Community](#community)
  * [Contributing](CONTRIBUTING.md)
  * [Issue reporting](https://github.com/KatanPanel/Katan/issues)
  * [Documentation](https://github.com/KatanPanel/Katan/wiki)
  * [License](LICENSE)
  
## Motivation
The initial motivation behind the creation of the Katan is partly for server handling panels to be paid for, licensed, and those that are not paid very poorly maintained with several basic problems that can be solved but there is no maintenance.
<br><br>
Katan combines a beautiful UI for clients in an excellent code base for the server with support for built-in plugins without the need for help from the creator, everyone can create and modify their own panels as they wish using the public API.

### Performance
Built to be functional and fault-tolerant, completely asynchronous and multi-threaded that even low-powered machines will handle it, running servers in isolated Docker containers, in conjunction with Redis caching service.

### Metrics
Katan Web Server has support for system metrics service using Prometheus, which absolutely no panel I used got support, you can enable in the settings if you have installed it on your machine.
  
### Freedom
In Katan it is possible to change absolutely everything in your containers via API (directly) or through the CLI and configuration files, for example you can define a predefined `docker-compose.yml` file for composition and use it during creation of the servers.

Imagine you have a composition file named `some-game`, when using` katan server create -c some-game` you will create a server based on that file.
 
## Features
### Command Line Interface
Create and manage Katan servers, accounts, settings and status right from Katan's built-in command line interface.

### Web server
If you are not familiar with the command line, we have the web server with a beautiful UI with the same functionality for those who prefer ease and simplicity.

### Facility
Installing, configuring and running Katan is extremely easy and fast, by default it comes ready to use with all the necessary features and you can configure it to your liking and add new features such as metrics.
 
## Modules
| Name           | Description |
| -------------- | ----------- |
| api            | High-level public API used in all modules. |
| bootstrap      | The launcher, is the starting point of Katan responsible for starting the Core, CLI and WebServer. |
| cli            | Built-in command line interface (type "katan"). |
| common         | Contains common code used in more than one module in the project. |
| docker-compose | Add [docker-compose](https://docs.docker.com/compose/) support not provided by the [Java Docker Client](https://github.com/docker-java/docker-java) (used by Katan) |
| web-server     | Katan's web server, uses [Ktor](https://ktor.io) with which the [Daemon](https://github.com/KatanPanel/daemon) interacts. |
  
## Community
Join our communities, discuss changes, discover news and things to come on Katan.
  * [Discord](https://discord.gg/DfxfXhm)
  * [Issues report](https://github.com/KatanPanel/Katan/issues)
