package auth

import (
	"encoding/json"
	"fmt"
	"mdcs-server/tools/auth"
	"net/http"
)

// Write user entry and return user id
func signup(res http.ResponseWriter, req *http.Request) {
	data := req.Context().Value(SUpDataKey).(CreateUsrReq)
	var respld Response

	user_id, err := createUsr(data)

	if err != nil {
		respld.Status = false
		respld.Body = nil
		respld.Error = err.Error()
		respld.Message = "Account creation failed"

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
	respld.Message = "Account created successfully"

	payload, err := json.Marshal(respld)

	if err != nil {
		http.Error(res, err.Error(), http.StatusInternalServerError)
		return
	}

	res.Write(payload)
}

func verifyUsr(res http.ResponseWriter, req *http.Request) {
	data := req.Context().Value(VerUsrDataKey).(ValidateUsrReq)

	var respld Response

	err := verifyOtp(data)

	if err != nil {
		respld.Status = false
		respld.Body = nil
		respld.Error = err.Error()
		respld.Message = "OTP verification failed"

		payload, err := json.Marshal(respld)

		if err != nil {
			http.Error(res, err.Error(), http.StatusInternalServerError)
			return
		}

		res.Write(payload)
		return
	}

	respld.Body = map[string]string{}
	respld.Body["auth_token"], err = auth.GenAuthTok(data.UserId)

	if err != nil {
		fmt.Println(err)

		respld.Status = false
		respld.Body = nil
		respld.Error = "REGISTRATION_FAILED"
		respld.Message = "Validation failed"

		payload, err := json.Marshal(respld)

		if err != nil {
			http.Error(res, err.Error(), http.StatusInternalServerError)
			return
		}

		res.Write(payload)
		return
	}

	respld.Body["refresh_token"], _ = auth.GenRefreshTok(data.UserId)

	respld.Status = true
	respld.Error = ""
	respld.Message = "Validation successful"

	payload, err := json.Marshal(respld)

	if err != nil {
		http.Error(res, err.Error(), http.StatusInternalServerError)
		return
	}

	res.Write(payload)
}

func handleFirstEnroll(res http.ResponseWriter, req *http.Request) {
	data := req.Context().Value(RegDevDataKey).(RegisterDeviceReq)

	var respld Response

	wid, did, err := firstEnroll(data)

	if err != nil {
		respld.Status = false
		respld.Body = nil
		respld.Error = err.Error()
		respld.Message = "Device enrollment failed"

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
	respld.Body["device_id"] = did
	respld.Body["device_name"] = data.DeviceName
	respld.Body["workspace_id"] = wid
	respld.Body["workspace_name"] = data.WorkspaceName

	respld.Error = ""
	respld.Message = "Device enrolled successfully"

	payload, err := json.Marshal(respld)

	if err != nil {
		http.Error(res, err.Error(), http.StatusInternalServerError)
		return
	}

	res.Write(payload)
}
