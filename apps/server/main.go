package main

import (
	"fmt"
	"mdcs-server/api"
	"net/http"
)

func main() {
	mux := http.NewServeMux()

	// Register all routes
	api.Router(mux)

	// Stripping /mdcs (base of url)
	mux_final := http.StripPrefix("/mdcs", mux)

	fmt.Println("Server is listening to :1097")
	http.ListenAndServe("0.0.0.0:1097", mux_final)
}
