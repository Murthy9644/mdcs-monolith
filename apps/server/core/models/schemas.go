package models

// Have entities: user, workspace, devices

/*
user have properties:
		user_id			(primary key)
		username
		email
		password
		verified

workspace is an imaginary container that stores the information about
devices of a particular user.
		user_id 		(foreign key -> user)
		workspace_id	(primary key)
		workspace_name
		main_device

A user may have multiple workspaces, but later in the application,
may assume single workspace for simplicity in v1.

devices have properties:
		workspace_id	(foreign_key -> workspace)
		device_id		(primary key)
		device_name
*/

// work (schema structure) in progress

type UserAttrs struct {
	Username string `json:"username"`
	Email    string `json:"email"`
	Password string `json:"password"`
	Verified bool   `json:"verified"`
}

// string(user_id) -> user attributes
type User map[string]UserAttrs

type WorkspaceAttrs struct {
	UserId        int    `json:"user_id"`
	WorkspaceId   int    `json:"workspace_id"`
	WorkspaceName string `json:"workspace_name"`
	MainDevice    int    `json:"main_device"`
}

// string(user_id) -> array of workspace attrs
type Workspace map[string]WorkspaceAttrs

type Devices struct {
	WorkspaceId int    `json:"workspace_id"`
	DeviceId    int    `json:"device_id"`
	DeviceName  string `json:"device_name"`
}

// How to access data items?
// :
/*
If want to access a user tuple, must know the user_id (PK)

If have the user_id, can access the user workspaces by using it as
foreign key.
*/
