package auth

import "net/http"

func RegisterRoutes(mux *http.ServeMux) {
	mux.HandleFunc("/user/signup", pswdCheck(signup))
	mux.HandleFunc("/user/verify-otp", requireOtp(verifyUser))
}
