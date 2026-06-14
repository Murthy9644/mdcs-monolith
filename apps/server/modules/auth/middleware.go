package auth

import (
	"context"
	"encoding/json"
	"net/http"
	"regexp"
)

// check for strenght of password.
/*
conditions for password strength:
password must contain atleast one
	- uppercase
	- lowercase
	- digit
	- sp. char
password must be atleast 6 chars long
*/
// Check for email structure: example@email.com
func checkPswd(next http.HandlerFunc) http.HandlerFunc {
	var tests = map[*regexp.Regexp]string{
		regexp.MustCompile(`[A-Z]+`):       "MISSING_UPPERCASE",
		regexp.MustCompile(`[a-z]+`):       "MISSING_LOWERCASE",
		regexp.MustCompile(`\d+`):          "MISSING_DIGIT",
		regexp.MustCompile(`[!@#$%^&*_]+`): "MISSING_SPECIAL",
	}

	emailtest := regexp.MustCompile(`^[a-zA-Z\d._%+-]+@(([a-z]+\.)[a-z]+)$`)

	return func(res http.ResponseWriter, req *http.Request) {
		var data CreateUsrReq
		err := json.NewDecoder(req.Body).Decode(&data)
		defer req.Body.Close()

		if err != nil {
			payload, err := json.Marshal(
				Response{
					Status:  false,
					Body:    nil,
					Error:   "INVALID_DATA",
					Message: "Failed to parse data",
				})

			if err != nil {
				http.Error(res, err.Error(), http.StatusInternalServerError)
				return
			}

			res.Write(payload)
			return
		}

		if len(data.Password) < 6 {
			respld := Response{
				Status:  false,
				Body:    nil,
				Error:   "SHORT_PASSWORD",
				Message: "Weak password",
			}

			payload, err := json.Marshal(respld)
			if err != nil {
				http.Error(res, err.Error(), http.StatusInternalServerError)
				return
			}

			res.Write(payload)
			return
		}

		for test, err := range tests {
			if !test.MatchString(data.Password) {
				respld := Response{
					Status:  false,
					Body:    nil,
					Error:   err,
					Message: "Weak password",
				}

				payload, err := json.Marshal(respld)
				if err != nil {
					http.Error(res, err.Error(), http.StatusInternalServerError)
					return
				}

				res.Write(payload)
				return
			}
		}

		if !emailtest.MatchString(data.Email) {
			respld := Response{
				Status:  false,
				Body:    nil,
				Error:   "INVALID_EMAIL",
				Message: "Invalid email id",
			}

			payload, err := json.Marshal(respld)
			if err != nil {
				http.Error(res, err.Error(), http.StatusInternalServerError)
				return
			}

			res.Write(payload)
			return
		}

		con := context.WithValue(
			req.Context(),
			SUpDataKey, data,
		)

		next(res, req.WithContext(con))
	}
}

func requireOtp(next http.HandlerFunc) http.HandlerFunc {

	return func(res http.ResponseWriter, req *http.Request) {
		var data ValidateUsrReq

		err := json.NewDecoder(req.Body).Decode(&data)

		defer req.Body.Close()

		if err != nil || data.UserId == "" || data.Email == "" || data.OTP == "" {
			payload, err := json.Marshal(
				Response{
					Status:  false,
					Body:    nil,
					Error:   "INVALID_DATA",
					Message: "Failed to parse data",
				})

			if err != nil {
				http.Error(res, err.Error(), http.StatusInternalServerError)
				return
			}

			res.Write(payload)
			return
		}

		con := context.WithValue(
			req.Context(),
			VerAccDataKey, data,
		)

		next(res, req.WithContext(con))
	}
}
