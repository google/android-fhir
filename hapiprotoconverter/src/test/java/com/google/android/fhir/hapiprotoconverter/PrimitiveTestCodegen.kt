/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.hapiprotoconverter

import android.annotation.SuppressLint
import com.google.common.truth.Truth
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.StructureDefinition
import com.google.fhir.r4.core.StructureDefinitionKindCode
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import java.util.Date as DateUtil
import kotlin.reflect.KClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/** Generate tests for classes generated in PrimitiveCodegen */
object PrimitiveTestCodegen {

  private const val protoPackage = "com.google.fhir.r4.core"
  private const val hapiPackage = "org.hl7.fhir.r4.model"
  val TIME_LIKE_TEST = listOf("date", "dateTime", "instant")

  @SuppressLint("DefaultLocale")
  fun generate(def: StructureDefinition, outLocation: File? = null) {
    require(def.kind.value == StructureDefinitionKindCode.Value.PRIMITIVE_TYPE) {
      "structure definition needs to be of type primitive"
    }
    val hapiName = "${def.id.value.capitalize()}Type"
    val protoName = def.id.value.capitalize()

    val fileBuilder =
      FileSpec.builder(
        "com.google.android.fhir.hapiprotoconverter.generated",
        "${protoName}ConverterTest"
      )
    val primitiveConverterClass =
      ClassName(
        "com.google.android.fhir.hapiprotoconverter.generated",
        def.id.value.capitalize() + "ConverterTest"
      )

    val testClass =
      TypeSpec.classBuilder(primitiveConverterClass.simpleName)
        .addAnnotation(
          AnnotationSpec.builder(RunWith::class)
            .addMember("%T::class", Parameterized::class)
            .build()
        )

    mutableListOf<FunSpec>()

    val parameterFunction =
      FunSpec.builder("data")
        .addAnnotation(Parameterized.Parameters::class)
        .addAnnotation(JvmStatic::class)
        .returns(List::class.parameterizedBy(Any::class))

    val toProtoBuilder = FunSpec.builder("proto").addAnnotation(Test::class)

    val toHapiBuilder = FunSpec.builder("hapi").addAnnotation(Test::class)

    when (def.id.value) {
      in TIME_LIKE_TEST -> {
        if (def.id.value == "date") fileBuilder.addAliasedImport(DateUtil::class, "DateUtil")
        toHapiBuilder.addStatement(
          "%T.assertThat(proto.%M().precision).isEquivalentAccordingToCompareTo(hapi.precision)",
          Truth::class,
          MemberName(
            "com.google.android.fhir.hapiprotoconverter.generated.${def.id.value.capitalize()}Converter",
            "toHapi"
          )
        )
        toHapiBuilder.addStatement(
          "%T.assertThat(proto.%M().timeZone.id).isEqualTo(hapi.timeZone.id)",
          Truth::class,
          MemberName(
            "com.google.android.fhir.hapiprotoconverter.generated.${def.id.value.capitalize()}Converter",
            "toHapi"
          )
        )
      }
      else -> {}
    }
    parameterFunction.addStatement(
      "return %T.%L_DATA",
      PrimitiveTestData::class,
      protoName.toUpperCase()
    )

    toProtoBuilder.addCode(
      "%T.assertThat(hapi.%M()).isEqualTo(proto)",
      Truth::class,
      MemberName(
        "com.google.android.fhir.hapiprotoconverter.generated.${def.id.value.capitalize()}Converter",
        "toProto"
      )
    )

    toHapiBuilder.addStatement(
      "%T.assertThat(proto.%M().value).isEqualTo(hapi.value)",
      Truth::class,
      MemberName(
        "com.google.android.fhir.hapiprotoconverter.generated.${def.id.value.capitalize()}Converter",
        "toHapi"
      )
    )
    val testClassConstructor = FunSpec.constructorBuilder()

    testClassConstructor.addConstructorParameter(
      "hapi",
      Class.forName("$hapiPackage.$hapiName"),
      testClass
    )
    testClassConstructor.addConstructorParameter(
      "proto",
      Class.forName("$protoPackage.$protoName"),
      testClass
    )

    fileBuilder
      .addType(
        testClass
          .primaryConstructor(testClassConstructor.build())
          .addFunction(toHapiBuilder.build())
          .addFunction(toProtoBuilder.build())
          .addType(TypeSpec.companionObjectBuilder().addFunction(parameterFunction.build()).build())
          .build()
      )
      .build()
      .writeTo(outLocation!!)
  }
}

private fun FunSpec.Builder.addConstructorParameter(
  s: String,
  kClass: KClass<*>,
  testClass: TypeSpec.Builder
) {
  addParameter(s, kClass)
  testClass.addProperty(PropertySpec.builder(s, kClass, KModifier.PRIVATE).initializer(s).build())
}

// because temporal precision enum is a java class
private fun FunSpec.Builder.addConstructorParameter(
  s: String,
  clazz: Class<*>,
  testClass: TypeSpec.Builder
) {
  this.addParameter(s, clazz)
  testClass.addProperty(PropertySpec.builder(s, clazz, KModifier.PRIVATE).initializer(s).build())
}

fun main() {
  for (x in
    listOf(
      "base64Binary",
      "boolean",
      "canonical",
      "code",
      "date",
      "dateTime",
      "decimal",
      "id",
      "instant",
      "integer",
      "markdown",
      "oid",
      "positiveInt",
      "string",
      "time",
      "unsignedInt",
      "uri",
      "url",
      "uuid"
    )) {
    val file = File("hapiprotoconverter\\src\\test\\java")
    // This is temp will parse files
    PrimitiveTestCodegen.generate(
      StructureDefinition.newBuilder()
        .setId(Id.newBuilder().setValue(x))
        .setKind(
          StructureDefinition.KindCode.newBuilder()
            .setValue(StructureDefinitionKindCode.Value.PRIMITIVE_TYPE)
        )
        .build(),
      outLocation = file
    )
  }
}
