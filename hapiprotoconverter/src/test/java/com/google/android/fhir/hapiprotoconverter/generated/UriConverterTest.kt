package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.PrimitiveTestData
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.common.truth.Truth
import com.google.fhir.r4.core.Uri
import kotlin.Any
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.UriType
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
public class UriConverterTest(
  private val hapi: UriType,
  private val proto: Uri
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
    public fun `data`(): List<Any> = PrimitiveTestData.URI_DATA
  }
}
