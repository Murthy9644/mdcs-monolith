package bootstrap

import "mdcs-server/core/models"

var Metadata *models.VersionMetadata

func BootstrapHandler() bool {

	// Load .env globally
	err := LoadEnv()

	if err != nil {
		return false
	}

	// Fetch version metadata
	metadata, err := FetchMetadata()

	if err != nil {
		Metadata = nil
	} else {
		Metadata = metadata
	}

	return true
}
