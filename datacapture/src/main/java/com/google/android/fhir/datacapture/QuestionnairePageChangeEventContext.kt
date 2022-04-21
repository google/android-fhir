package com.google.android.fhir.datacapture

import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem

class QuestionnairePageChangeEventContext {
    private var pageChangeStrategy : PageChangeStrategy = DefaultPageChangeStrategy()

    fun setStrategy(pageChangeStrategy : PageChangeStrategy ){
        this.pageChangeStrategy = pageChangeStrategy
    }

    //This function will be called in the QuestionnaireViewModel goToNextPage and if returns true  then pageFlow.value will be changed.
    fun pageNextEvent( list: List<QuestionnaireItemViewItem> ):Boolean{
        return pageChangeStrategy.shouldGoToNextPage(list)
    }

    //This function will be called in the QuestionnaireViewModel goToPreviousPage and if returns true  then pageFlow.value will be changed.
    fun pagePreviousEvent( list: List<QuestionnaireItemViewItem> ):Boolean{
        return pageChangeStrategy.shouldGoToPreviousPage(list)
    }
}

interface PageChangeStrategy {
    fun shouldGoToPreviousPage(list: List<QuestionnaireItemViewItem>): Boolean

    fun shouldGoToNextPage(list: List<QuestionnaireItemViewItem>): Boolean

}