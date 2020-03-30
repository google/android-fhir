package com.google.fhirengine.db.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.google.fhir.r4.core.Id;
import com.google.fhir.shaded.common.base.Joiner;
import com.google.fhir.shaded.protobuf.Any;
import com.google.fhir.shaded.protobuf.Descriptors;
import com.google.fhir.shaded.protobuf.InvalidProtocolBufferException;
import com.google.fhir.shaded.protobuf.Message;
import com.google.fhirengine.db.Database;
import com.google.fhirengine.db.ResourceAlreadyExistsInDbException;
import com.google.fhirengine.db.ResourceNotFoundInDbException;

import javax.inject.Inject;

/** Helper class that manages the FHIR resource database, and provides a database connection. */
public class DatabaseImpl extends SQLiteOpenHelper implements Database {

  private static String DB_NAME = "FHIRDB";
  private static int DB_VERSION = 1;

  /** Table names */
  interface Tables {
    String RESOURCES = "resources";
  }

  /** {@link Tables#RESOURCES} columns. */
  interface ResourcesColumns extends BaseColumns {
    String RESOURCE_TYPE = "resource_type";
    String RESOURCE_ID = "resource_id";
    String RESOURCE = "resource";
  }

  /** Unique indices */
  private interface UniqueIndices {
    String RESOURCE_TYPE_RESOURCE_ID_UNIQUE_INDEX =
        Joiner.on("_")
            .join(Tables.RESOURCES, ResourcesColumns.RESOURCE_TYPE, ResourcesColumns.RESOURCE_ID);
  }

  private static String CREATE_RESOURCES_TABLE =
      "CREATE TABLE " + Tables.RESOURCES + " ( " +
          ResourcesColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
          ResourcesColumns.RESOURCE_TYPE + " TEXT NOT NULL," +
          ResourcesColumns.RESOURCE_ID + " INTEGER NOT NULL," +
          ResourcesColumns.RESOURCE + " BLOB NOT NULL);";
  private static String CREATE_INDEX =
      "CREATE UNIQUE INDEX " + UniqueIndices.RESOURCE_TYPE_RESOURCE_ID_UNIQUE_INDEX + " ON " +
          Tables.RESOURCES + " ( " +
          ResourcesColumns.RESOURCE_TYPE + ", " +
          ResourcesColumns.RESOURCE_ID + ")";

  @Inject
  DatabaseImpl(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(CREATE_RESOURCES_TABLE);
    sqLiteDatabase.execSQL(CREATE_INDEX);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

  @Override
  public <M extends Message> void insert(M resource) throws ResourceAlreadyExistsInDbException {
    String type = getResourceType(resource.getClass());
    String id = getResourceId(resource);
    ContentValues contentValues = new ContentValues();
    contentValues.put(ResourcesColumns.RESOURCE_TYPE, type);
    contentValues.put(ResourcesColumns.RESOURCE_ID, id);
    contentValues.put(ResourcesColumns.RESOURCE, Any.pack(resource).toByteArray());
    try {
      getWritableDatabase().insertOrThrow(Tables.RESOURCES, null, contentValues);
    } catch (SQLiteConstraintException e) {
      throw new ResourceAlreadyExistsInDbException(type, id, e);
    }
  }

  @Override
  public <M extends Message> void update(M resource) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

  @Override
  public <M extends Message> M select(Class<M> clazz, String id)
      throws ResourceNotFoundInDbException {
    String type = getResourceType(clazz);
    String[] columns = new String[]{ResourcesColumns.RESOURCE};
    String whereClause =
        ResourcesColumns.RESOURCE_TYPE + " = ? AND " + ResourcesColumns.RESOURCE_ID + " = ?";
    String[] whereArgs = new String[]{type, id};
    Cursor cursor = getReadableDatabase()
        .query(Tables.RESOURCES, columns, whereClause, whereArgs, null, null, null);
    try {
      if (cursor == null) {
        throw new SQLException("Null cursor!");
      }
      if (cursor.getCount() == 0) {
        throw new ResourceNotFoundInDbException(type, id);
      }
      if (cursor.getCount() > 1) {
        throw new SQLException("Unexpected number of records!");
      }
      cursor.moveToFirst();
      Any a = Any.parseFrom(cursor.getBlob(0));
      return a.unpack(clazz);
    } catch (InvalidProtocolBufferException e) {
      throw new SQLException("Deserialization error!", e);
    } finally {
      cursor.close();
    }
  }

  @Override
  public <M extends Message> void delete(Class<M> clazz, String id) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

  /** Returns the FHIR resource type. */
  private static <M extends Message> String getResourceType(Class<M> clazz) {
    return clazz.getName();
  }

  /** Returns the value of the id field of the FHIR resource. */
  private static <M extends Message> String getResourceId(M resource) {
    for (Descriptors.FieldDescriptor field : resource.getDescriptorForType().getFields()) {
      if (field.getName().equals("id")) {
        return ((Id) resource.getField(field)).getValue();
      }
    }
    throw new IllegalArgumentException("Missing ID!");
  }
}
