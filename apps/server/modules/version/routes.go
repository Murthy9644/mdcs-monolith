package version

import "net/http"

func RegisterRoutes(mux *http.ServeMux) {
	mux.HandleFunc("/check", versionCheck)
}
