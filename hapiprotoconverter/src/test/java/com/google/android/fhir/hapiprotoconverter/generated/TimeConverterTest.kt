package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.PrimitiveTestData
import com.google.android.fhir.hapiprotoconverter.generated.TimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimeConverter.toProto
import com.google.common.truth.Truth
import com.google.fhir.r4.core.Time
import kotlin.Any
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.TimeType
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
public class TimeConverterTest(
  private val hapi: TimeType,
  private val proto: Time
) {
  @Test
  public fun hapi(): Unit {
    Truth.assertThat(proto.toHapi().value).isEqualTo(hapi.value)
  }

  @Test
  public fun proto(): Unit {
    Truth.assertThat(hapi.toProto()).isEqualTo(proto)
  }

  public companion object {
    @Parameterized.Parameters
    @JvmStatic
    public fun `data`(): List<Any> = PrimitiveTestData.TIME_DATA
  }
}
