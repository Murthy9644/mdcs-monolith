package bootstrap

// Manages startup process of the application

import (
	"fmt"
	"mdcs-server/core/models"
	"mdcs-server/data"
)

/*
Bootstrap includes the phases:
- Load .env file globally
- Fetch the latest verion metadata and cache it
- Check persistence files existence and validate their format and schema
*/

var Metadata *models.VersionMetadata

func BootstrapHandler() bool {
	fmt.Println("Starting server bootstrap")
	err := LoadEnv()

	if err != nil {
		fmt.Println(".env file can't be locatted at root (/)")
		return false
	}

	fmt.Println(".env loaded successfully")

	metadata, err := FetchMetadata()

	if err != nil {
		fmt.Println("Could not load version metadata")
		return false
	} else {
		Metadata = metadata
		fmt.Println("Version metadata loaded successfully")
	}

	val_stat := SchForValidation()

	if val_stat {
		fmt.Println("Schema validated successfully")
		data.LoadFiles()
	}

	return val_stat
}
