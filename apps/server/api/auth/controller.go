package auth

import (
	"encoding/json"
	"fmt"
	"net/http"
)

type SignupRequest struct {
	Username string `json:"username"`
	Email    string `json:"email"`
	Password string `json:"password"`
}

func SignupHandler(res http.ResponseWriter, req *http.Request) {
	var new_req SignupRequest

	err := json.NewDecoder(req.Body).Decode(&new_req)
	if err != nil {
		http.Error(res, "Invalid body", http.StatusBadRequest)
	}

	fmt.Fprintf(res, "signup req received\n")
}
