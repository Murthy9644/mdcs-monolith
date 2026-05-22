package main

// Main Server for MDCS

import (
	"fmt"
	"mdcs-server/core/bootstrap"
	"mdcs-server/data"
	"mdcs-server/modules"
	"net/http"
	"os"
	"os/signal"
	"syscall"
)

func main() {
	if !bootstrap.BootstrapHandler() {
		return
	}

	mux := http.NewServeMux()
	modules.Router(mux)

	mux_final := http.StripPrefix("/mdcs", mux)

	// Shutdown channel and hook
	// Buffer size 1 is enough because, will be catching one signal at a
	// time and channel waits till that's read
	sigchan := make(chan os.Signal, 1)
	signal.Notify(sigchan, os.Interrupt, syscall.SIGTERM)

	go func() {
		<-sigchan

		fmt.Println("Attempting cache flush")
		err := data.FlushFiles()

		if err != nil {
			fmt.Println("Flush failed: ", err)
		} else {
			fmt.Println("Flush successful")
		}

		os.Exit(0)
	}()

	fmt.Println("\nServer listening at :1097")
	http.ListenAndServe("0.0.0.0:1097", mux_final)
}
