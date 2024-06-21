# Author Questionnaires

An Android app developer can use the Structured Data Capture Library's capabilities without a deep understanding of FHIR or FHIR questionnaires; similarly, healthcare data experts can build questionnaires independently from the app development workflow. However, understanding FHIR Questionnaires can be helpful for developers to debug issues, implement more advanced features, or authoring questionnaires themselves. This section is meant to provide Android developers a brief overview of the Questionnaire with a focus on the parts relevant for the Structured Data Capture Library.

You should be familiar with:

* Reading and writing [JSON](https://www.json.org/)
* Working with data structures besides FHIR
* (Recommended) [FHIR overview for developers](https://www.hl7.org/fhir/overview-dev.html)

The Structured Data Capture Library uses the [FHIR Questionnaire](https://www.hl7.org/fhir/questionnaire.html) as its fundamental data structure. A FHIR Questionnaire is a structured set of questions for collecting data from the end users. In addition, the [Structured Data Capture](http://hl7.org/fhir/uv/sdc/) (SDC) implementation guide supplements questionnaires by defining capabilities for more advanced control of rendering and question flow.

* [Example questionnaires from HL7](https://www.hl7.org/fhir/questionnaire-examples.html)
* [Example questionnaires from the SDC IG](http://hl7.org/fhir/uv/sdc/artifacts.html#example-example-instances)

## Questionnaire builders

There are a number of web-based tools for building FHIR Questionnaires like the [NLM Form Builder](https://lhcformbuilder.nlm.nih.gov/), which are a great starting point for both developers and non-developers to create and edit Questionnaires. They allow you to add and edit questions using a simple drag-and-drop interface, and include some advanced features like conditional display. Many features which require extensions like [data extraction and population](#data-extraction-and-population) or [item control](FHIR-specification-support.md#item-control) are not included in the UI but can be manually added by saving as a JSON file and editing the file. Typically, if you load a file with manually added extensions back into a Questionnaire builder and edit an item with an extension, the extension is maintained. However, if you add new items any desired extensions would need to be manually added.

One possible workflow is to have a non-developer create an initial version of a questionnaire using a Questionnaire builder tool, then saving it as a JSON file and it handing off to someone familiar with the FHIR Questionnaire syntax to add any desired extensions or other advanced functionality manually.

Questionnaire builders may support different parts of the SDC implementation guide than the Structured Data Capture Library does, so double-check the SDC Library's [FHIR specification support](FHIR-specification-support.md) if something doesn't seem to be working.

## Questionnaire basics

Following is an example of a very simple Questionnaire:

```json
{
  "resourceType": "Questionnaire",
  "status": "draft",
  "item": [
    {
      "linkId": "1",
      "text": "Name",
      "type": "string"
    }
  ]
}

```

The `item` array contains a single question object which collects a string. The optional `text` field means this question will have a text label containing "Name" when rendered. `linkId` is used to identify the specific component of the Questionnaire. Two common conventions for `linkId` are using the numeric representation of the nested structure (`1`, `1.1`, `1.2`, etc.), or descriptive names (`patient-info`, `patient-info-name`, `patient-info-dob`, for example).

Let's look at a more complex Questionnaire, focused on the top-level `item` element:

```json
"item": [
  {
    "linkId": "1",
    "text": "Patient Information",
    "type": "group",
    "item": [
      {
        "linkId": "1.1",
        "text": "Name",
        "type": "string",
        "required": true
      },
      {
        "linkId": "1.2",
        "text": "Date of birth",
        "type": "date"
      }
    ]
  },
  {
    "linkId": "2",
    "text": "Demographic Information",
    "type": "group",
    "item": [
      {
        "linkId": "2.1",
        "text": "Cats are better than dogs",
        "type": "boolean"
      },
      {
        "linkId": "2.2",
        "text": "Correct!",
        "type": "display",
        "enableWhen": [
          {
            "question": "2.1",
            "operator": "=",
            "answerBoolean": true
          }
        ]
      }
    ]
  }
]
```

There are [several options](https://www.hl7.org/fhir/valueset-item-type.html) for the `type` member of `item` objects. The Structured Data Capture Library selects the UI component to use when rendering based on the type. This example also uses the `group` type where `text` acts as section headers and child item objects are logically grouped.

Some Questionnaire elements control validation or rendering logic. For example, item `1.1` is required, and item `2.1.1` is only shown if item `2.1` is `true`.

The next example of an item object uses extensions from the SDC implementation guide and also demonstrates the `choice` type:

```json
...
{
  "linkId": "1.3",
  "text": "Gender",
  "type": "choice",
  "extension": [
    {
      "url": "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl",
      "valueCodeableConcept": {
        "coding": [
          {
            "system": "http://hl7.org/fhir/questionnaire-item-control",
            "code": "radio-button",
            "display": "Radio Button"
          }
        ]
      }
    },
    {
      "url" : "http://hl7.org/fhir/StructureDefinition/questionnaire-choiceOrientation",
      "valueCode" : "horizontal"
    }
  ],
  "answerOption": [
    {
      "valueCoding": {
        "code": "female",
        "display": "Female",
        "system": "http://hl7.org/fhir/gender-identity"
      }
    },
    {
      "valueCoding": {
        "code": "male",
        "display": "Male",
        "system": "http://hl7.org/fhir/gender-identity"
      }
    },
    {
      "valueCoding": {
        "code": "other",
        "display": "Other",
        "system": "http://hl7.org/fhir/gender-identity"
      }
    }
  ]
},
...
```

There are two extensions: the `questionnaire-itemControl` specifies that the `choice` type question should use radio buttons (and not a dropdown menu or checkboxes, for example), and the `questionnaire-choiceOrientation` says question options should be rendered horizontally.

### Popular features and extensions

#### Pagination

[Page](https://build.fhir.org/codesystem-questionnaire-item-control.html#questionnaire-item-control-page) item control on group items for paginated questionnaires. Note: the `page` control type is not yet part of a stable FHIR release, but has been added to the SDC Library due to developer demand.

Example:

```json
"item": [
  {
    "type": "group",
    "linkId": "pagedemo",
    "text": "Page title",
    "extension": [
      {
        "url": "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl",
        "valueCodeableConcept": {
          "coding": [
            {
              "system": "http://hl7.org/fhir/questionnaire-item-control",
              "code": "page",
              "display": "Page"
            }
          ],
          "text": "Page"
        }
      }
    ],
    "item": [ ... ]
  }
]
```

#### Value constraints

[Value constraints](http://hl7.org/fhir/uv/sdc/behavior.html#value-constraints) to add validation to responses. Currently supported: `maxLength`, `minLength`, `regex`, `minValue`, `maxValue`.

Example:

```json
"item": [
  {
    "type": "decimal",
    "linkId": "weightdemo",
    "text": "Weight, Measured",
    "extension": [
      {
        "url": "http://hl7.org/fhir/StructureDefinition/minValue",
        "valueDecimal": 1
      },
      {
        "url": "http://hl7.org/fhir/StructureDefinition/maxValue",
        "valueDecimal": 100
      },
      {
        "url": "http://hl7.org/fhir/StructureDefinition/maxDecimalPlaces",
        "valueInteger": 2
      }
    ]
  }
]
```

#### Conditional display

Conditional display using [enableWhen](http://hl7.org/fhir/uv/sdc/behavior.html#enableWhen) and [enableWhenExpression](http://hl7.org/fhir/uv/sdc/expressions.html#enableWhenExpression).

Example:

```json
"item": [
  {
    "linkId": "sex",
    "text": "Sex",
    "type": "choice",
    "answerOption": [
      {
        "valueCoding": {
          "code": "male",
          "display": "Male"
        }
      },
      {
        "valueCoding": {
          "code": "female",
          "display": "Female"
        }
      }
    ]
  },
  {
    "linkId": "birthdate",
    "text": "Birth date"
    "type": "date",
  },
  {
    "linkId" : "example1",
    "text": "Shown only if female",
    "type": "text",
    "enableWhen": [
      {
        "question" : "sex",
        "operator": "=",
        "answerCoding": {
          "code": "female"
        }
      }
    ]
  },
  {
    "linkId" : "example2",
    "text": "Shown only if female over 40 years old",
    "type": "text",
    "extension": [
      {
        "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-enableWhenExpression",
        "valueExpression": {
          "description": "female over 40",
          "language": "text/fhirpath",
          "expression": "%resource.repeat(item).where(linkId='sex').answer.value.code ='female' and today().toString().substring(0, 4).toInteger() - %resource.repeat(item).where(linkId='birthdate').answer.value.toString().substring(0, 4).toInteger() >= 40"
        }
      }
    ],
  }
]
```

#### Autocomplete

[Autocomplete](http://hl7.org/fhir/R4/codesystem-questionnaire-item-control.html#questionnaire-item-control-autocomplete) for choice-type questionnaire item.

Example:

```json
"item": [
  {
    "linkId": "state"
    "text": "US State",
    "type": "choice",
    "extension": [
      {
        "url": "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl",
        "valueCodeableConcept": {
          "coding": [
            {
              "system": "http://hl7.org/fhir/questionnaire-item-control",
              "code": "autocomplete",
              "display": "autocomplete"
            }
          ]
        }
      }
    ],
    "answerOption": [
      {
        "valueCoding": {
          "code": "AL",
          "display": "Alabama"
        }
      },
      {
        "valueCoding": {
          "code": "AK",
          "display": "Alaska"
        }
      },
      {
        "valueCoding": {
          "code": "AS",
          "display": "American Samoa"
        }
      },
      {
        "valueCoding": {
          "code": "AZ",
          "display": "Arizona"
        }
      },
      {
        "valueCoding": {
          "code": "AR",
          "display": "Arkansas"
        }
      },
      ...
    ]
  }
]
```

## Data extraction and population

Mapping FHIR QuestionnaireResponses to other FHIR resources (and back) allows the structured data capture process to be more tightly integrated with clinical workflows.

For example, if your application has a questionnaire for new patient registration, your ultimate goal may be to create a [FHIR Patient resource](https://www.hl7.org/fhir/patient.html) based on the answers provided to use in your application. Or, if your application has a questionnaire for entering test results, you could create a FHIR Observation resource. The process of mapping a FHIR QuestionnaireResponse to one or more other FHIR resources is called [extraction](http://hl7.org/fhir/uv/sdc/extraction.html).

On the other hand, you may want to reduce data entry by loading values from existing FHIR resources into your questionnaire. For example, if a questionnaire asks for a patient's name and age, you can pre-populate that information from an existing FHIR Patient resource. The process of mapping one or more FHIR resources to a FHIR QuestionnaireResponse is called [population](http://hl7.org/fhir/uv/sdc/populate.html).

### Definition-based extraction

A questionnaire using [definition-based extraction](http://hl7.org/fhir/uv/sdc/extraction.html#definition-based-extraction) includes the [questionnaire-itemExtractionContext](http://hl7.org/fhir/uv/sdc/StructureDefinition-sdc-questionnaire-itemExtractionContext.html) extension to identify the FHIR resource to extract, and fill in the `Questionnaire.item.definition` to specify the resource or profile element that Questionnaire item corresponds to:

```json
{
  "resourceType": "Questionnaire",
  "status": "draft",
  "extension": [
    {
      "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemExtractionContext",
      "valueExpression": {
        "language": "application/x-fhir-query",
        "expression": "Patient",
        "name": "patient"
      }
    }
  ],
  "item": [
    {
      "linkId": "PR",
      "type": "group",
      "item": [
        {
          "linkId": "PR-name",
          "type": "group",
          "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.name",
          "item": [
            {
              "linkId": "PR-name-given",
              "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.name.given",
              "type": "string",
              "text": "First Name"
            },
            {
              "linkId": "PR-name-family",
              "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.name.family",
              "type": "string",
              "text": "Family Name"
            }
          ]
        },
        {
          "linkId": "PR-birthdate",
          "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.birthDate",
          "type": "date",
          "text": "Date of Birth"
        },
        {
          "linkId": "PR-id",
          "type": "group",
          "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.identifier",
          "item": [
            {
              "extension" : [
                {
                  "url" : "http://hl7.org/fhir/StructureDefinition/questionnaire-hidden",
                  "valueBoolean" : true
                },
                {
                  "url" : "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression",
                  "valueString" : "http://example.org/mrn"
                }
              ],
              "linkId": "PR-name-id-url",
              "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.identifier.system",
              "type": "string"
            },
            {
              "linkId": "PR-name-id",
              "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.identifier.value",
              "type": "string",
              "text": "Patient Id"
            }
          ]
        }
      ]
    }
  ]
}
```

This example extracts to a Patient resource. In order to extract to a Patient.identifier, it includes the hidden `PR-name-id-url` item to populate the `Patient.identifier.system` element programmatically.

One major limitation of expression-based extraction is that the questionnaire must be structured the same as the resource you're extracting to. For example, there is no simple way to extract answers from a single group to multiple different FHIR resources.

See the [SDC implementation guide on definition-based extraction](http://hl7.org/fhir/uv/sdc/extraction.html#definition-based-extraction) for more information.

### StructureMap-based extraction

A questionnaire using [StructureMap-based extraction](http://hl7.org/fhir/uv/sdc/extraction.html#structuremap-based-extraction) includes the `sdc-questionnaire-targetStructureMap` extension specifying the structure map to use when transforming the QuestionnaireResponse to other FHIR resources.

```json
{
  "resourceType": "Questionnaire",
  "status": "active",
  "extension": [
    {
      "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-targetStructureMap",
      "valueCanonical": "http://example.org/fhir/StructureMap/PatientRegistration"
    }
  ],
  "item": [
    {
      "linkId": "PR",
      "type": "group",
      "item": [
        {
          "linkId": "PR-name",
          "type": "group",
          "item": [
            {
              "linkId": "PR-name-given",
              "type": "string",
              "text": "First Name"
            },
            {
              "linkId": "PR-name-family",
              "type": "string",
              "text": "Family Name"
            }
          ]
        },
        {
          "linkId": "PR-birthdate",
          "type": "date",
          "text": "Date of Birth"
        },
        {
          "linkId": "PR-name-id",
          "type": "string",
          "text": "Patient Id"
        }
      ]
    }
  ]
}
```

Structure maps are typically authored in the [FHIR mapping language](http://hl7.org/fhir/R4/mapping-language.html), although they can also be represented by the [StructureMap resource](http://hl7.org/fhir/R4/structuremap.html):

```
map "http://example.org/fhir/StructureMap/PatientRegistration" = 'PatientRegistration'
uses "http://hl7.org/fhir/StructureDefinition/QuestionnaireReponse" as source
uses "http://hl7.org/fhir/StructureDefinition/Bundle" as target

group PatientRegistration(source src : QuestionnaireResponse, target bundle: Bundle) {
  src -> bundle.id = uuid() "rule_bundle_id";
  src -> bundle.type = 'collection' "rule_bundle_type";
  src -> bundle.entry as entry, entry.resource = create('Patient') as patient then
    ExtractPatient(src, patient) "rule_extract_patient";
}

group ExtractPatient(source src : QuestionnaireResponse, target tgt : Patient) {
  src.item as item where(linkId = 'PR') then {
    item.item as inner_item where (linkId = 'patient-0-birth-date') then {
      inner_item.answer first as ans then {
        ans.value as val -> tgt.birthDate = val "rule_birthdate";
      };
    };

    item.item as inner_item where (linkId = 'PR-name-id') then {
      inner_item.answer first as ans -> tgt.identifier = create('Identifier') as id then {
        ans.value -> id.system = 'http://example.org/mrn' "rule_name_id_code";
        ans.value as val -> id.value = val "rule_name_id_val";
      };
    };

    item.item as nameItem where(linkId = 'PR-name') -> tgt.name = create('HumanName') as patientName then {
      nameItem.item as famItem where (linkId = 'PR-name-family') then {
        famItem.answer first as family then {
          family.value as val -> patientName.family = val "rule_name_family";
        };
      };
      src -> patientName.given = evaluate(nameItem, ${"$"}this.item.where(linkId = 'PR-name-given').answer.value) "rule_name_given";
    };
  };
}
```

The `PatientRegistration` group creates a Bundle to contain the other resources, which is required by the Structured Data Capture library, then creates an empty Patient and continues to `ExtractPatient`. Most of the example simply searches for items by `linkId` and then navigates the questionnaire's `item` data structure to set Patient element values. For the `Patient.name` element, the example uses the same strategy for the extraction rule `rule_name_family`, but also demonstrates using the [FHIRPath language](https://hl7.org/fhirpath/) to navigate and search the questionnaire structure with the extraction rule `rule_name_given`.

Relies on HAPI FHIR implementation which does not support all features of mapping language, mostly convenience features so if it doesn't work try a more verbose form.

See the [FHIR mapping language specification](http://hl7.org/fhir/R4/mapping-language.html) and [FHIR mapping language tutorial](http://hl7.org/fhir/R4/mapping-tutorial.html) for more information.

The [online fhirpath.js demo](https://hl7.github.io/fhirpath.js/) is useful for learning and iterating on [FHIRPath](https://hl7.org/fhirpath/) expressions.

### Expression-based population

A questionnaire using [expression-based population](http://hl7.org/fhir/uv/sdc/populate.html#expression-based-population) primarily relies on the `sdc-questionnaire-initialExpression` extension to specify the starting value for each questionnaire item:

```json
{
  "resourceType": "Questionnaire",
  "status": "active",
  "item": [
    {
      "linkId": "PR",
      "type": "group",
      "item": [
        {
          "extension" : [
            {
              "url" : "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression",
              "valueExpression" : {
                "language" : "text/fhirpath",
                "expression" : "Patient.name.first().select(given.first() + ' ' + family.first())"
              }
            }
          ],
          "linkId": "PR-name",
          "type": "string",
          "text": " Full name"
        },
        {
          "extension" : [
            {
              "url" : "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression",
              "valueExpression" : {
                "language" : "text/fhirpath",
                "expression" : "Patient.birthDate"
              }
            }
          ],
          "linkId": "PR-birthdate",
          "type": "date",
          "text": "Date of Birth"
        },
        {
          "extension" : [
            {
              "url" : "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression",
              "valueExpression" : {
                "language" : "text/fhirpath",
                "expression" : "Patient.identifier.where(system='http://example.org/mrn').value"
              }
            }
          ],
          "linkId": "PR-name-id",
          "type": "string",
          "text": "Patient Id"
        }
      ]
    }
  ]
}
```

Some external examples use FHIR search queries (where `language` is `"application/x-fhir-query"`) - these are not well supported by the Structured Data Capture library as you must directly pass all resources needed for population.

You can test expression-based population for the example questionnaire with this Patient resource:

```json
{
  "resourceType": "Patient",
  "identifier": [
    {
      "system": "http://example.org/mrn",
      "value": "abcd-efgh-ijkl-mnop"
    }
  ],
  "active": true,
  "name": [
    {
      "family": "Ali",
      "given": [
        "Salman"
      ]
    }
  ],
  "birthDate": "1968-09-17"
}
```
