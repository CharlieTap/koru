BINARY_NAME=koru.kexe
IMAGE_NAME=koru

BUILD_ARG_BINARY=--build-arg BINARY=$(BINARY_NAME)

.DEFAULT_GOAL := help
.PHONY: build build-release build-verbose debug run help

build:
	docker build $(BUILD_ARG_BINARY) -t $(IMAGE_NAME) .
build-release:
	docker build $(BUILD_ARG_BINARY) --build-arg MODE=release -t $(IMAGE_NAME) .
build-verbose:
	docker build $(BUILD_ARG_BINARY) -t $(IMAGE_NAME) --progress=plain .
debug: build
	docker run --rm -ti --platform linux/amd64 $(IMAGE_NAME) /bin/bash
run: build
	docker run --rm -ti --platform linux/amd64 $(IMAGE_NAME)
run-release: build-release
	docker run --rm -ti --platform linux/amd64 $(IMAGE_NAME)

help:
	@echo "Available targets:"
	@echo "  build         Build the Docker image in debug mode"
	@echo "  build-release Build the Docker image in release mode"
	@echo "  build-verbose Build the Docker image in debug mode with verbose output"
	@echo "  debug         Build (if necessary) and run the container attaching a bash shell"
	@echo "  run           Build (if necessary) and run the Docker container"
	@echo "  help          Display this help message"
