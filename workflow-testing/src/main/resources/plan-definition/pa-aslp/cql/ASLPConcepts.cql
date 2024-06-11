library ASLPConcepts version '1.0.000'

// Code Systems


codesystem "ICD-10": 'http://hl7.org/fhir/sid/icd-10'
codesystem "SNOMED-CT": 'http://snomed.info/sct'
codesystem "LOINC": 'http://loinc.org'
codesystem "RxNorm": 'http://www.nlm.nih.gov/research/umls/rxnorm'
codesystem "CPT": 'http://www.ama-assn.org/go/cpt'
codesystem "HCPCS": 'https://www.cms.gov/Medicare/Coding/HCPCSReleaseCodeSets'
codesystem "CIEL": 'http://hl7.org/fhir/sid/ciel'
codesystem "ICD-11": 'http://hl7.org/fhir/sid/icd-11'
codesystem "ICHI": 'https://mitel.dimi.uniud.it/ichi/#http://id.who.int/ichi'
codesystem "ICF": 'http://hl7.org/fhir/sid/icf-nl'
codesystem "NDC": 'http://hl7.org/fhir/sid/ndc'
codesystem "NIDA": 'https://cde.drugabuse.gov'
codesystem "ASLP Codes": 'http://example.org/sdh/dtr/aslp/CodeSystem/aslp-codes'

// Value Sets


valueset "Home Based Testing Sleep Studies Codes": 'http://example.org/sdh/dtr/aslp/ValueSet/aslp-a1-de2'
valueset "Facility Based Testing Sleep Studies Codes": 'http://example.org/sdh/dtr/aslp/ValueSet/aslp-a1-de9'
valueset "Sleep Study Codes Grouper": 'http://example.org/sdh/dtr/aslp/ValueSet/aslp-a1-de1-codes-grouper'
valueset "Diagnosis of Obstructive Sleep Apnea Codes": 'http://example.org/sdh/dtr/aslp/ValueSet/aslp-a1-de17'

// Codes


code "History of Hypertension": 'ASLP.A1.DE18' from "ASLP Codes" display 'History of Hypertension'
code "History of Diabetes": 'ASLP.A1.DE19' from "ASLP Codes" display 'History of Diabetes'
code "Neck Circumference": 'ASLP.A1.DE23' from "ASLP Codes" display 'Neck Circumference'
code "Height": 'ASLP.A1.DE20' from "ASLP Codes" display 'Body height'
code "Weight": 'ASLP.A1.DE21' from "ASLP Codes" display 'Weight'
code "BMI": 'ASLP.A1.DE22' from "ASLP Codes" display 'BMI'