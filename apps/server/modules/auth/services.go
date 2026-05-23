package auth

import (
	"errors"
	"mdcs-server/core/models"
	"mdcs-server/data/repo"

	"golang.org/x/crypto/bcrypt"
)

// Register a new user after validating uniqueness and hashing password.
func registerUser(data SignupRequest) (string, error) {
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

	return user_id, nil
}
