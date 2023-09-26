package com.google.android.fhir.document

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.document.dataClasses.SHLData
import org.hl7.fhir.r4.model.Bundle.BundleType
import org.hl7.fhir.r4.model.Resource
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
class DocGenTest {

  private val docGenerator = DocumentGenerator()
  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  val res1 = "{\n" +
    "      \"resourceType\" : \"Observation\",\n" +
    "      \"id\" : \"b4916505-a06b-460c-9be8-011609282457\",\n" +
    "      \"text\" : {\n" +
    "        \"status\" : \"generated\",\n" +
    "        \"div\" : \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><p><b>Generated Narrative: Observation</b><a name=\\\"b4916505-a06b-460c-9be8-011609282457\\\"> </a></p><div style=\\\"display: inline-block; background-color: #d9e0e7; padding: 6px; margin: 4px; border: 1px solid #8da1b4; border-radius: 5px; line-height: 60%\\\"><p style=\\\"margin-bottom: 0px\\\">Resource Observation &quot;b4916505-a06b-460c-9be8-011609282457&quot; </p></div><p><b>status</b>: final</p><p><b>category</b>: Laboratory <span style=\\\"background: LightGoldenRodYellow; margin: 4px; border: 1px solid khaki\\\"> (<a href=\\\"http://terminology.hl7.org/5.0.0/CodeSystem-observation-category.html\\\">Observation Category Codes</a>#laboratory)</span></p><p><b>code</b>: E Ab [Presence] in Serum or Plasma <span style=\\\"background: LightGoldenRodYellow; margin: 4px; border: 1px solid khaki\\\"> (<a href=\\\"https://loinc.org/\\\">LOINC</a>#1018-1)</span></p><p><b>subject</b>: <a href=\\\"#Patient_2b90dd2b-2dab-4c75-9bb9-a355e07401e8\\\">See above (Patient/2b90dd2b-2dab-4c75-9bb9-a355e07401e8)</a></p><p><b>effective</b>: 2015-10-10 09:35:00+0100</p><p><b>performer</b>: <a href=\\\"#Organization_45a5c5b1-4ec1-4d60-b4b2-ff5a84a41fd7\\\">See above (Organization/45a5c5b1-4ec1-4d60-b4b2-ff5a84a41fd7)</a></p><p><b>value</b>: Positive <span style=\\\"background: LightGoldenRodYellow; margin: 4px; border: 1px solid khaki\\\"> (<a href=\\\"https://browser.ihtsdotools.org/\\\">SNOMED CT</a>#10828004)</span></p></div>\"\n" +
    "      },\n" +
    "      \"status\" : \"final\",\n" +
    "      \"category\" : [{\n" +
    "        \"coding\" : [{\n" +
    "          \"system\" : \"http://terminology.hl7.org/CodeSystem/observation-category\",\n" +
    "          \"code\" : \"laboratory\"\n" +
    "        }]\n" +
    "      }],\n" +
    "      \"code\" : {\n" +
    "        \"coding\" : [{\n" +
    "          \"system\" : \"http://loinc.org\",\n" +
    "          \"code\" : \"1018-1\",\n" +
    "          \"display\" : \"E Ab [Presence] in Serum or Plasma\"\n" +
    "        }]\n" +
    "      },\n" +
    "      \"subject\" : {\n" +
    "        \"reference\" : \"Patient/2b90dd2b-2dab-4c75-9bb9-a355e07401e8\"\n" +
    "      },\n" +
    "      \"effectiveDateTime\" : \"2015-10-10T09:35:00+01:00\",\n" +
    "      \"performer\" : [{\n" +
    "        \"reference\" : \"Organization/45a5c5b1-4ec1-4d60-b4b2-ff5a84a41fd7\"\n" +
    "      }],\n" +
    "      \"valueCodeableConcept\" : {\n" +
    "        \"coding\" : [{\n" +
    "          \"system\" : \"http://snomed.info/sct\",\n" +
    "          \"code\" : \"10828004\",\n" +
    "          \"display\" : \"Positive\"\n" +
    "        }]\n" +
    "      }\n" +
    "    }"

  val res2 = "{\"resourceType\" : \"AllergyIntolerance\",\n" +
    "      \"id\" : \"c7781f44-6df8-4a8b-9e06-0b34263a47c5\",\n" +
    "      \"text\" : {\n" +
    "        \"status\" : \"generated\",\n" +
    "        \"div\" : \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><p><b>Generated Narrative: AllergyIntolerance</b><a name=\\\"c7781f44-6df8-4a8b-9e06-0b34263a47c5\\\"> </a></p><div style=\\\"display: inline-block; background-color: #d9e0e7; padding: 6px; margin: 4px; border: 1px solid #8da1b4; border-radius: 5px; line-height: 60%\\\"><p style=\\\"margin-bottom: 0px\\\">Resource AllergyIntolerance &quot;c7781f44-6df8-4a8b-9e06-0b34263a47c5&quot; </p></div><p><b>clinicalStatus</b>: Active <span style=\\\"background: LightGoldenRodYellow; margin: 4px; border: 1px solid khaki\\\"> (<a href=\\\"http://terminology.hl7.org/5.0.0/CodeSystem-allergyintolerance-clinical.html\\\">AllergyIntolerance Clinical Status Codes</a>#active)</span></p><p><b>code</b>: No known food allergies <span style=\\\"background: LightGoldenRodYellow; margin: 4px; border: 1px solid khaki\\\"> (<a href=\\\"CodeSystem-absent-unknown-uv-ips.html\\\">Absent and Unknown Data - IPS</a>#no-known-food-allergies)</span></p><p><b>patient</b>: <a href=\\\"#Patient_2b90dd2b-2dab-4c75-9bb9-a355e07401e8\\\">See above (Patient/2b90dd2b-2dab-4c75-9bb9-a355e07401e8)</a></p></div>\"\n" +
    "      },\n" +
    "      \"clinicalStatus\" : {\n" +
    "        \"coding\" : [{\n" +
    "          \"system\" : \"http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical\",\n" +
    "          \"code\" : \"active\"\n" +
    "        }]\n" +
    "      },\n" +
    "      \"code\" : {\n" +
    "        \"coding\" : [{\n" +
    "          \"system\" : \"http://hl7.org/fhir/uv/ips/CodeSystem/absent-unknown-uv-ips\",\n" +
    "          \"code\" : \"no-known-food-allergies\"\n" +
    "        }]\n" +
    "      },\n" +
    "      \"patient\" : {\n" +
    "        \"reference\" : \"Patient/2b90dd2b-2dab-4c75-9bb9-a355e07401e8\"\n" +
    "      }\n" +
    "    }"

  val res3 = "{\n" +
    "      \"resourceType\" : \"Immunization\",\n" +
    "      \"id\" : \"40b7b6a0-c043-423a-9959-be3707e728b2\",\n" +
    "      \"language\" : \"fr-LU\",\n" +
    "      \"text\" : {\n" +
    "        \"status\" : \"generated\",\n" +
    "        \"div\" : \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\" xml:lang=\\\"fr-LU\\\" lang=\\\"fr-LU\\\">\\n\\t\\t\\t\\t\\t\\t<p>\\n\\t\\t\\t\\t\\t\\t\\t<b>Marie Lux-Brennard</b>\\n\\t\\t\\t\\t\\t\\t\\t(Apr 17, 1998)\\n\\t\\t\\t\\t\\t\\t</p>\\n\\t\\t\\t\\t\\t\\t<p>Vaccin anti diphtérie-coqueluche-tétanos-poliomyélite, Jun 3, 1998, 10:00:00 PM</p>\\n\\t\\t\\t\\t\\t\\t<p>Voie intramusculaire, Cuisse droite</p>\\n\\t\\t\\t\\t\\t</div>\"\n" +
    "      },\n" +
    "      \"status\" : \"completed\",\n" +
    "      \"vaccineCode\" : {\n" +
    "        \"coding\" : [{\n" +
    "          \"system\" : \"http://snomed.info/sct\",\n" +
    "          \"code\" : \"836508001\",\n" +
    "          \"display\" : \"Bordetella pertussis and Clostridium tetani and Corynebacterium diphtheriae and Human poliovirus antigens-containing vaccine product\",\n" +
    "          \"_display\" : {\n" +
    "            \"extension\" : [{\n" +
    "              \"url\" : \"http://hl7.org/fhir/StructureDefinition/translation\",\n" +
    "              \"extension\" : [{\n" +
    "                \"url\" : \"lang\",\n" +
    "                \"valueCode\" : \"fr-LU\"\n" +
    "              },\n" +
    "              {\n" +
    "                \"url\" : \"content\",\n" +
    "                \"valueString\" : \"Vaccin anti diphtérie-coqueluche-tétanos-poliomyélite\"\n" +
    "              }]\n" +
    "            }]\n" +
    "          }\n" +
    "        },\n" +
    "        {\n" +
    "          \"system\" : \"http://www.whocc.no/atc\",\n" +
    "          \"code\" : \"J07CA02\",\n" +
    "          \"display\" : \"diphtheria-pertussis-poliomyelitis-tetanus\",\n" +
    "          \"_display\" : {\n" +
    "            \"extension\" : [{\n" +
    "              \"url\" : \"http://hl7.org/fhir/StructureDefinition/translation\",\n" +
    "              \"extension\" : [{\n" +
    "                \"url\" : \"lang\",\n" +
    "                \"valueCode\" : \"fr-LU\"\n" +
    "              },\n" +
    "              {\n" +
    "                \"url\" : \"content\",\n" +
    "                \"valueString\" : \"DIPHTERIE - COQUELUCHE - POLIOMYELITE - TETANOS\"\n" +
    "              }]\n" +
    "            }]\n" +
    "          }\n" +
    "        }],\n" +
    "        \"text\" : \"Vaccin anti diphtérie-coqueluche-tétanos-poliomyélite\"\n" +
    "      },\n" +
    "      \"patient\" : {\n" +
    "        \"reference\" : \"Patient/2b90dd2b-2dab-4c75-9bb9-a355e07401e8\"\n" +
    "      },\n" +
    "      \"occurrenceDateTime\" : \"1998-06-04T00:00:00+02:00\",\n" +
    "      \"primarySource\" : true,\n" +
    "      \"lotNumber\" : \"AXK23RWERS\",\n" +
    "      \"expirationDate\" : \"2000-06-01\",\n" +
    "      \"site\" : {\n" +
    "        \"coding\" : [{\n" +
    "          \"system\" : \"http://snomed.info/sct\",\n" +
    "          \"code\" : \"11207009\",\n" +
    "          \"display\" : \"Right thigh\",\n" +
    "          \"_display\" : {\n" +
    "            \"extension\" : [{\n" +
    "              \"url\" : \"http://hl7.org/fhir/StructureDefinition/translation\",\n" +
    "              \"extension\" : [{\n" +
    "                \"url\" : \"lang\",\n" +
    "                \"valueCode\" : \"fr-LU\"\n" +
    "              },\n" +
    "              {\n" +
    "                \"url\" : \"content\",\n" +
    "                \"valueString\" : \"Cuisse droite\"\n" +
    "              }]\n" +
    "            }]\n" +
    "          }\n" +
    "        }],\n" +
    "        \"text\" : \"Cuisse droite\"\n" +
    "      },\n" +
    "      \"route\" : {\n" +
    "        \"coding\" : [{\n" +
    "          \"system\" : \"http://standardterms.edqm.eu\",\n" +
    "          \"code\" : \"20035000\",\n" +
    "          \"display\" : \"Intramuscular use\",\n" +
    "          \"_display\" : {\n" +
    "            \"extension\" : [{\n" +
    "              \"url\" : \"http://hl7.org/fhir/StructureDefinition/translation\",\n" +
    "              \"extension\" : [{\n" +
    "                \"url\" : \"lang\",\n" +
    "                \"valueCode\" : \"fr-LU\"\n" +
    "              },\n" +
    "              {\n" +
    "                \"url\" : \"content\",\n" +
    "                \"valueString\" : \"Voie intramusculaire\"\n" +
    "              }]\n" +
    "            }]\n" +
    "          }\n" +
    "        }],\n" +
    "        \"text\" : \"Voie intramusculaire\"\n" +
    "      },\n" +
    "      \"protocolApplied\" : [{\n" +
    "        \"targetDisease\" : [{\n" +
    "          \"coding\" : [{\n" +
    "            \"system\" : \"http://snomed.info/sct\",\n" +
    "            \"code\" : \"27836007\",\n" +
    "            \"display\" : \"Pertussis\"\n" +
    "          },\n" +
    "          {\n" +
    "            \"system\" : \"http://snomed.info/sct\",\n" +
    "            \"code\" : \"76902006\",\n" +
    "            \"display\" : \"Tetanus\"\n" +
    "          },\n" +
    "          {\n" +
    "            \"system\" : \"http://snomed.info/sct\",\n" +
    "            \"code\" : \"398102009\",\n" +
    "            \"display\" : \"Acute poliomyelitis\"\n" +
    "          },\n" +
    "          {\n" +
    "            \"system\" : \"http://snomed.info/sct\",\n" +
    "            \"code\" : \"397430003\",\n" +
    "            \"display\" : \"Diphtheria caused by Corynebacterium diphtheriae\"\n" +
    "          }]\n" +
    "        }],\n" +
    "        \"doseNumberPositiveInt\" : 1\n" +
    "      }]\n" +
    "    }"

  val res4 = "{\n" +
    "      \"resourceType\" : \"Condition\",\n" +
    "      \"id\" : \"c64139e7-f02d-409c-bf34-75e8bf23bc80\",\n" +
    "      \"text\" : {\n" +
    "        \"status\" : \"generated\",\n" +
    "        \"div\" : \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><p><b>Generated Narrative: Condition</b><a name=\\\"c64139e7-f02d-409c-bf34-75e8bf23bc80\\\"> </a></p><div style=\\\"display: inline-block; background-color: #d9e0e7; padding: 6px; margin: 4px; border: 1px solid #8da1b4; border-radius: 5px; line-height: 60%\\\"><p style=\\\"margin-bottom: 0px\\\">Resource Condition &quot;c64139e7-f02d-409c-bf34-75e8bf23bc80&quot; </p></div><p><b>identifier</b>: id: c87bf51c-e53c-4bfe-b8b7-aa62bdd93002</p><p><b>clinicalStatus</b>: Active <span style=\\\"background: LightGoldenRodYellow; margin: 4px; border: 1px solid khaki\\\"> (<a href=\\\"http://terminology.hl7.org/5.0.0/CodeSystem-condition-clinical.html\\\">Condition Clinical Status Codes</a>#active)</span></p><p><b>verificationStatus</b>: Confirmed <span style=\\\"background: LightGoldenRodYellow; margin: 4px; border: 1px solid khaki\\\"> (<a href=\\\"http://terminology.hl7.org/5.0.0/CodeSystem-condition-ver-status.html\\\">ConditionVerificationStatus</a>#confirmed)</span></p><p><b>category</b>: Problem <span style=\\\"background: LightGoldenRodYellow; margin: 4px; border: 1px solid khaki\\\"> (<a href=\\\"https://loinc.org/\\\">LOINC</a>#75326-9)</span></p><p><b>severity</b>: Moderate <span style=\\\"background: LightGoldenRodYellow; margin: 4px; border: 1px solid khaki\\\"> (<a href=\\\"https://loinc.org/\\\">LOINC</a>#LA6751-7)</span></p><p><b>code</b>: Menopausal flushing (finding) <span style=\\\"background: LightGoldenRodYellow; margin: 4px; border: 1px solid khaki\\\"> (<a href=\\\"https://browser.ihtsdotools.org/\\\">SNOMED CT</a>#198436008; <a href=\\\"http://terminology.hl7.org/5.0.0/CodeSystem-icd10.html\\\">ICD-10</a>#N95.1 &quot;Menopausal and female climacteric states&quot;)</span></p><p><b>subject</b>: <a href=\\\"#Patient_2b90dd2b-2dab-4c75-9bb9-a355e07401e8\\\">See above (Patient/2b90dd2b-2dab-4c75-9bb9-a355e07401e8)</a></p><p><b>onset</b>: 2015</p><p><b>recordedDate</b>: 2016-10</p></div>\"\n" +
    "      },\n" +
    "      \"identifier\" : [{\n" +
    "        \"system\" : \"urn:oid:1.2.3.999\",\n" +
    "        \"value\" : \"c87bf51c-e53c-4bfe-b8b7-aa62bdd93002\"\n" +
    "      }],\n" +
    "      \"clinicalStatus\" : {\n" +
    "        \"coding\" : [{\n" +
    "          \"system\" : \"http://terminology.hl7.org/CodeSystem/condition-clinical\",\n" +
    "          \"code\" : \"activ\"\n" +
    "        }]\n" +
    "      },\n" +
    "      \"verificationStatus\" : {\n" +
    "        \"coding\" : [{\n" +
    "          \"system\" : \"http://terminology.hl7.org/CodeSystem/condition-ver-status\",\n" +
    "          \"code\" : \"confirmed\"\n" +
    "        }]\n" +
    "      },\n" +
    "      \"category\" : [{\n" +
    "        \"coding\" : [{\n" +
    "          \"system\" : \"http://loinc.org\",\n" +
    "          \"code\" : \"75326-9\",\n" +
    "          \"display\" : \"Problem\"\n" +
    "        }]\n" +
    "      }],\n" +
    "      \"severity\" : {\n" +
    "        \"coding\" : [{\n" +
    "          \"system\" : \"http://loinc.org\",\n" +
    "          \"code\" : \"LA6751-7\",\n" +
    "          \"display\" : \"Moderate\"\n" +
    "        }]\n" +
    "      },\n" +
    "      \"code\" : {\n" +
    "        \"coding\" : [{\n" +
    "          \"system\" : \"http://snomed.info/sct\",\n" +
    "          \"code\" : \"198436008\",\n" +
    "          \"display\" : \"Menopausal flushing (finding)\",\n" +
    "          \"_display\" : {\n" +
    "            \"extension\" : [{\n" +
    "              \"url\" : \"http://hl7.org/fhir/StructureDefinition/translation\",\n" +
    "              \"extension\" : [{\n" +
    "                \"url\" : \"lang\",\n" +
    "                \"valueCode\" : \"nl-NL\"\n" +
    "              },\n" +
    "              {\n" +
    "                \"url\" : \"content\",\n" +
    "                \"valueString\" : \"opvliegers\"\n" +
    "              }]\n" +
    "            }]\n" +
    "          }\n" +
    "        },\n" +
    "        {\n" +
    "          \"system\" : \"http://hl7.org/fhir/sid/icd-10\",\n" +
    "          \"code\" : \"N95.1\",\n" +
    "          \"display\" : \"Menopausal and female climacteric states\"\n" +
    "        }]\n" +
    "      },\n" +
    "      \"subject\" : {\n" +
    "        \"reference\" : \"Patient/2b90dd2b-2dab-4c75-9bb9-a355e07401e8\"\n" +
    "      },\n" +
    "      \"onsetDateTime\" : \"2015\",\n" +
    "      \"recordedDate\" : \"2016-10\"\n" +
    "    }"

  val res5 = "{\n" +
    "      \"resourceType\" : \"Medication\",\n" +
    "      \"id\" : \"976d0804-cae0-45ae-afe3-a19f3ceba6bc\",\n" +
    "      \"text\" : {\n" +
    "        \"status\" : \"generated\",\n" +
    "        \"div\" : \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><p><b>Generated Narrative: Medication</b><a name=\\\"976d0804-cae0-45ae-afe3-a19f3ceba6bc\\\"> </a></p><div style=\\\"display: inline-block; background-color: #d9e0e7; padding: 6px; margin: 4px; border: 1px solid #8da1b4; border-radius: 5px; line-height: 60%\\\"><p style=\\\"margin-bottom: 0px\\\">Resource Medication &quot;976d0804-cae0-45ae-afe3-a19f3ceba6bc&quot; </p></div><p><b>code</b>: Product containing anastrozole (medicinal product) <span style=\\\"background: LightGoldenRodYellow; margin: 4px; border: 1px solid khaki\\\"> (<a href=\\\"https://browser.ihtsdotools.org/\\\">SNOMED CT</a>#108774000; unknown#99872 &quot;ANASTROZOL 1MG TABLET&quot;; unknown#2076667 &quot;ANASTROZOL CF TABLET FILMOMHULD 1MG&quot;; <a href=\\\"http://terminology.hl7.org/5.0.0/CodeSystem-v3-WC.html\\\">WHO ATC</a>#L02BG03 &quot;anastrozole&quot;)</span></p></div>\"\n" +
    "      },\n" +
    "      \"code\" : {\n" +
    "        \"coding\" : [{\n" +
    "          \"system\" : \"http://snomed.info/sct\",\n" +
    "          \"code\" : \"108774000\",\n" +
    "          \"display\" : \"Product containing anastrozole (medicinal product)\"\n" +
    "        },\n" +
    "        {\n" +
    "          \"system\" : \"urn:oid:2.16.840.1.113883.2.4.4.1\",\n" +
    "          \"code\" : \"99872\",\n" +
    "          \"display\" : \"ANASTROZOL 1MG TABLET\"\n" +
    "        },\n" +
    "        {\n" +
    "          \"system\" : \"urn:oid:2.16.840.1.113883.2.4.4.7\",\n" +
    "          \"code\" : \"2076667\",\n" +
    "          \"display\" : \"ANASTROZOL CF TABLET FILMOMHULD 1MG\"\n" +
    "        },\n" +
    "        {\n" +
    "          \"system\" : \"http://www.whocc.no/atc\",\n" +
    "          \"code\" : \"L02BG03\",\n" +
    "          \"display\" : \"anastrozole\"\n" +
    "        }]\n" +
    "      }\n" +
    "    }"

  private val list = listOf(parser.parseResource(res1) as Resource, parser.parseResource(res2) as Resource, parser.parseResource(res3) as Resource, parser.parseResource(res3) as Resource, parser.parseResource(res5) as Resource)

  @Test
  fun testDocGen() {
    val doc = docGenerator.generateIPS(list)
    println(parser.encodeResourceToString(doc.document))
    assertEquals(doc.document.type, BundleType.DOCUMENT)
  }

  @Test
  fun testSHLDataCanBeInitialised() {
    val doc = docGenerator.generateIPS(list)
    val shlData = SHLData(doc)
    shlData.label = "abc"
    assertEquals(shlData.label, "abc")
  }
}