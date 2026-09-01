package auth

import (
	"errors"
	"mdcs-server/data/repo"
	"mdcs-server/models"
	"mdcs-server/tools/auth"
	"time"

	"github.com/google/uuid"
	"golang.org/x/crypto/bcrypt"
)

// Generate and store auth tokens
func getAccessTokens(uid string) (string, string, error) {
	auth_tok, err := auth.GenAuthTok(uid)

	if err != nil {
		return "", "", errors.New("TOK_GEN_ERR")
	}

	refresh_tok, err := auth.GenRefreshTok(uid)

	if err != nil {
		return "", "", errors.New("TOK_GEN_ERR")
	}

	return auth_tok, refresh_tok, nil
}

// Create a new user after validating uniqueness and hashing password.
func createUsr(data CreateUsrReq) (string, error) {
	hashed, err := bcrypt.GenerateFromPassword(
		[]byte(data.Password),
		bcrypt.DefaultCost,
	)

	if err != nil {
		return "", errors.New("PASSWORD_HASH_ERROR")
	}

	var usr = models.UserAttrs{
		Username: data.Username,
		Email:    data.Email,
		Password: string(hashed),
		Phase:    "UNVERIFIED",
	}

	if _, _, err := repo.UsrByEmail(data.Email); err == nil {
		return "", errors.New("DUPLICATE_USR")
	}

	usr_id, err := repo.CreateUsr(usr)

	if err != nil {
		return "", errors.New("REGISTRATION_FAILED")
	}

	err = auth.SendOtp(usr_id, data.Email)

	if err != nil {
		return "", errors.New("OTP_VER_FAIL")
	}

	return usr_id, nil
}

/*
Need to implement the tries. A max of 5 tries are allowed before the OTP is erased.
*/
func verifyOtp(data ValidateUsrReq) error {
	otp_hash, err := repo.GetOtp(data.UserId)

	if err != nil {
		return err
	}

	dur := time.Since(otp_hash.Sent)

	if dur.Seconds() > 300 {
		return errors.New("OTP_EXPIRED")
	}

	err = bcrypt.CompareHashAndPassword([]byte(otp_hash.OTP), []byte(data.OTP))

	if err != nil {
		return errors.New("INCORRECT_OTP")
	}

	return repo.SetPhase(data.UserId, "VERIFIED")
}

func firstEnroll(data EnrollReq) (string, string, error) {
	wid := uuid.NewString()
	did := uuid.NewString()

	var workspace = models.WorkspaceAttrs{
		WId:        wid,
		WName:      data.WorkspaceName,
		MainDevice: did,
	}

	var device = models.DeviceAttrs{
		DId:   did,
		DName: data.DeviceName,
	}

	// Check if user exists and is verified.
	if status := repo.GetPhase(data.UserId); status == "NO_SUCH_USER" {
		return "", "", errors.New(status)
	} else if status != "VERIFIED" {
		return "", "", errors.New("FORBIDDEN_ACCESS")
	}

	// Check if workspace with same name is available
	if err, _ := repo.WorkspaceByName(data.UserId, data.WorkspaceName); err == nil {
		return "", "", errors.New("DUPLICATE_WORKSPACE")
	}

	repo.AddWorkspace(data.UserId, workspace)

	// Check if device with same name is available
	if _, err := repo.DeviceByName(wid, data.DeviceName); err == nil {
		/*
			First enroll is atomic. That means, if failed to add device then undo the
			creation of workspace too.
		*/

		repo.DeleteWorkspace(data.UserId, wid)

		return "", "", errors.New("DUPLICATE_DEVICE")
	}

	repo.AddDevice(wid, device)
	repo.SetPhase(data.UserId, "ONBORADED")

	return wid, did, nil
}

func loginVer(data LoginReq) (
	error,
	string,
	models.UserAttrs,
	models.DeviceAttrs,
	models.WorkspaceAttrs,
	string,
) {
	usr_id, usr, err := repo.UsrByEmail(data.Email)

	if err != nil {
		return errors.New("INVALID_CREDS"),
			"",
			models.UserAttrs{},
			models.DeviceAttrs{},
			models.WorkspaceAttrs{},
			""
	}

	err = bcrypt.CompareHashAndPassword([]byte(usr.Password), []byte(data.Password))

	if err != nil {
		return errors.New("INVALID_CREDS"),
			"",
			models.UserAttrs{},
			models.DeviceAttrs{},
			models.WorkspaceAttrs{},
			""
	}

	err, device := repo.DeviceById(data.WorkspaceId, data.DeviceId)

	if err != nil {
		return errors.New("DEVICE_NOT_FOUND"),
			"",
			models.UserAttrs{},
			models.DeviceAttrs{},
			models.WorkspaceAttrs{},
			""
	}

	err, workspace := repo.WorkspaceById(usr_id, data.WorkspaceId)

	if err != nil {
		return errors.New("WORKSPACE_NOT_FOUND"),
			"",
			models.UserAttrs{},
			models.DeviceAttrs{},
			models.WorkspaceAttrs{},
			""
	}

	return nil, usr_id, usr, device, workspace, repo.GetPhase(usr_id)
}

func workspaceDeviceMapUsr(
	email, workspace_id, device_id string,
) error {
	user_id, _, err := repo.UsrByEmail(email)

	if err != nil {
		return errors.New("USER_NOT_FOUND")
	}

	if err, _ := repo.WorkspaceById(user_id, workspace_id); err != nil {
		return err
	}

	if err, _ := repo.DeviceById(workspace_id, device_id); err != nil {
		return err
	}

	return nil
}
