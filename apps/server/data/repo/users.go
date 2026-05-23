package repo

import (
	"errors"
	"mdcs-server/core/models"
	"mdcs-server/data"

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
