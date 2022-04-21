package com.google.android.fhir.datacapture

import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem

class DefaultPageChangeStrategy : PageChangeStrategy {

    override fun shouldGoToPreviousPage(list: List<QuestionnaireItemViewItem>): Boolean {
        return !list.any { it.isErrorTriggered }
    }

    override fun shouldGoToNextPage(list: List<QuestionnaireItemViewItem>): Boolean {
        return !list.any { it.isErrorTriggered }
    }

}