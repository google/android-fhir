#!/usr/bin/env bash

# Parameters
POPULATION="${1:-3}"
SCRIPT_DIR=$(dirname "$0")
SYNTHEA_DIR="$SCRIPT_DIR"/synthea
GIT_ROOT_DIR=$(git rev-parse --show-toplevel)

if [ ! -d "$SYNTHEA_DIR" ]; then
  # build Synthea according to https://github.com/synthetichealth/synthea
  git clone https://github.com/synthetichealth/synthea.git "$SYNTHEA_DIR"
fi
pushd "$SYNTHEA_DIR" || exit
# generate valid R4 resources in output/fhir
./run_synthea -m pregnancy -p "$POPULATION" --exporter.fhir.included_resources Patient --exporter.fhir.transaction_bundle false --exporter.years_of_history 1 --exporter.fhir.use_us_core_ig false --exporter.fhir.bulk_data true
popd || exit

# Move to benchmark assets dir
mkdir -p "$GIT_ROOT_DIR"/engine/benchmarks/app/src/main/assets/bulk_data
cp "$SYNTHEA_DIR"/output/fhir/Patient*.ndjson "$GIT_ROOT_DIR"/engine/benchmarks/app/src/main/assets/bulk_data

rm -rf "$SYNTHEA_DIR"/output
