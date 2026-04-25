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

## Files
### 1. user.json
Stores authenticated user information and session state

**Fields**
- user_id
- user_name
- email
- auth_token
- login_status

### 2. device.json
Stores device-level and workspace-related metadata

**Fields**
- device_id
- device_name
- workspace_id
- workspace_name
- device_status

### 3. configs.json
Stores application configuration and user preferences

**Fields**
- server_url
- theme
- other application settings

### 4. app.properties
Stores static application metadata and versioning information

**Fields**
- app.name
- app.version
- app.cli_version
- app.gui_version
- other metadata

## Usage in Bootstrap Process

During application startup, the system:

1. Loads configuration from configs.json
2. Reads user.json to determine authentication state
3. Loads device.json for environment context
4. Reads app.properties for version and compatibility checks
5. Determines whether this is a first-time run or a returning session

---