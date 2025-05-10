## Benchmarking

# How to run the benchmark with Synthea for different population sizes
1. Change into the project's root directory
2. Run script to generate population data `sh engine/benchmark/generate_synthea.sh [<population-size>]`
   ```bash
   # For population of 100 patients
   sh engine/benchmark/generate_synthea.sh 100
    ```
3. Run benchmarks
    ```bash
   ./gradlew :engine:benchmark:connectedReleaseAndroidTest
   ```
