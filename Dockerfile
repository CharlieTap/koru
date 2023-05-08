FROM  --platform=linux/amd64 gradle:8.1.1-jdk11
#Cannot use alpine as the kotlin native toolchain uses gcc in places :(
#Cannot use jdk 17 as fails to spawn helper

SHELL ["/bin/bash", "-c"]

ARG BINARY=program.kexe
ARG MODE=debug

ENV COMMAND=${BINARY}
# For some reason konan seems to wipe the $HOME/.konan folder between builds
# By defining this env variable konan will write cache out to a folder we are already using for caching
ENV KONAN_DATA_DIR=/home/gradle/.gradle

RUN apt-get update && \
    apt-get install -y \
    build-essential \
    liburing-dev

COPY . /app
WORKDIR /app

RUN \
    --mount=type=cache,target=/app/.gradle,rw \
    --mount=type=cache,target=/app/bin/build,rw \
    --mount=type=cache,target=/home/gradle/.gradle,rw \
    ./gradlew --stacktrace link${MODE^}ExecutableNative --no-daemon

# Copy the generated binaries out of the build volume and into the PATH
RUN \
    --mount=type=cache,target=/app/bin/build,rw \
    find /app/bin/build/ \
        -maxdepth 4 \
        -type f -executable \
        -exec cp {} /usr/local/bin \;

CMD ["sh", "-c", "$COMMAND"]