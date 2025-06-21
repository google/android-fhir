CONTAINER_ID=$(docker ps --quiet --all --no-trunc --filter "name=sync-hapi-fhir-server")
if [ -z "$CONTAINER_ID" ]; then
  docker run -p 8080:8080 --detach --name sync-hapi-fhir-server hapiproject/hapi:latest
  sleep 3
else
  docker container restart "$CONTAINER_ID"
fi

if [ -z "$(docker ps --quiet --no-trunc --filter "name=sync-hapi-fhir-server")" ]; then
  echo "Failed to start Hapi server"
  exit 1
fi

