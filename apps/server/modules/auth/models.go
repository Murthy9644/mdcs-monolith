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

type RegisterDeviceReq struct {
	UserId        string `json:"user_id"`
	DeviceName    string `json:"device_name"`
	WorkspaceName string `json:"workspace_name"`
}

type Response struct {
	Status  bool              `json:"status"`
	Body    map[string]string `json:"body"`
	Error   string            `json:"error"`
	Message string            `json:"message"`
}

const (
	SUpDataKey    string = "signup_req_data"
	VerUsrDataKey string = "verifyusr_req_data"
	RegDevDataKey string = "registerdevice_req_data"
)
