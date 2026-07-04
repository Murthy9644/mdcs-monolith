package bootstrap

import (
	"encoding/json"
	"fmt"
	"io"
	"mdcs-server/models"
	"net/http"
	"os"
	"time"
)

// The metadata this function returns is necessary.
// Server startup must fail if metadata server is unreachable
func metadata() (*models.VerData, error) {
	url := os.Getenv("VERSION_DATA_URL")

	if url == "" {
		return nil, fmt.Errorf("error: VERSION_DATA_URL not found")
	}

	client := http.Client{
		Timeout: 10 * time.Second,
	}

	res, err := client.Get(url)

	if err != nil {
		return nil, err
	}

	defer res.Body.Close()

	if res.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("error: Bad status code: %d", res.StatusCode)
	}

	data, err := io.ReadAll(res.Body)

	if err != nil {
		return nil, err
	}

	var metadata models.VerData
	err = json.Unmarshal(data, &metadata)

	if err != nil {
		return nil, err
	}

	// Used for later cases when may want to load metadata at regular intervals
	metadata.Fetched = time.Now()

	return &metadata, nil
}
