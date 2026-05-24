package models

import "time"

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

type OTP struct {
	OTP    string    `json:"otp"`
	SentAt time.Time `json:"sent_at"`
}

type AccessToken struct {
	RefreshToken string    `json:"refresh_token"`
	CreatedAt    time.Time `json:"created_at"`
}

type UserAttrs struct {
	Username    string      `json:"username"`
	Email       string      `json:"email"`
	Password    string      `json:"password"`
	Otp         OTP         `json:"verification"`
	AccessToken AccessToken `json:"token"`
	Verified    bool        `json:"verified"`
}

// string(user_id) -> user attributes
type Users map[string]UserAttrs

type WorkspaceAttrs struct {
	WorkspaceId   int    `json:"workspace_id"`
	WorkspaceName string `json:"workspace_name"`
	MainDevice    int    `json:"main_device"`
}

// string(user_id) -> array of workspaces
type Workspaces map[string][]WorkspaceAttrs

type DeviceAttrs struct {
	DeviceId   int    `json:"device_id"`
	DeviceName string `json:"device_name"`
}

// string(workspace_id) -> array of devices
type Devices map[string][]DeviceAttrs
