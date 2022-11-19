package com.google.android.fhir.implementationguide.npm;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.hl7.fhir.utilities.json.JsonTrackingParser;

public class JsonUtilities {

  public static JsonObject parse(String json) throws IOException {
    return JsonTrackingParser.parseJson(json);

  }

  public static JsonObject forceObject(JsonObject obj, String name) {
    if (obj.has(name) && obj.get(name).isJsonObject())
      return obj.getAsJsonObject(name);
    if (obj.has(name))
      obj.remove(name);
    JsonObject res = new JsonObject();
    obj.add(name, res);
    return res;
  }

  public static JsonArray forceArray(JsonObject obj, String name) {
    if (obj.has(name) && obj.get(name).isJsonArray())
      return obj.getAsJsonArray(name);
    if (obj.has(name))
      obj.remove(name);
    JsonArray res = new JsonArray();
    obj.add(name, res);
    return res;  }

  public static JsonObject addObj(JsonArray arr) {
    JsonObject res = new JsonObject();
    arr.add(res);
    return res;
  }

  public static JsonObject findByStringProp(JsonArray arr, String prop, String value) {
    for (JsonElement e : arr) {
      JsonObject obj = (JsonObject) e;
      if (obj.has(prop) && obj.get(prop).getAsString().equals(value))
        return obj;
    }
    return null;
  }

  public static String str(JsonObject json, String name) {
    JsonElement e = json.get(name);
    return e == null || e instanceof JsonNull ? null : e.getAsString();
  }

  public static boolean bool(JsonObject json, String name) {
    JsonElement e = json.get(name);
    return e == null || e instanceof JsonNull ? false : e.getAsBoolean();
  }

  public static String str(JsonObject json, String name1, String name2) {
    JsonElement e = json.get(name1);
    if (e == null)
      e = json.get(name2);
    return e == null ? null : e instanceof JsonNull ? null :  e.getAsString();
  }

  public static boolean has(JsonObject json, String name1, String name2) {
    return json.has(name1) || json.has(name2);
  }

  public static List<JsonObject> objects(JsonObject json, String name) {
    List<JsonObject> res = new ArrayList<>();
    if (json.has(name))
      for (JsonElement e : json.getAsJsonArray(name))
        if (e instanceof JsonObject)
          res.add((JsonObject) e);
    return res;
  }

  public static void merge(JsonObject source, JsonObject target) {
    for (Entry<String, JsonElement> pp : source.entrySet()) {
      if (target.has(pp.getKey())) {
        JsonElement te = target.get(pp.getKey());
        if (te.isJsonObject() && pp.getValue().isJsonObject()) {
          merge(te.getAsJsonObject(), pp.getValue().getAsJsonObject());
        } else {
          target.remove(pp.getKey());
          target.add(pp.getKey(), pp.getValue());
        }
      } else {
        target.add(pp.getKey(), pp.getValue());
      }
    }
  }

  public static String type(JsonElement e) {
    if (e == null) {
      return "(null)";
    }
    if (e.isJsonObject()) {
      return "Object";
    }
    if (e.isJsonArray()) {
      return "Array";
    }
    if (e.isJsonNull()) {
      return "Null";
    }
    JsonPrimitive p = (JsonPrimitive) e;
    if (p.isBoolean()) {
      return "Boolean";
    }
    if (p.isNumber()) {
      return "Number";
    }
    return "String";
  }

  public static List<String> strings(JsonArray arr) {
    List<String> res = new ArrayList<String>();
    for (int i = 0; i < arr.size(); i++) {
      JsonElement n = arr.get(i);
      if (n.isJsonPrimitive()) {
        res.add(n.getAsString());
      }
    }
    return res;
  }

}