package api

import (
	"fmt"
	"mdcs-server/api/auth"
	"net/http"
)

func Router(mux *http.ServeMux) {

	// ping api
	mux.HandleFunc("/ping", func(res http.ResponseWriter, req *http.Request) {
		fmt.Fprintf(res, "Hello, there!\n")
	})

	// Mount auth (sub-router) at /auth
	var auth_mux *http.ServeMux = http.NewServeMux()
	auth.RegisterRoutes(auth_mux)

	mux.Handle("/auth/", http.StripPrefix("/auth", auth_mux))
}
