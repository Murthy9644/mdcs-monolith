package main

// Main Server for MDCS

import (
	"fmt"
	"mdcs-server/core/bootstrap"
	"mdcs-server/modules"
	"net/http"
)

func main() {
	if !bootstrap.BootstrapHandler() {
		return
	}

	mux := http.NewServeMux()
	modules.Router(mux)

	mux_final := http.StripPrefix("/mdcs", mux)

	fmt.Println("\nServer listening at :1097")
	http.ListenAndServe("0.0.0.0:1097", mux_final)
}
