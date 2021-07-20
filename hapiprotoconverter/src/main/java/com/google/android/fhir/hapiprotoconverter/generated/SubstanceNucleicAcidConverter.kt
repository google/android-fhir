package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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
import com.google.fhir.r4.core.SubstanceNucleicAcid

public object SubstanceNucleicAcidConverter {
  public fun SubstanceNucleicAcid.toHapi(): org.hl7.fhir.r4.model.SubstanceNucleicAcid {
    val hapiValue = org.hl7.fhir.r4.model.SubstanceNucleicAcid()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSequenceType(sequenceType.toHapi())
    hapiValue.setNumberOfSubunitsElement(numberOfSubunits.toHapi())
    hapiValue.setAreaOfHybridisationElement(areaOfHybridisation.toHapi())
    hapiValue.setOligoNucleotideType(oligoNucleotideType.toHapi())
    hapiValue.setSubunit(subunitList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.SubstanceNucleicAcid.toProto(): SubstanceNucleicAcid {
    val protoValue = SubstanceNucleicAcid.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSequenceType(sequenceType.toProto())
    .setNumberOfSubunits(numberOfSubunitsElement.toProto())
    .setAreaOfHybridisation(areaOfHybridisationElement.toProto())
    .setOligoNucleotideType(oligoNucleotideType.toProto())
    .addAllSubunit(subunit.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceNucleicAcid.SubstanceNucleicAcidSubunitComponent.toProto():
      SubstanceNucleicAcid.Subunit {
    val protoValue = SubstanceNucleicAcid.Subunit.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSubunit(subunitElement.toProto())
    .setSequence(sequenceElement.toProto())
    .setLength(lengthElement.toProto())
    .setSequenceAttachment(sequenceAttachment.toProto())
    .setFivePrime(fivePrime.toProto())
    .setThreePrime(threePrime.toProto())
    .addAllLinkage(linkage.map{it.toProto()})
    .addAllSugar(sugar.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceNucleicAcid.SubstanceNucleicAcidSubunitLinkageComponent.toProto():
      SubstanceNucleicAcid.Subunit.Linkage {
    val protoValue = SubstanceNucleicAcid.Subunit.Linkage.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setConnectivity(connectivityElement.toProto())
    .setIdentifier(identifier.toProto())
    .setName(nameElement.toProto())
    .setResidueSite(residueSiteElement.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.SubstanceNucleicAcid.SubstanceNucleicAcidSubunitSugarComponent.toProto():
      SubstanceNucleicAcid.Subunit.Sugar {
    val protoValue = SubstanceNucleicAcid.Subunit.Sugar.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setIdentifier(identifier.toProto())
    .setName(nameElement.toProto())
    .setResidueSite(residueSiteElement.toProto())
    .build()
    return protoValue
  }

  public fun SubstanceNucleicAcid.Subunit.toHapi():
      org.hl7.fhir.r4.model.SubstanceNucleicAcid.SubstanceNucleicAcidSubunitComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceNucleicAcid.SubstanceNucleicAcidSubunitComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSubunitElement(subunit.toHapi())
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setLengthElement(length.toHapi())
    hapiValue.setSequenceAttachment(sequenceAttachment.toHapi())
    hapiValue.setFivePrime(fivePrime.toHapi())
    hapiValue.setThreePrime(threePrime.toHapi())
    hapiValue.setLinkage(linkageList.map{it.toHapi()})
    hapiValue.setSugar(sugarList.map{it.toHapi()})
    return hapiValue
  }

  public fun SubstanceNucleicAcid.Subunit.Linkage.toHapi():
      org.hl7.fhir.r4.model.SubstanceNucleicAcid.SubstanceNucleicAcidSubunitLinkageComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceNucleicAcid.SubstanceNucleicAcidSubunitLinkageComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setConnectivityElement(connectivity.toHapi())
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setResidueSiteElement(residueSite.toHapi())
    return hapiValue
  }

  public fun SubstanceNucleicAcid.Subunit.Sugar.toHapi():
      org.hl7.fhir.r4.model.SubstanceNucleicAcid.SubstanceNucleicAcidSubunitSugarComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SubstanceNucleicAcid.SubstanceNucleicAcidSubunitSugarComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setResidueSiteElement(residueSite.toHapi())
    return hapiValue
  }
}
