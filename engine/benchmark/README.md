# Android FHIR SDK Engine Benchmark module

Contains test cases that evaluate the performance of individual tasks executed for the first time directly on hardware. 

The test cases are designed to run in sequence of their alphabetic order to make sure larger tasks do not build cache for smaller ones. Their class names are prefixed by an extra letter to inform their position relative to others in the list.

# How to run the benchmark

In Android Studio, set your build variants to `release` and run your benchmark as you would any `@Test` using the gutter action next to your test class or method.

![gutter test action](https://developer.android.com/static/topic/performance/images/benchmark_images/microbenchmark_run.png)

The results will be similar to this:
```
1,297,374       ns        5345 allocs    trace    EngineDatabaseBenchmark.createAndGet
1,114,474,793   ns     4922289 allocs    trace    FhirSyncWorkerBenchmark.oneTimeSync_50patients
15,251,125      ns      100542 allocs    trace    FhirSyncWorkerBenchmark.oneTimeSync_1patient
179,806,709     ns      986017 allocs    trace    FhirSyncWorkerBenchmark.oneTimeSync_10patients
1,451,758       ns       11883 allocs    trace    GzipUploadInterceptorBenchmark.upload_10patientsWithGzip
1,537,559       ns       11829 allocs    trace    GzipUploadInterceptorBenchmark.upload_10patientsWithoutGzip
73,640,833      ns     1074360 allocs    trace    GzipUploadInterceptorBenchmark.upload_1000patientsWithGzip
7,493,642       ns      108428 allocs    trace    GzipUploadInterceptorBenchmark.upload_100patientsWithoutGzip
7,799,264       ns      108465 allocs    trace    GzipUploadInterceptorBenchmark.upload_100patientsWithGzip
71,189,333      ns     1074466 allocs    trace    GzipUploadInterceptorBenchmark.upload_1000patientsWithoutGzip

```

Alternatively, from the command line, run the connectedCheck to run all of the tests from specified Gradle module:

```bash
./gradlew :engine:benchmark:connectedReleaseAndroidTest
```

In this case, results will be saved to the `outputs/androidTest-results/connected/<device>/test-result.pb`. To visualize on Android Studio, click Run / Import Tests From File and find the `.pb` file