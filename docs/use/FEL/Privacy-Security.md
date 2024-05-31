# Privacy & Security

## Database encryption

In FHIREngine, by default, we utilize Android [encryption](https://source.android.com/security/encryption) and support Android 5.0 and above. This ensures data is always encrypted at rest.

On Android 6.0 or above, FHIREngine offers **additional** application level database encryption via SQLChiper.

To enable database encryption, you should invoke the `FhirEngineProvider#init(FhirEngineConfiguration)` in your `Application#onCreate` before creating any instance of `FhirEngine`. You can select one of the two database error handlings:

* `UNSPECIFIED`: All database errors will be propagated to the call site. The caller shall handle the database error on a case-by-case basis.
* `RECREATE_AT_OPEN`: If a database error occurs at open, automatically recreate the database. This strategy is **NOT** respected when opening a previously unencrypted database with an encrypted configuration or vice versa. An `IllegalStateException` is thrown instead.

Here is a code snippet of enabling database encryption with `RECREATE_AT_OPEN` error handling strategy:

```
FhirEngineProvider.init(
  FhirEngineConfiguration(
    enableEncryptionIfSupported = true,
    databaseErrorStrategy = RECREATE_AT_OPEN
  )
)
```

## Data safety

This library does not collect or share any personal or sensitive [user data](https://developer.android.com/guide/topics/data/collect-share) with any third party libraries or SDKs.
