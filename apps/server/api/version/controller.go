package version

import (
	"fmt"
	"net/http"
)

func versionCheck(res http.ResponseWriter, req *http.Request) {
	fmt.Print(req.Body)

	fmt.Fprintf(res, "{status: true}")
}
