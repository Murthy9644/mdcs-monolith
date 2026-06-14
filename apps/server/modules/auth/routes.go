package auth

import "net/http"

func Routes(mux *http.ServeMux) {
	mux.HandleFunc("/user/signup", checkPswd(signup))
	mux.HandleFunc("/user/verify-otp", requireOtp(verifyUsr))
}
