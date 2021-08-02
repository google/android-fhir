package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.PrimitiveTestData
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toProto
import com.google.common.truth.Truth
import com.google.fhir.r4.core.UnsignedInt
import kotlin.Any
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.UnsignedIntType
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
public class UnsignedIntConverterTest(
  private val hapi: UnsignedIntType,
  private val proto: UnsignedInt
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
    public fun `data`(): List<Any> = PrimitiveTestData.UNSIGNEDINT_DATA
  }
}
