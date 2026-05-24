package repo

import (
	"errors"
	"mdcs-server/core/models"
	"mdcs-server/data"
	"time"

	"github.com/google/uuid"
)

// Create new user entry in the users.json
// Returns the user id if entry written or returns error
/*
Right now, there are no possible errors to return but, kept "just in case"
*/
func CreateUser(user models.UserAttrs) (string, error) {
	user_id := uuid.NewString()
	data.Store.Users[user_id] = user

	return user_id, nil
}

// Find the user entry by email
// Returns models.UserAttrs type
func FindUserByEmail(email string) (models.UserAttrs, error) {
	for _, user := range data.Store.Users {

		if user.Email == email {
			return user, nil
		}
	}

	return models.UserAttrs{}, errors.New("no such user found")
}

func StoreOtp(user_id string, otp_det models.OTP) error {
	user, exists := data.Store.Users[user_id]

	if !exists {
		return errors.New("NO_SUCH_USER")
	}

	user.Otp = otp_det
	data.Store.Users[user_id] = user

	return nil
}

func GetOtp(user_id string) (models.OTP, error) {
	user, exists := data.Store.Users[user_id]

	if !exists {
		return models.OTP{}, errors.New("NO_SUCH_USER")
	}

	return user.Otp, nil
}

func SetVerified(user_id string) error {
	user := data.Store.Users[user_id]
	user.Otp = models.OTP{}
	user.Verified = true

	data.Store.Users[user_id] = user

	return nil
}

func StoreRefreshToken(user_id, token string) error {
	user := data.Store.Users[user_id]
	user.AccessToken.RefreshToken = token
	user.AccessToken.CreatedAt = time.Now()

	data.Store.Users[user_id] = user

	return nil
}
