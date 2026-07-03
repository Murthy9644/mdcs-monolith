package auth

type CreateUsrReq struct {
	Username string `json:"username"`
	Email    string `json:"email"`
	Password string `json:"password"`
}

type ValidateUsrReq struct {
	UserId string `json:"user_id"`
	Email  string `json:"email"`
	OTP    string `json:"otp"`
}

type EnrollDeviceReq struct {
	UserId        string `json:"user_id"`
	DeviceName    string `json:"device_name"`
	WorkspaceName string `json:"workspace_name"`
}

type LoginReq struct {
	Email       string `json:"email"`
	Password    string `json:"password"`
	WorkspaceId string `json:"workspace_id"`
	DeviceId    string `json:"device_id"`
}

const (
	SUpDataKey          string = "signup_req_data"
	VerUsrDataKey       string = "verifyusr_req_data"
	EnrollDeviceDataKey string = "enrolldevice_req_data"
	LoginDataKey        string = "login_req_data"
)
