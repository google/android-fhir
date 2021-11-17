package com.google.android.fhir.reference.testData

class AddPatientTestData {
    /*Constant Data declaration*/
    val phoneNumber: String = "1234567890"
    val gender: String = "Male"
    val city: String = "NAIROBI"
    val country: String = "KE"
    val isActive: String = "Is Active?"

    fun firstName(): String {
        return generateString()
    }

    fun familyName(): String{
        return generateString()
    }

    private fun generateString(): String {

        val charPool: List<Char> = ('a'..'z') + ('A'..'Z')
        val outputStrLength = (5..10).shuffled().first()

        return (1..outputStrLength)
            .map{ kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}