# FHIR search specification support

This document tracks the support for the FHIR search specification supported by the SDK (fhirEngine library) and common example use cases. It will be updated over time.

## Search parameter types

The following table summarizes the support (released and planned) for [search parameter types](https://www.hl7.org/fhir/search.html) within the SDK:

|           | Support     | Notes                    |
|-----------|-------------|--------------------------|
| String    | Yes         |                          |
| Number    | Yes         |                          |
| Date      | Yes         |                          |
| DateTime  | Yes         |                          |
| Token     | Partial     | URI, code, boolean       |
| Reference | Yes         | See examples             |
| Quantity  | No          | Planned                  |
| Special   | In-progress | TBC                      |
| Composite | Yes         | AND and OR are supported |

## Search prefixes

https://www.hl7.org/fhir/search.html#prefix

|    | Support | Notes                     |
|----|---------|---------------------------|
| eq | Partial | Numerical and date values |
| ne | Partial | Numerical and date values |
| gt | Partial | Numerical and date values |
| lt | Partial | Numerical and date values |
| ge | Partial | Numerical and date values |
| le | Partial | Numerical and date values |
| sa | No      |                           |
| eb | No      |                           |
| ap | No      |                           |

## Modifiers

https://www.hl7.org/fhir/search.html#modifiers

|                                               | Support | Notes  |
|-----------------------------------------------|---------|--------|
| :contains                                     | Partial | String |
| :exact                                        | Partial | String |
| :missing, :text, :in, :below, :above, :not-in | No      |        |

Other:

* Search by meta-data - in-progress: https://github.com/google/android-fhir/issues/481
