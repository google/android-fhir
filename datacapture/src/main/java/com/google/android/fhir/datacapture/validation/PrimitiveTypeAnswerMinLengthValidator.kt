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

package com.google.android.fhir.datacapture.validation

import android.content.Context
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.PrimitiveType

/**
 * A validator to check if the answer fulfills the minimum number of permitted characters.
 *
 * <p>Only the following primitive types are subjected to this validation:
 * 1. BooleanType
 * 2. DecimalType
 * 3. IntegerType
 * 4. DateType
 * 5. TimeType
 * 6. StringType
 * 7. UriType
 */
internal object PrimitiveTypeAnswerMinLengthValidator :
  ValueConstraintExtensionValidator(
    url = MIN_LENGTH_EXTENSION_URL,
    predicate = { extension, answer ->
      answer.value.isPrimitive &&
        (answer.value as PrimitiveType<*>).asStringValue().length <
          (extension.value as IntegerType).value
    },
    messageGenerator = { extension: Extension, _: Context ->
      ("The minimum number of characters that are permitted in the answer is: " +
        extension.value.primitiveValue())
    }
  )

internal const val MIN_LENGTH_EXTENSION_URL = "http://hl7.org/fhir/StructureDefinition/minLength"
