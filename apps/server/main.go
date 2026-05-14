package main

import (
	"fmt"
	"mdcs-server/api"
	"mdcs-server/core/bootstrap"
	"net/http"
)

func main() {
	if !bootstrap.BootstrapHandler() {
		return
	}

	mux := http.NewServeMux()

	// Register all routes
	api.Router(mux)

	// Stripping /mdcs (base of url)
	mux_final := http.StripPrefix("/mdcs", mux)
	fmt.Println("Server listening at :1097")
	http.ListenAndServe("0.0.0.0:1097", mux_final)
}
