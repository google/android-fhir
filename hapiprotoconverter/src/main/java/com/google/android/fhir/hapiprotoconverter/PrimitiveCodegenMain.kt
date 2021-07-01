package com.google.android.fhir.hapiprotoconverter

import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.StructureDefinition
import java.io.File

fun main() {
    for (x in listOf(

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
        val file =
            File("hapiprotoconverter\\src\\main\\java")
        PrimitiveCodegen.generate(StructureDefinition.newBuilder().setId(Id.newBuilder().setValue(x)).build(),outLocation = file)
    }
}