package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.SubstanceProtein

public object SubstanceProteinConverter {
  public fun SubstanceProtein.toHapi(): org.hl7.fhir.r4.model.SubstanceProtein {
    val hapiValue = org.hl7.fhir.r4.model.SubstanceProtein()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSequenceType(sequenceType.toHapi())
    hapiValue.setNumberOfSubunitsElement(numberOfSubunits.toHapi())
    hapiValue.setDisulfideLinkage(disulfideLinkageList.map{it.toHapi()})
    hapiValue.setSubunit(subunitList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.SubstanceProtein.toProto(): SubstanceProtein {
    val protoValue = SubstanceProtein.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSequenceType(sequenceType.toProto())
    .setNumberOfSubunits(numberOfSubunitsElement.toProto())
    .addAllDisulfideLinkage(disulfideLinkage.map{it.toProto()})
    .addAllSubunit(subunit.map{it.toProto()})
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.SubstanceProtein.SubstanceProteinSubunitComponent.toProto():
      SubstanceProtein.Subunit {
    val protoValue = SubstanceProtein.Subunit.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSubunit(subunitElement.toProto())
    .setSequence(sequenceElement.toProto())
    .setLength(lengthElement.toProto())
    .setSequenceAttachment(sequenceAttachment.toProto())
    .setNTerminalModificationId(nTerminalModificationId.toProto())
    .setNTerminalModification(nTerminalModificationElement.toProto())
    .setCTerminalModificationId(cTerminalModificationId.toProto())
    .setCTerminalModification(cTerminalModificationElement.toProto())
    .build()
    return protoValue
  }

  private fun SubstanceProtein.Subunit.toHapi():
      org.hl7.fhir.r4.model.SubstanceProtein.SubstanceProteinSubunitComponent {
    val hapiValue = org.hl7.fhir.r4.model.SubstanceProtein.SubstanceProteinSubunitComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSubunitElement(subunit.toHapi())
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setLengthElement(length.toHapi())
    hapiValue.setSequenceAttachment(sequenceAttachment.toHapi())
    hapiValue.setNTerminalModificationId(nTerminalModificationId.toHapi())
    hapiValue.setNTerminalModificationElement(nTerminalModification.toHapi())
    hapiValue.setCTerminalModificationId(cTerminalModificationId.toHapi())
    hapiValue.setCTerminalModificationElement(cTerminalModification.toHapi())
    return hapiValue
  }
}
