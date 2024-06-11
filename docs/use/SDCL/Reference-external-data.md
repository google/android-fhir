# Reference external data

A questionnaire may reference external information. If so, you must provide a `DataCaptureConfig` by implementing the `DataCaptureConfig.Provider` interface in your application's <code>[Application](https://developer.android.com/reference/android/app/Application)</code> class. Be sure to provide the same configuration throughout your application's lifecycle to avoid any configuration issues.

## Value sets

If a value set is not included as a [contained resource](http://www.hl7.org/fhir/references.html#contained) within the questionnaire or structure map that references it, you must provide an implementation of `ExternalAnswerValueSetResolver.resolve()` which describes how to translate a value set's canonical URL to the list of actual codes:

```
class FhirApplication : Application(), DataCaptureConfig.Provider {

  private val dataCaptureConfiguration by lazy {
    DataCaptureConfig(
      valueSetResolverExternal =
        object : ExternalAnswerValueSetResolver {
          override suspend fun resolve(uri: String): List<Coding> {
            return lookupCodesFromDb(uri)
          }
        }
    )
  }

  override fun getDataCaptureConfig(): DataCaptureConfig {
    return dataCaptureConfiguration
  }
}
```

`ExternalAnswerValueSetResolver` is used for any URI that does not look like an internal reference where the URI starts with `#`.

## FHIR NPM packages

FHIR resources published in [FHIR Packages](https://confluence.hl7.org/display/FHIR/NPM+Package+Specification) can be used to provide context while performing StructureMap-based data extraction.
