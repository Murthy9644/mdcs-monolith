package models

// Have entities: user, workspace, device

/*
user have properties:
		user_id			(primary key)
		username
		email
		password
		verified

workspace is an imaginary container that stores the information about
devices of a particular user.
		workspace_id	(primary key)
		workspace_name
		main_device

A user may have multiple workspaces, but later in the application,
may assume single workspace for simplicity in v1.

device have properties:
		device_id		(primary key)
		device_name
*/

type UserAttrs struct {
	Username string `json:"username"`
	Email    string `json:"email"`
	Password string `json:"password"`
	Verified bool   `json:"verified"`
}

// string(user_id) -> user attributes
type Users map[string]UserAttrs

type WorkspaceAttrs struct {`
	WorkspaceId   int    `json:"workspace_id"`
	WorkspaceName string `json:"workspace_name"`
	MainDevice    int    `json:"main_device"`
}

// string(user_id) -> array of workspaces
type Workspaces map[string][]WorkspaceAttrs

type DeviceAttrs struct {
	DeviceId    int    `json:"device_id"`
	DeviceName  string `json:"device_name"`
}

// string(workspace_id) -> array of devices
type Devices map[string][]DeviceAttrs
