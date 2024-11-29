# ActivityFlow

![Activity Flow](activity_flow.svg)

The `ActivityFlow` class manages the workflow of clinical recommendations according to the [FHIR Clinical Practice Guidelines (CPG) specification](https://build.fhir.org/ig/HL7/cqf-recommendations/activityflow.html#activity-lifecycle---request-phases-proposal-plan-order). It implements an activity flow as defined in the FHIR CPG IG, allowing you to guide clinical recommendations through various phases (proposal, plan, order, perform). 

You can start new workflows with an appropriate request resource from the generated [CarePlan](Generate-A-Care-Plan.md) or resume existing ones from any phase.

**Important Considerations:**

* **Thread Safety:** The `ActivityFlow` is not thread-safe. Concurrent changes from multiple threads may lead to unexpected behavior.
* **Blocking Operations:** Some methods of `ActivityFlow` and its associated `Phase` interface may block the caller thread. Ensure these are called from a worker thread to avoid UI freezes.

## Creating an ActivityFlow

Use the appropriate `ActivityFlow.of()` factory function to create an instance. You can start anew flow with a `CPGRequestResource` or resume an existing flow from a `CPGRequestResource` or `CPGEventResource` based on the current state.

**Example:**
```kotlin 
val repository = FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine)
val request = CPGMedicationRequest( medicationRequestGeneratedByCarePlan)
val flow = ActivityFlow.of(repository,  request)
```

## Navigating phases

An `ActivityFlow` progresses through a series of phases, represented by the `Phase` class. Access the current phase using `getCurrentPhase()`.

**Example:**
```kotlin
 when (val phase = flow.getCurrentPhase( )) {
 is Phase.ProposalPhase -> { /* Handle proposal phase */ }
 is Phase.PlanPhase -> { /* Handle plan phase */ }
  // ... other phases 
}
```

## Transitioning between the phases

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

## Updating states in a phase

* **`RequestPhase`:** (`ProposalPhase`, `PlanPhase`, `OrderPhase`) allows updating the request state using `update()`.
```kotlin
proposalPhase.update(
  proposalPhase.getRequestResource().apply { setStatus(Status.ACTIVE) }
)
```
* **`EventPhase`:** (`PerformPhase`) allows updating the event state using `update()` and completing the phase using `complete()`.
```kotlin
performPhase.update(
  performPhase.getEventResource().apply {  setStatus(EventStatus.COMPLETED) }
)
```
## API List
### Factory functions

* `ActivityFlow.of(...)`: Various overloads for creating `ActivityFlow` instances with different resource types. Refer to the code for specific usage.

### Phase transition API

* `preparePlan()`: Prepares a plan resource.
* `initiatePlan(...)`: Initiates the plan phase.
* `prepareOrder()`: Prepares an order resource.
* `initiateOrder(...)`: Initiates the order phase.
* `preparePerform(...)`: Prepares an event resource for the perform phase.
* `initiatePerform(...)`: Initiates the perform phase.

### Other API
* `getCurrentPhase()`: Returns the current `Phase` of the workflow.

### Request phase API

* `getRequestResource()`: Returns a copy of resource.
* `update(..)`: Updates the resource.
* `suspend(..)`: Suspends the phase.
* `resume(..)`: Resumes the phase.
* `enteredInError(..)`: Marks the request entered-in-error.
* `reject(..)`: Rejects the phase.

### Event phase API

* `getEventResource()`: Returns a copy of resource.
* `update(..)`: Updates the resource.
* `suspend(..)`: Suspends the phase.
* `resume(..)`: Resumes the phase.
* `enteredInError(..)`: Marks the event entered-in-error.
* `start(..)`: Starts the event.
* `notDone(..)`: Marks the event not-done.
* `stop(..)`: Stop the event.
* `complete(..)`: Marks the event as complete.


## Supported activities
The library currently doesn't implement all of the activities outlined in the [activity profiles](https://build.fhir.org/ig/HL7/cqf-recommendations/profiles.html#activity-profiles). New activities may be added as per the requirement from the application developers.

| Activity           | Request                 | Event                 |
|--------------------|-------------------------|-----------------------|
| Send a message     | CPGCommunicationRequest | CPGCommunication      |
| Order a medication | CPGMedicationRequest    | CPGMedicationDispense |

## Additional resources

* [FHIR Clinical Practice Guidelines IG](https://build.fhir.org/ig/HL7/cqf-recommendations/)
* [Activity Flow](https://build.fhir.org/ig/HL7/cqf-recommendations/activityflow.html#activity-flow)
* [Activity Profiles](https://build.fhir.org/ig/HL7/cqf-recommendations/profiles.html#activity-profiles)