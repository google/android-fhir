# Database

## Migrations

If you are making changes to the database schema (in the `engine` or the `knowledge` module), you
need to consider how applications with Android FHIR SDK dependencies can upgrade to the new schema
without losing or corrupting existing data already on device. This can be done with [Room database
migration](https://developer.android.com/training/data-storage/room/migrating-db-versions).

!!! tip
    A new JSON schema file will be generated under the `schemas` folder in the module when you
    update the database version. If you are having trouble with this, make sure you run the gradle
    command with `--rerun-tasks`:

    ```sh
    ./gradlew :<module>:build --rerun-tasks
    ```
