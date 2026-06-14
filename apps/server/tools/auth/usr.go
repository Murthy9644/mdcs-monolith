package auth

import (
	"crypto/rand"
	"encoding/base64"
	"fmt"
	"math/big"
	"mdcs-server/data/repo"
	"mdcs-server/models"
	"mdcs-server/tools"
	"os"
	"time"

	"github.com/golang-jwt/jwt/v5"
	"golang.org/x/crypto/bcrypt"
)

func SendOtp(user_id, email string) error {
	n, err := rand.Int(rand.Reader, big.NewInt(900000))

	if err != nil {
		return err
	}

	otp := fmt.Sprintf("%06d", n.Int64()+100000)

	otp_hash, err := bcrypt.GenerateFromPassword(
		[]byte(otp),
		bcrypt.DefaultCost,
	)

	msg := fmt.Sprintf(
		"From: %s\r\n"+
			"To: %s\r\n"+
			"Subject: MDCS Verification\r\n"+
			"MIME-version: 1.0;\r\n"+
			"Content-Type: text/html; charset=\"UTF-8\";\r\n\r\n"+

			`
		<html>
			<body>
				<h2>MDCS Verification</h2>

				<p>Hi! Thanks for downloading MDCS</p>

				<p>
					If you have any queries or feedback, feel free to reply to this
					email.
				</p>
				
				<p>Your OTP is:</p>

				<h1>%s</h1>

				<p>This code expires in 5 minutes.</p>
			</body>
		</html>
		`,
		os.Getenv("EMAIL"), email, otp,
	)

	err = tools.SendEmail(email, []byte(msg))

	if err != nil {
		return err
	}

	err = repo.StoreOtp(
		user_id,
		models.OTP{OTP: string(otp_hash), Sent: time.Now()},
	)

	return err
}

func GenAuthTok(user_id string) (string, error) {
	claims := jwt.MapClaims{
		"user_id": user_id,
		"exp":     time.Now().Add(60 * time.Minute).Unix(),
		"iat":     time.Now().Unix(),
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)

	return token.SignedString([]byte(os.Getenv("JWT_KEY")))
}

func GenRefreshTok(user_id string) (string, error) {
	key := make([]byte, 32)
	_, err := rand.Read(key)

	if err != nil {
		// Ususally error will be null
		return "", err
	}

	token := base64.URLEncoding.EncodeToString(key)
	hashed, err := bcrypt.GenerateFromPassword(
		[]byte(token),
		bcrypt.DefaultCost,
	)

	err = repo.StoreRefreshTok(user_id, string(hashed))

	return token, err
}
