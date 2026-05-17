# MDCS Folder Structure
`Changes are possible as the project evolves`
```
mdcs-monolith/
    apps/
        cli/
            pom.xml
            src/main/
                java/cli/
                    helpers/
                        BootstrapTerminal.java
                    utils/
                        command_utils/
                            AuthPipe.java
                            Interface.java
                        tools/
                            Colors.java
                            ConfigLoader.java
                            ConsoleIO.java
                        CLIHandler.java
                    Main.java # (Console app entry point)
                    App.java
                resources/
                    application.properties
                    versions.properties
                    
        server/
            api/
                auth/
                    controller.go
                    middleware.go
                    routes.go
                shared/
                    error.go
                    request.go
                    response.go
                    tools.go
                version/
                    controller.go
                    models.go
                    routes.go
                    services.go
                router.go
            core/
                bootstrap/
                    BootstrapHandler.go
                    LoadEnv.go
                    Models.go
                    VersionMetadata.go
            # .env
            go.mod
            go.sum
            main.go

    core/
        src/main/java/
            bootstrap/
                BootstrapHandler.java
                SchemaValidation.java
                UserStateResolution.java
                VersionCheck.java
            user_auth/
                UserAuth.java
            device_auth/
                DeviceAuth.java
        pom.xml
        
    shared/
        src/main/java/
            file_io/
                DataClasses.java
                FileIO.java
            logger/
                Log.java
            utils/
                SystemUtils.java
            response_classes/
                BootstrapResponse.java
                ServerResponseClasses.java
            network/
                ServerRequest.java
        pom.xml
        
    docs/
        architecture/
            folder-structure.md
            state-store_files.md
        system_design/
            app-bootstrap.md
            auth-system.md
        tests/
            Bootstrap.md
        build-commands.md

    assets/
        images/
            user_level_viz.png

    .gitignore
    build.py
    pom.xml
    README.md
```
---