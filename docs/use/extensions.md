# FHIR Extensions

This page lists [FHIR Extensions](http://hl7.org/fhir/extensibility.html) defined by the Android FHIR SDK.

* Dialog extension (https://github.com/google/android-fhir/StructureDefinition/dialog)

  This extension can only be used if the questionnaire item type is `choice` and has an item-control of type `check-box` or `radio-button`.

* GPS Coordinate URL extension (https://github.com/google/StructureDefinition/gps-coordinate)
   
  This URL extension can only be used if the questionnaire item type is `decimal` and has a valueString `latitude` or `longitude` .