package api

import (
	"fmt"
	"mdcs-server/api/auth"
	"mdcs-server/api/version"
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

	// Mount version-check
	var ver_mux *http.ServeMux = http.NewServeMux()
	version.RegisterRoutes(ver_mux)

	mux.Handle("/auth/", http.StripPrefix("/auth", auth_mux))
	mux.Handle("/version/", http.StripPrefix("/version", ver_mux))
}
