package bootstrap

import (
	"encoding/json"
	"fmt"
	"io"
	"mdcs-server/core/models"
	"net/http"
	"os"
	"time"
)

// The metadata this function returns is necessary.
// Server startup must fail if metadata server is unreachable
func FetchMetadata() (*models.VersionMetadata, error) {
	url := os.Getenv("VERSION_DATA_URL")

	if url == "" {
		return nil, fmt.Errorf("VERSION_DATA_URL not found")
	}

	client := http.Client{
		Timeout: 10 * time.Second,
	}

	response, err := client.Get(url)

	if err != nil {
		return nil, err
	}

	defer response.Body.Close()

	if response.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("bad status code: %d", response.StatusCode)
	}

	data, err := io.ReadAll(response.Body)

	if err != nil {
		return nil, err
	}

	var metadata models.VersionMetadata
	err = json.Unmarshal(data, &metadata)

	if err != nil {
		return nil, err
	}

	// This is used for later cases when may want to load metadata at regular
	// intervals
	metadata.FetchedAt = time.Now()

	return &metadata, nil
}
