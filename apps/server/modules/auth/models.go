package auth

type SignupRequest struct {
	Username string `json:"username"`
	Email    string `json:"email"`
	Password string `json:"password"`
}

type SignupResponse struct {
	Status  bool              `json:"status"`
	Body    map[string]string `json:"body"`
	Error   string            `json:"error"`
	Message string            `json:"message"`
}

const (
	SUpDataKey string = "signup_req_data"
)
