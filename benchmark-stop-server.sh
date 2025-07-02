# Stop container instance
CONTAINER_ID=$(docker ps --quiet --no-trunc --filter "name=sync-hapi-fhir-server")
docker container stop "$CONTAINER_ID"
