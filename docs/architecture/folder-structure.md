# MDCS Folder Structure
`Changes are possible as the project evolves`
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
│   │   └── state-store_files.md
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