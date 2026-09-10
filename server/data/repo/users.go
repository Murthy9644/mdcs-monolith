package repo

import (
	"context"
	"errors"
	"mdcs-server/data"
	"mdcs-server/models"
	"time"

	"github.com/google/uuid"
)

func CreateUsr(usr models.UserAttrs) (string, error) {
	usr_id := uuid.NewString()

	_, err := data.Pool.Exec(
		context.Background(),
		`
		INSERT INTO users
			(id, email, password, phase)			
		VALUES ($1, $2, $3, $4)
		`,
		usr_id,
		usr.Email,
		usr.Password,
		usr.Phase,
	)

	if err != nil {
		return "", err
	}

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
