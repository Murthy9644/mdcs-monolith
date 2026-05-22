package auth

import "net/http"

func RegisterRoutes(mux *http.ServeMux) {

	// Signup handler
	mux.HandleFunc("/signup", SignupHandler)
}
