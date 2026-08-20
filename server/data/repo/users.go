package repo

import (
	"errors"
	"mdcs-server/data"
	"mdcs-server/models"
	"time"

	"github.com/google/uuid"
)

/*
Create new user entry in the users.json
Returns the user id if entry written or returns error

Right now, there are no possible errors to return but, kept "just in case"
*/
func CreateUsr(usr models.UserAttrs) (string, error) {
	usr_id := uuid.NewString()
	data.Store.Users[usr_id] = usr

	return usr_id, nil
}

func UsrByEmail(email string) (string, models.UserAttrs, error) {

	for user_id, user := range data.Store.Users {

		if user.Email == email {
			return user_id, user, nil
		}
	}

	return "", models.UserAttrs{}, errors.New("NO_SUCH_USER")
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

	if !exists || user.Phase != "UNVERIFIED" {
		return models.OTP{}, errors.New("NO_SUCH_USER")
	}

	return user.Otp, nil
}

func SetPhase(user_id, phase string) error {
	user := data.Store.Users[user_id]
	user.Otp = models.OTP{}
	user.Phase = phase

	data.Store.Users[user_id] = user

	return nil
}

func GetPhase(uid string) string {
	usr, exists := data.Store.Users[uid]

	if !exists {
		return "NO_SUCH_USER"
	}

	return usr.Phase
}

func StoreRefreshTok(user_id, token string) error {
	user := data.Store.Users[user_id]
	user.RefreshTok.Token = token
	user.RefreshTok.Created = time.Now()

	data.Store.Users[user_id] = user

	return nil
}
