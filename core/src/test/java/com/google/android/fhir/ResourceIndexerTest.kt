package com.google.android.fhir

import android.os.Build
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.index.ResourceIndexer
import com.google.android.fhir.index.entities.DateIndex
import com.google.android.fhir.index.entities.NumberIndex
import com.google.android.fhir.index.entities.QuantityIndex
import com.google.android.fhir.index.entities.UriIndex
import com.google.android.fhir.resource.TestingUtils
import com.google.common.truth.Truth
import org.hl7.fhir.r4.model.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.math.BigDecimal

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ResourceIndexerTest {
    private lateinit var qtyTestSubstance: Substance
    private lateinit var qtyTestInvoice: Invoice
    private lateinit var uriTestQuestionnaire: Questionnaire
    private lateinit var dateTestPatient: Patient
    private lateinit var lastUpdatedTestPatient: Patient
    private lateinit var numberTestChargeItem: ChargeItem
    private lateinit var numberTestMolecularSequence: MolecularSequence

    @Before
    fun setUp() {
        val testingUtils = TestingUtils(FhirContext.forR4().newJsonParser())
        // TODO: Improve sample data reading. Current approach has a downside of failing all tests if
        // one file name is mistyped.
        qtyTestSubstance =
            testingUtils.readFromFile(Substance::class.java, "/quantity_test_substance.json")
        qtyTestInvoice =
            testingUtils.readFromFile(Invoice::class.java, "/quantity_test_invoice.json")
        uriTestQuestionnaire =
            testingUtils.readFromFile(Questionnaire::class.java, "/uri_test_questionnaire.json")
        dateTestPatient = testingUtils.readFromFile(Patient::class.java, "/date_test_patient.json")
        lastUpdatedTestPatient =
            testingUtils.readFromFile(Patient::class.java, "/lastupdated_ts_test_patient.json")
        numberTestChargeItem =
            testingUtils.readFromFile(ChargeItem::class.java, "/number_test_charge_item.json")
        numberTestMolecularSequence =
            testingUtils.readFromFile(
                MolecularSequence::class.java,
                "/number_test_molecular_sequence.json"
            )
    }

    @Test
    fun index_invoice_shouldIndexMoneyQuantity() {
        val resourceIndices = ResourceIndexer.index(qtyTestInvoice)
        Truth.assertThat(resourceIndices.quantityIndices)
            .containsAtLeast(
                // Search parameter names flatten camel case so "totalGross" becomes "totalgross"
                QuantityIndex(
                    "totalgross",
                    "Invoice.totalGross",
                    FHIR_CURRENCY_SYSTEM,
                    "EUR",
                    BigDecimal("48")
                ),
                QuantityIndex(
                    "totalnet",
                    "Invoice.totalNet",
                    FHIR_CURRENCY_SYSTEM,
                    "EUR",
                    BigDecimal("40.22")
                )
            )
    }

    @Test
    fun index_substance_shouldIndexQuantityQuantity() {
        val resourceIndices = ResourceIndexer.index(qtyTestSubstance)
        Truth.assertThat(resourceIndices.quantityIndices)
            .contains(
                QuantityIndex(
                    "quantity",
                    "Substance.instance.quantity",
                    "http://unitsofmeasure.org",
                    "mL",
                    BigDecimal("100")
                )
            )
    }

    @Test
    fun index_questionnaire_shouldIndexUri() {
        val resourceIndices = ResourceIndexer.index(uriTestQuestionnaire)
        Truth.assertThat(resourceIndices.uriIndices)
            .contains(UriIndex("url", "Questionnaire.url", "http://hl7.org/fhir/Questionnaire/3141"))
    }

    @Test
    fun index_patient_birthDate_shouldIndexBirthDate() {
        val resourceIndices = ResourceIndexer.index(dateTestPatient)
        val birthDateElement = dateTestPatient.getBirthDateElement()
        Truth.assertThat(resourceIndices.dateIndices)
            .contains(
                DateIndex(
                    "birthdate",
                    "Patient.birthDate",
                    birthDateElement.getValue().getTime(),
                    birthDateElement.getValue().getTime(),
                    birthDateElement.getPrecision()
                )
            )
    }

    @Test
    fun index_patient_lastUpdated_shouldIndexLastUpdated() {
        val resourceIndices = ResourceIndexer.index(lastUpdatedTestPatient)
        val lastUpdatedElement = lastUpdatedTestPatient.getMeta().getLastUpdatedElement()
        Truth.assertThat(resourceIndices.dateIndices)
            .contains(
                DateIndex(
                    "_lastUpdated",
                    "Patient.meta.lastUpdated",
                    lastUpdatedElement.value.getTime(),
                    lastUpdatedElement.getValue().getTime(),
                    lastUpdatedElement.getPrecision()
                )
            )
    }

    @Test
    fun index_chargeItem_shouldIndexFactorOverride() {
        val resourceIndices = ResourceIndexer.index(numberTestChargeItem)
        Truth.assertThat(resourceIndices.numberIndices)
            .contains(NumberIndex("factor-override", "ChargeItem.factorOverride", BigDecimal("0.8")))
    }

    @Test
    fun index_molecularSequence_shouldIndexWindowAndVariant() {
        val resourceIndices = ResourceIndexer.index(numberTestMolecularSequence)
        Truth.assertThat(resourceIndices.numberIndices)
            .containsAtLeast(
                NumberIndex(
                    "window-end",
                    "MolecularSequence.referenceSeq.windowEnd",
                    BigDecimal("22125510")
                ),
                NumberIndex(
                    "window-start",
                    "MolecularSequence.referenceSeq.windowStart",
                    BigDecimal("22125500")
                ),
                NumberIndex("variant-end", "MolecularSequence.variant.end", BigDecimal("22125504")),
                NumberIndex("variant-start", "MolecularSequence.variant.start", BigDecimal("22125503"))
            )
    }

    private companion object {
        // See: https://www.hl7.org/fhir/valueset-currencies.html
        const val FHIR_CURRENCY_SYSTEM = "urn:iso:std:iso:4217"
    }
}