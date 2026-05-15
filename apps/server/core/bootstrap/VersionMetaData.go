package bootstrap

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"os"
	"time"
)

func FetchMetadata() (*VersionMetadata, error) {
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

	var metadata VersionMetadata
	err = json.Unmarshal(data, &metadata)

	if err != nil {
		return nil, err
	}

	metadata.FetchedAt = time.Now()

	return &metadata, nil
}
