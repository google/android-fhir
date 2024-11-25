# ActivityFlow

The `ActivityFlow` class manages the workflow of clinical recommendations according to the [FHIR Clinical Practice Guidelines (CPG) specification](https://build.fhir.org/ig/HL7/cqf-recommendations/activityflow.html#activity-lifecycle---request-phases-proposal-plan-order). It implements an activity flow as defined in the FHIR CPG IG, allowing you to guide clinical recommendations through various phases (proposal, plan, order, perform). 

You can start new workflows with an appropriate request resource from the generated [CarePlan](Generate-A-Care-Plan.md) or resume existing ones from any phase.

**Important Considerations:**

* **Thread Safety:** The `ActivityFlow` is not thread-safe. Concurrent changes from multiple threads may lead to unexpected behavior.
* **Blocking Operations:** Some methods of `ActivityFlow` and its associated `Phase` interface may block the caller thread. Ensure these are called from a worker thread to avoid UI freezes.

## Creating an ActivityFlow

Use the appropriate `ActivityFlow.of()` factory function to create an instance. You can start anew flow with a `CPGRequestResource` or resume an existing flow from a `CPGRequestResource` or `CPGEventResource` based on the current state.

**Example:**
```kotlin 
val request = CPGMedicationRequest( medicationRequestGeneratedByCarePlan)
val flow = ActivityFlow.of(repository,  request)
```

## Navigating Phases

An `ActivityFlow` progresses through a series of phases, represented by the `Phase` class. Access the current phase using `getCurrentPhase()`.

**Example:**
```kotlin
 when (val phase = flow.getCurrentPhase( )) {
 is Phase.ProposalPhase -> { /* Handle proposal phase */ }
 is Phase.PlanPhase -> { /* Handle plan phase */ }
  // ... other phases 
}
```

## Transitioning Between Phases

`ActivityFlow` provides functions to prepare and initiate the next phase:

* **`prepare...()`:** Creates a new request or event for the next phase without persisting changes.
* **`initiate...()`:** Creates a new phase based on the provided request/event and persists changes to the repository.

**Example:**
```kotlin 
val preparedPlan = flow.preparePlan().getOrThrow( )
// ... modify preparedPlan
val planPhase = flow.initiatePlan(preparedPlan).getOrThrow( )
```

## Transitioning to Perform Phase

The `preparePerform()` function requires the event type as a parameter since the perform phase can create different event resources.

**Example:**
```kotlin 
val preparedPerformEvent = flow.preparePerform( CPGMedicationDispenseEvent::class.java).getOrThrow() 
 // ... update preparedPerformEvent 
 val performPhase = flow.initiatePerform( preparedPerformEvent). getOrThrow( )
```

## Updating States in a Phase

* **`RequestPhase`:** (`ProposalPhase`, `PlanPhase`, `OrderPhase`) allows updating the request state using `update()`.
* **`EventPhase`:** (`PerformPhase`) allows updating the event state using `update()` and completing the phase using `complete()`.

## Factory Functions

* `ActivityFlow.of(...)`: Various overloads for creating `ActivityFlow` instances with different resource types. Refer to the code for specific usage.

## Public Methods

* `getCurrentPhase()`: Returns the current `Phase` of the workflow.
* `preparePlan()`: Prepares a plan resource.
* `initiatePlan(...)`: Initiates the plan phase.
* `prepareOrder()`: Prepares an order resource.
* `initiateOrder(...)`: Initiates the order phase.
* `preparePerform(...)`: Prepares an event resource for the perform phase.
* `initiatePerform(...)`: Initiates the perform phase.

## Additional Resources

* [FHIR Clinical Practice Guidelines IG](https://build.fhir.org/ig/HL7/cqf-recommendations/)
* [Activity Flow](https://build.fhir.org/ig/HL7/cqf-recommendations/activityflow.html#activity-flow)
* [Activity Profiles](https://build.fhir.org/ig/HL7/cqf-recommendations/activityflow.html)