package auth

import (
	"errors"
	"mdcs-server/data/repo"
	"mdcs-server/models"
	"mdcs-server/tools/auth"
	"time"

	"golang.org/x/crypto/bcrypt"
)

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
		Status:   "UNVERIFIED",
	}

	if _, err := repo.UsrByEmail(data.Email); err == nil {
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

	return repo.SetVerified(data.UserId)
}
