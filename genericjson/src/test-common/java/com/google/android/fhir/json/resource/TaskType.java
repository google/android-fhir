package com.google.android.fhir.json.resource;

public enum TaskType {
  CREATE_BENEFICIARY,
  MOVE_BENEFICIARY;
  public static TaskType parse(String type) {
    if (type.compareToIgnoreCase("CREATE_BENEFICIARY") == 0) {
      return TaskType.CREATE_BENEFICIARY;
    } else if (type.compareToIgnoreCase("MOVE_BENEFICIARY") == 0) {
      return TaskType.MOVE_BENEFICIARY;
    } else {
      throw new IllegalArgumentException("Invalid Task Type : " + type);
    }
  }
}
