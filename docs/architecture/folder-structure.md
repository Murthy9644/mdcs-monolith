# MDCS Folder Structure
> Changes are possible as the project evolves

---

```
.
├── apps
│   └── core
│       ├── pom.xml
│       └── src/main/java/com/mdcs/core
│           ├── App.java
│           ├── Main.java
│           ├── Stream.java
│           ├── auth
│           │   ├── Enroll.java
│           │   ├── Login.java
│           │   └── Register.java
│           └── bootstrap
│               ├── Schema.java
│               ├── Supervise.java
│               ├── UserState.java
│               └── Version.java
│
├── build.py
├── docs
│   ├── architecture
│   │   ├── design.md
│   │   ├── folder-structure.md
│   │   └── overview.md
│   ├── assets/images
│   │   └── user_level_viz.png
│   ├── development
│   │   └── build-commands.md
│   ├── modules
│   │   ├── feature.md
│   │   └── internal.md
│   ├── overview
│   │   ├── features.md
│   │   └── introduction.md
│   ├── system-design
│   │   ├── app-bootstrap.md
│   │   ├── auth-system.md
│   │   └── state-store.md
│   └── tests
│       └── bootstrap.md
│
├── pom.xml
├── README.md
│
├── server
│   ├── core/bootstrap
│   │   ├── env.go
│   │   ├── handler.go
│   │   ├── metadata.go
│   │   └── schema.go
│   ├── data
│   │   ├── ddl.sql
│   │   ├── init.go
│   │   └── repo
│   │       ├── devices.go
│   │       ├── users.go
│   │       └── workspaces.go
│   ├── go.mod
│   ├── go.sum
│   ├── main.go
│   ├── models
│   │   ├── metadata.go
│   │   └── schemas.go
│   ├── modules
│   │   ├── auth
│   │   │   ├── controller.go
│   │   │   ├── middleware.go
│   │   │   ├── models.go
│   │   │   ├── routes.go
│   │   │   └── services.go
│   │   ├── router.go
│   │   ├── shared
│   │   │   ├── models.go
│   │   │   └── util.go
│   │   └── version
│   │       ├── controller.go
│   │       ├── models.go
│   │       ├── routes.go
│   │       └── services.go
│   └── tools
│       ├── auth
│       │   └── usr.go
│       ├── mail.go
│       └── version
│           └── version.go
│
└── shared
    ├── pom.xml
    └── src/main/java/com/mdcs/shared
        ├── archive/postals
        │   ├── Envelope.java
        │   ├── Jobs.java
        │   ├── Report.java
        │   └── State.java
        ├── fileio
        │   ├── DataClasses.java
        │   └── FileIO.java
        ├── logger
        │   └── Log.java
        ├── models
        │   ├── auth
        │   │   ├── Network.java
        │   │   └── Provider.java
        │   ├── bootstrap
        │   │   └── Network.java
        │   ├── network
        │   │   └── Http.java
        │   └── Report.java
        ├── network
        │   └── ProtoMet.java
        ├── security
        │   ├── KeyManager.java
        │   └── TokCipher.java
        └── utils
            ├── NetErrors.java
            └── SystemUtils.java
```

---

`@deprecated`
```
mdcs-desktop/
├── apps/
│   ├── cli/
│   │   ├── pom.xml
│   │   └── src/
│   │       └── main/
│   │           ├── resources/
│   │           │   ├── application.properties
│   │           │   └── versions.properties
│   │           └── java/
│   │               └── cli/
│   │                   ├── utils/
│   │                   │   ├── Colors.java
│   │                   │   ├── CLI.java
│   │                   │   ├── Config.java
│   │                   │   └── ConsoleIO.java
│   │                   ├── interact/
│   │                   │   ├── auth/
│   │                   │   │   ├── CLIProvider.java
│   │                   │   │   └── Onboard.java
│   │                   │   ├── Bootstrap.java
│   │                   │   └── Interface.java
│   │                   ├── App.java
│   │                   └── Main.java
│   │
│   └── server/
│       ├── modules/
│       │   ├── auth/
│       │   │   ├── controller.go
│       │   │   ├── middleware.go
│       │   │   ├── models.go
│       │   │   ├── routes.go
│       │   │   └── services.go
│       │   ├── shared/
│       │   │   ├── models.go
│       │   │   └── util.go
│       │   ├── version/
│       │   │   ├── controller.go
│       │   │   ├── models.go
│       │   │   ├── routes.go
│       │   │   └── services.go
│       │   └── router.go
│       │
│       ├── core/
│       │   └── bootstrap/
│       │       ├── env.go
│       │       ├── handler.go
│       │       ├── metadata.go
│       │       └── schema.go
│       │
│       ├── data/
│       │   ├── repo/
│       │   │   ├── devices.go
│       │   │   ├── users.go
│       │   │   └── workspaces.go
│       │   ├── ddl.sql
│       │   └── init.go
│       │
│       ├── models/
│       │   ├── metadata.go
│       │   └── schemas.go
│       │
│       ├── tools/
│       │   ├── auth/
│       │   │   └── usr.go
│       │   ├── version/
│       │   │   └── version.go
│       │   └── mail.go
│       │
│       ├── .env
│       ├── go.mod
│       ├── go.sum
│       └── main.go
│
├── core/
│   ├── pom.xml
│   └── src/
│       └── main/
│           └── java/
│               ├── auth/
│               │   ├── Enroll.java
│               │   ├── Login.java
│               │   └── Register.java
│               └── bootstrap/
│                   ├── Schema.java
│                   ├── Supervise.java
│                   ├── UserState.java
│                   └── Version.java
│
├── shared/
│   ├── pom.xml
│   └── src/
│       └── main/
│           └── java/
│               ├── file_io/
│               │   ├── DataClasses.java
│               │   └── FileIO.java
│               ├── logger/
│               │   └── Log.java
|               ├── models/
|               |   ├── auth/
|               |   |   ├── Network.java
|               |   |   ├── Provider.java
|               |   |   ├── State.java
|               |   ├── bootstrap/
|               |   |   ├── Jobs.java
|               |   |   ├── Network.java
|               |   ├── network/
|               |   |   ├── Http.java
|               |   └── postals/
|               |       ├── Envelop.java
|               |       ├── Report.java
│               ├── network/
│               │   └── ProtoMet.java
│               ├── security/
│               │   ├── KeyManager.java
│               │   └── TokCipher.java
│               └── utils/
|                   ├── NetErrors.java
│                   └── SystemUtils.java
│
├── docs/
│   ├── architecture/
│   │   ├── design.md
│   │   ├── folder-structure.md
│   │   ├── overview.md
│   ├── development/
│   │   └── build-commands.md
│   ├── modules/
│   │   ├── feature.md
│   │   └── internal.md
│   ├── overview/
│   │   ├── features.md
│   │   └── introduction.md
│   ├── system_design/
│   │   ├── app-bootstrap.md
│   │   └── auth-system.md
│   │   └── state-store.md
│   └── tests/
│       └── bootstrap.md
│
├── assets/
│   └── images/
│       └── user_level_viz.png
│
├── .gitignore
├── build.py
├── pom.xml
└── README.md
```
---
