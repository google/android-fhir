POPULATION="${1}"
SERVER_URL="${2}"
SCRIPT_DIR=$(dirname "$0")
SYNTHEA_DIR="$SCRIPT_DIR"/synthea

if [ ! -d "$SYNTHEA_DIR" ]; then
  # build Synthea according to https://github.com/synthetichealth/synthea
  git clone https://github.com/synthetichealth/synthea.git "$SYNTHEA_DIR"
fi

rm -rf "$SYNTHEA_DIR"/output

cd "$SYNTHEA_DIR" || exit
# generate valid R4 resources in output/fhir
./run_synthea -m pregnancy -p "$POPULATION" -s 12345 --exporter.fhir.included_resources Patient --exporter.years_of_history 1 --exporter.fhir.use_us_core_ig false --exporter.fhir.transaction_bundle true --exporter.practitioner.fhir.export true --exporter.hospital.fhir.export true

cd - || exit
# Upload hospital information
for filename in "$SYNTHEA_DIR"/output/fhir/hospital*.json; do
  echo "Uploading $filename"
  curl "$SERVER_URL"/fhir --data-binary "@$filename" -H "Content-Type: application/fhir+json"  > /dev/null
done

# Upload practitioner information
for filename in "$SYNTHEA_DIR"/output/fhir/practitioner*.json; do
  echo "Uploading $filename"
  curl "$SERVER_URL"/fhir --data-binary "@$filename" -H "Content-Type: application/fhir+json"  > /dev/null
done

# Upload fhir patients data
for filename in "$SYNTHEA_DIR"/output/fhir/*.json; do
  echo "Uploading $filename"
  curl "$SERVER_URL"/fhir --data-binary "@$filename" -H "Content-Type: application/fhir+json"  > /dev/null
done
