# Application Data Store

## Overview
This directory contains all locally persisted application data, including user information, device metadata, configuration settings, and application properties. It is used during the bootstrap process to restore application state and determine runtime behavior.

## Directory Locations
- Microsoft Windows:
```
%APPDATA%/MDCS/
```

- Linux:
```
%XDG_CONFIG_HOME/MDCS/
    or
~/.config/MDCS/
```

- MAC
```
~/Library/Application Support/MDCS/
```

## Folder Structure
```
MDCS/
    entities/
        Accounts.json
        Device.json
    application/
        Configs.json
        ModulePaths.json
        Data.json
    logs/
        App.log
        Error.log
        Network.log
```

## Files
> Relative to base folder (MDCS/)

### 1. Accounts.json
Stores authenticated user information and session state

**Fields**
- user_id
- user_name
- email
- auth_token
- login_status

### 2. Device.json
Stores device-level and workspace-related metadata

**Fields**
- device_id
- device_name
- workspace_id
- workspace_name
- device_status

### 3. Configs.json
Stores application configuration and user preferences

**Fields**
- application settings

### 4. ModulePaths.json
Stores absolute location of all modules

**Fields**
- clipboard
- file_share
- folder_sync
- protocols
- application_acess
- scheduler
- host
- client
- mesh

### 5. Data.json
Stores user specific information related to the device

**Fields**
- custom_paths
- Will be updated later as required.

### 6. App.log
Logs general info and high-level flow

### 7. Error.log
Logs exceptions, crashes and failed operations

### 8. Network.log
Logs about requests/responses, API calls, socket communications

### 9. application.properties (bundled with app)
Stores static application metadata and versioning information

**Fields**
- app.name
- server.host
- server.port
- other metadata

### 10. versions.properties (bundled with app)
Stores versioning of application and modules

**Fields**
- app.version
- app.cli.version
- app.gui.version
- module.clipboard.version
- module.file_share.version
- module.folder_sync.version
- module.protocols.version
- module.application_access.version
- module.scheduler.version
- module.host.version
- module.client.version
- module.mesh.version
- logger.version
- schema.version

## Logs File Structure
```
[time] [level] [module] message
```

## Usage in Bootstrap Process

[App Bootstrap Process](/docs/system_design/app-bootstrap.md)

---