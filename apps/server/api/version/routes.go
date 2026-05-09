package version

import "net/http"

func RegisterRoutes(mux *http.ServeMux) {

	// Version check handler
	mux.HandleFunc("/check", versionCheck)
}
