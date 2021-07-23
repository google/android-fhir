package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicinalProductInteraction
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Type

public object MedicinalProductInteractionConverter {
  public
      fun MedicinalProductInteraction.Interactant.ItemX.medicinalProductInteractionInteractantItemToHapi():
      Type {
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    throw
        IllegalArgumentException("Invalid Type for MedicinalProductInteraction.interactant.item[x]")
  }

  public fun Type.medicinalProductInteractionInteractantItemToProto():
      MedicinalProductInteraction.Interactant.ItemX {
    val protoValue = MedicinalProductInteraction.Interactant.ItemX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    return protoValue.build()
  }

  public fun MedicinalProductInteraction.toHapi():
      org.hl7.fhir.r4.model.MedicinalProductInteraction {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductInteraction()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSubject(subjectList.map{it.toHapi()})
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setInteractant(interactantList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setEffect(effect.toHapi())
    hapiValue.setIncidence(incidence.toHapi())
    hapiValue.setManagement(management.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.MedicinalProductInteraction.toProto():
      MedicinalProductInteraction {
    val protoValue = MedicinalProductInteraction.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllSubject(subject.map{it.toProto()})
    .setDescription(descriptionElement.toProto())
    .addAllInteractant(interactant.map{it.toProto()})
    .setType(type.toProto())
    .setEffect(effect.toProto())
    .setIncidence(incidence.toProto())
    .setManagement(management.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.MedicinalProductInteraction.MedicinalProductInteractionInteractantComponent.toProto():
      MedicinalProductInteraction.Interactant {
    val protoValue = MedicinalProductInteraction.Interactant.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setItem(item.medicinalProductInteractionInteractantItemToProto())
    .build()
    return protoValue
  }

  public fun MedicinalProductInteraction.Interactant.toHapi():
      org.hl7.fhir.r4.model.MedicinalProductInteraction.MedicinalProductInteractionInteractantComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicinalProductInteraction.MedicinalProductInteractionInteractantComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setItem(item.medicinalProductInteractionInteractantItemToHapi())
    return hapiValue
  }
}
