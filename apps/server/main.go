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
	if !bootstrap.Run() {
		return
	}

	mux := http.NewServeMux()
	modules.Router(mux)

	app := http.StripPrefix("/mdcs", mux)

	// Shutdown channel and hook
	// Buffer size 1 is enough because, will be catching one signal at a time
	// and channel waits till that's read
	sigchan := make(chan os.Signal, 1)
	signal.Notify(sigchan, os.Interrupt, syscall.SIGTERM)

	go func() {
		<-sigchan

		fmt.Println("process: Attempting cache flush")
		err := data.Flush()

		if err != nil {
			fmt.Println("fatal: Flush failed: ", err)
		} else {
			fmt.Println("success: Flush successful")
		}

		os.Exit(0)
	}()

	// Port should be moved to .env and is imported at run-time
	fmt.Println("process: Server listening at :1800")
	http.ListenAndServe("0.0.0.0:1800", app)
}
