# Module Android FHIR Engine Library

The FHIR Engine Library stores and manages FHIR resources locally on Android and
synchronizes with FHIR servers.

# Package com.google.android.fhir

Entrypoint for the Data Access API: basic access to local FHIR resources,
namely, the CRUD operations: create, read, update, and delete. 

# Package com.google.android.fhir.search

Entrypoint for the Search API: a Kotlin DSL (domain-specific language) for
searching local FHIR resources.

# Package com.google.android.fhir.sync

Entrypoint for the Sync API: synchronizes local FHIR resources with a remote
FHIR server/store.