-- Have entities: users, workspaces, devices

-- devices have properties:
--  workspace_id    (foreign key)
-- 	device_id		(primary key)
-- 	device_name

-- workspaces is an imaginary container that stores information about devices of
-- a particular users. A users may have multiple workspaces.

CREATE TABLE users(
    user_id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    pswd VARCHAR(50) NOT NULL,
    phase VARCHAR(50) DEFAULT 'UNVERIFIED'
);

CREATE TABLE workspaces(
    user_id VARCHAR(50) NOT NULL,
    workspace_id VARCHAR(50) PRIMARY KEY,
    workspace_name VARCHAR(50) NOT NULL,
    main_device VARCHAR(50) NOT NULL,
);

CREATE TABLE devices(
    workspace_id VARCHAR(50) NOT NULL,
    device_id VARCHAR(50) PRIMARY KEY,
    device_name VARCHAR(50) NOT NULL
);