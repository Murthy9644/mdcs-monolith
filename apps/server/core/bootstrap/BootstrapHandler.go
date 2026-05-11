package bootstrap

var Metadata *VersionMetadata

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
