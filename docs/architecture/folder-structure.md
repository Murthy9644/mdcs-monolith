# MDCS Folder Structure
`Changes are possible as the project evolves`
```
mdcs-monolith/
    apps/
        cli/
            pom.xml
            src/main/java/cli/
                cli_utils/
                    CLIHandler.java
                    Colors.java
                    IO.java
                Main.java # (Console app entry point)
                App.java
        gui/
            pom.xml
            src/main/java/gui/
                Main.java # (GUI app entry point)
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
                router.go
            configs/
                configs.go
            core/
                auth/
                    services.go
                    validation.go
            storage/
                db.go
            go.mod
            main.go

    core/
        src/main/java/
            user_auth/
                UserAuth.java
            device_auth/
                DeviceAuth.java
        pom.xml

    services/
        clipboard/
        file_share/
        folder_sync/
        protocols/
        application_access/
        scheduler/
        host/
        client/
        mesh/
        
    shared/
        src/main/java/
            logger/
            file_io/
            utils/
            configs/
            network/
        pom.xml

    assets/
```
---