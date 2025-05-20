#!/usr/bin/env bash

# Parameters
POPULATION="${1}"
OUTPUT_DIR="${2}"
SCRIPT_DIR=$(dirname "$0")
SYNTHEA_DIR="$SCRIPT_DIR"/synthea

if [ ! -d "$SYNTHEA_DIR" ]; then
  # build Synthea according to https://github.com/synthetichealth/synthea
  git clone https://github.com/synthetichealth/synthea.git "$SYNTHEA_DIR"
fi

rm -rf "$SYNTHEA_DIR"/output

cd "$SYNTHEA_DIR" || exit
# generate valid R4 resources in output/fhir
./run_synthea -m pregnancy -p "$POPULATION" --exporter.fhir.included_resources Patient --exporter.fhir.transaction_bundle false --exporter.years_of_history 1 --exporter.fhir.use_us_core_ig false --exporter.fhir.bulk_data true

# Move to output dir
if [ -n "$OUTPUT_DIR" ]; then
  mkdir -p "$OUTPUT_DIR"
  cp "$SYNTHEA_DIR"/output/fhir/*.ndjson "$OUTPUT_DIR"
fi
