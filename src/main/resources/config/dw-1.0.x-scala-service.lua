config.server = {
    applicationConnectors = {
        {
            type = "http",
            port = 8080
        }
    },
    adminMinThreads = 1,
    adminMaxThreads = 64,
    adminConnectors = {
        {
            type = "http",
            port = 8081
        }
    },
    registerDefaultExceptionMappers = false
}

config.jerseyClient = {
    timeout = "15s",
    connectionTimeout = "5s",
    maxThreads = 500,
    workQueueSize = 40
}