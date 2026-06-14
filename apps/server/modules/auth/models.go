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

type Response struct {
	Status  bool              `json:"status"`
	Body    map[string]string `json:"body"`
	Error   string            `json:"error"`
	Message string            `json:"message"`
}

const (
	SUpDataKey    string = "signup_req_data"
	VerAccDataKey string = "verifyacc_req_data"
)
