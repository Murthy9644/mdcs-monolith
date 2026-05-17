package bootstrap

import "time"

// version_metadata DTO

type CompatibleAppVersions struct {
	Min string `json:"min"`
	Max string `json:"max"`
}

type PluginMetadata struct {
	AvailableVersion        string                `json:"available_version"`
	MinimumSupportedVersion string                `json:"minimum_supported_version"`
	ReleaseDate             string                `json:"release_date"`
	CompatibleAppVersions   CompatibleAppVersions `json:"compatible_app_versions"`
}

type AppMetadata struct {
	LatestVersion           string `json:"latest_version"`
	MinimumSupportedVersion string `json:"minimum_supported_version"`
	ReleaseDate             string `json:"release_date"`
}

// Main DTO
type VersionMetadata struct {
	App       AppMetadata               `json:"app"`
	Plugins   map[string]PluginMetadata `json:"plugins"`
	Changes   []string                  `json:"changes"`
	FetchedAt time.Time
}
