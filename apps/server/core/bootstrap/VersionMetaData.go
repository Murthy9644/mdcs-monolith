package bootstrap

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"os"
	"time"
)

type AppMetadata struct {
	LatestVersion           string `json:"latest_version"`
	MinimumSupportedVersion string `json:"minimum_supported_version"`
	ReleaseDate             string `json:"release_date"`
	CriticalUpdate          bool   `json:"critical_update"`
}

type VersionMetadata struct {
	App       AppMetadata       `json:"app"`
	Modules   map[string]string `json:"modules"`
	Changes   []string          `json:"changes"`
	FetchedAt time.Time
}

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
