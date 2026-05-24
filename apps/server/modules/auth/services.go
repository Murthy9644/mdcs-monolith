package auth

import (
	"errors"
	"mdcs-server/core/models"
	"mdcs-server/data/repo"
	"mdcs-server/tools/auth"
	"time"

	"golang.org/x/crypto/bcrypt"
)

// Register a new user after validating uniqueness and hashing password.
func registerUser(data SignupReq) (string, error) {
	hashed, err := bcrypt.GenerateFromPassword(
		[]byte(data.Password),
		bcrypt.DefaultCost,
	)

	if err != nil {
		return "", errors.New("PASSWORD_HASH_ERROR")
	}

	var user = models.UserAttrs{
		Username: data.Username,
		Email:    data.Email,
		Password: string(hashed),
	}

	if _, err := repo.FindUserByEmail(data.Email); err == nil {
		return "", errors.New("DUPLICATE_USER")
	}

	user_id, err := repo.CreateUser(user)

	if err != nil {
		return "", errors.New("REGISTRATION_FAILED")
	}

	err = auth.SendOtp(user_id, data.Email)

	if err != nil {
		return "", errors.New("OTP_VER_FAIL")
	}

	return user_id, nil
}

func verifyOtp(data VerifyAccReq) error {
	otp_hash, err := repo.GetOtp(data.UserId)

	if err != nil {
		return err
	}

	dur := time.Since(otp_hash.SentAt)

	if dur.Seconds() > 300 {
		return errors.New("OTP_EXPIRED")
	}

	err = bcrypt.CompareHashAndPassword([]byte(otp_hash.OTP), []byte(data.OTP))

	if err != nil {
		return errors.New("INCORRECT_OTP")
	}

	return repo.SetVerified(data.UserId)
}
