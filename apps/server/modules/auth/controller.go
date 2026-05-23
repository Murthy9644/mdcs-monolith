package auth

import (
	"encoding/json"
	"net/http"
)

// Write user entry and return user id
func signup(res http.ResponseWriter, req *http.Request) {
	data := req.Context().Value(SUpDataKey).(SignupRequest)
	var respld SignupResponse

	user_id, err := registerUser(data)

	if err != nil {
		respld.Status = false
		respld.Body = nil
		respld.Error = err.Error()
		respld.Message = "User registration failed"

		payload, err := json.Marshal(respld)

		if err != nil {
			http.Error(res, err.Error(), http.StatusInternalServerError)
			return
		}

		res.Write(payload)
		return
	}

	respld.Status = true

	respld.Body = map[string]string{}
	respld.Body["user_id"] = user_id
	respld.Body["username"] = data.Username
	respld.Body["email"] = data.Email

	respld.Error = ""
	respld.Message = "User registered successfully"

	payload, err := json.Marshal(respld)

	if err != nil {
		http.Error(res, err.Error(), http.StatusInternalServerError)
		return
	}

	res.Write(payload)
}
