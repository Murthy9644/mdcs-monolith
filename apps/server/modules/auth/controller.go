package auth

import (
	"encoding/json"
	"mdcs-server/data/repo"
	"net/http"
)

// Write user entry and return user id
func register(res http.ResponseWriter, req *http.Request) {
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

	auth_tok, refresh_tok, err := getAccessTokens(data.UserId)

	if err != nil {
		// This is not error, it will be automatically rectified in next user login
	}

	respld.Body = map[string]string{}
	respld.Body["auth_tok"] = auth_tok
	respld.Body["refresh_tok"] = refresh_tok

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
	data := req.Context().Value(EnrollDeviceDataKey).(EnrollDeviceReq)

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

func login(res http.ResponseWriter, req *http.Request) {
	data := req.Context().Value(LoginDataKey).(LoginReq)

	var respld Response

	err := checkPswd(data)

	if err != nil {
		respld.Status = false
		respld.Body = nil
		respld.Error = err.Error()
		respld.Message = "Invalid email or password"

		payload, err := json.Marshal(respld)

		if err != nil {
			http.Error(res, err.Error(), http.StatusInternalServerError)
			return
		}

		res.Write(payload)
		return
	}

	user_id, usr, err := workspaceDeviceMapUsr(
		data.Email,
		data.WorkspaceId,
		data.DeviceId,
	)

	if err != nil {
		respld.Status = false
		respld.Body = nil
		respld.Error = err.Error()
		respld.Message = "Workspace or device not enrolled"

		payload, err := json.Marshal(respld)

		if err != nil {
			http.Error(res, err.Error(), http.StatusInternalServerError)
			return
		}

		res.Write(payload)
		return
	}

	auth_tok, refresh_tok, err := getAccessTokens(user_id)

	if err != nil {
		// This is not error, it will be automatically rectified in next user login
		// Also, user should be prompted for login next time instead of continuing
	}

	respld.Body = map[string]string{}

	respld.Body["user_id"] = user_id
	respld.Body["username"] = usr.Username
	respld.Body["workspace_id"] = data.WorkspaceId
	respld.Body["device_id"] = data.DeviceId
	respld.Body["auth_tok"] = auth_tok
	respld.Body["refresh_tok"] = refresh_tok

	device, _ := repo.DeviceById(data.WorkspaceId, data.DeviceId)
	workspace, _ := repo.WorkspaceById(user_id, data.WorkspaceId)

	respld.Body["workspace_name"] = workspace.WName
	respld.Body["device_name"] = device.DName

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
