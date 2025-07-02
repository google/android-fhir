CONTAINER_ID=$(docker ps --quiet --all --no-trunc --filter "name=sync-hapi-fhir-server")
if [ -n "$CONTAINER_ID" ]; then
    docker container stop "$CONTAINER_ID" > /dev/null
    docker container rm "$CONTAINER_ID" > /dev/null
fi

docker run -e JAVA_TOOL_OPTIONS="-Xmx1g" -p 8080:8080 --detach --name sync-hapi-fhir-server hapiproject/hapi:latest
sleep 3

# Checks for whether the container started successfully
if [ -z "$(docker ps --quiet --no-trunc --filter "name=sync-hapi-fhir-server")" ]; then
  echo "Failed to start Hapi server"
  exit 1
fi

