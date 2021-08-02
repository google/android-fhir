package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.PrimitiveTestData
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
import com.google.common.truth.Truth
import com.google.fhir.r4.core.Date
import kotlin.Any
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateType
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
public class DateConverterTest(
  private val hapi: DateType,
  private val proto: Date
) {
  @Test
  public fun hapi(): Unit {
    Truth.assertThat(proto.toHapi().precision).isEquivalentAccordingToCompareTo(hapi.precision)
        Truth.assertThat(proto.toHapi().timeZone.id).isEqualTo(hapi.timeZone.id)
        Truth.assertThat(proto.toHapi().value).isEqualTo(hapi.value)
  }

  @Test
  public fun proto(): Unit {
    Truth.assertThat(hapi.toProto()).isEqualTo(proto)
  }

  public companion object {
    @Parameterized.Parameters
    @JvmStatic
    public fun `data`(): List<Any> = PrimitiveTestData.DATE_DATA
  }
}
