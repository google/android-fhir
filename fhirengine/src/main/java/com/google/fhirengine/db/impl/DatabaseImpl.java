// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.fhirengine.db.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.google.common.base.Joiner;
import com.google.fhirengine.db.Database;
import com.google.fhirengine.db.ResourceAlreadyExistsInDbException;
import com.google.fhirengine.db.ResourceNotFoundInDbException;
import com.google.fhirengine.index.CodeIndex;
import com.google.fhirengine.index.FhirIndexer;
import com.google.fhirengine.index.ReferenceIndex;
import com.google.fhirengine.index.ResourceIndices;
import com.google.fhirengine.index.StringIndex;
import com.google.fhirengine.resource.ResourceUtils;

import org.hl7.fhir.r4.model.Resource;

import javax.inject.Inject;

import ca.uhn.fhir.parser.IParser;

/** Helper class that manages the FHIR resource database, and provides a database connection. */
public class DatabaseImpl extends SQLiteOpenHelper implements Database {

  private static String DB_NAME = "FHIRDB";
  private static int DB_VERSION = 1;

  /** Table names */
  interface Tables {
    String RESOURCES = "resources";
    String STRING_INDICES = "string_indices";
    String REFERENCE_INDICES = "reference_indices";
    String CODE_INDICES = "code_indices";
  }

  /** {@link Tables#RESOURCES} columns. */
  interface ResourcesColumns extends BaseColumns {
    String RESOURCE_TYPE = "resource_type";
    String RESOURCE_ID = "resource_id";
    String RESOURCE = "resource";
  }

  /** {@link Tables#STRING_INDICES} columns. */
  interface StringIndicesColumns extends BaseColumns {
    String RESOURCE_TYPE = "resource_type";
    String INDEX_NAME = "index_name";
    String INDEX_PATH = "index_path";
    String INDEX_VALUE = "index_value";
    String RESOURCE_ID = "resource_id";
  }

  /** {@link Tables#REFERENCE_INDICES} columns. */
  interface ReferenceIndicesColumns extends BaseColumns {
    String RESOURCE_TYPE = "resource_type";
    String INDEX_NAME = "index_name";
    String INDEX_PATH = "index_path";
    String INDEX_VALUE = "index_value";
    String RESOURCE_ID = "resource_id";
  }

  /** {@link Tables#CODE_INDICES} columns. */
  interface CodeIndicesColumns extends BaseColumns {
    String RESOURCE_TYPE = "resource_type";
    String INDEX_NAME = "index_name";
    String INDEX_PATH = "index_path";
    String INDEX_VALUE_SYSTEM = "index_value_system";
    String INDEX_VALUE_CODE = "index_value_code";
    String RESOURCE_ID = "resource_id";
  }

  /** Unique indices */
  private interface UniqueIndices {
    String RESOURCE_TYPE_RESOURCE_ID_UNIQUE_INDEX =
        Joiner.on("_")
            .join(Tables.RESOURCES, ResourcesColumns.RESOURCE_TYPE, ResourcesColumns.RESOURCE_ID);

  }

  /** Indices */
  private interface Indices {
    String STRING_INDICES_TABLE_RESOURCE_TYPE_INDEX_NAME_INDEX_VALUE_INDEX =
        Joiner.on("_")
            .join(Tables.STRING_INDICES, StringIndicesColumns.RESOURCE_TYPE,
                StringIndicesColumns.INDEX_NAME,
                StringIndicesColumns.INDEX_VALUE);
    String REFERENCE_INDICES_TABLE_RESOURCE_TYPE_INDEX_NAME_INDEX_VALUE_INDEX =
        Joiner.on("_")
            .join(Tables.REFERENCE_INDICES, ReferenceIndicesColumns.RESOURCE_TYPE,
                ReferenceIndicesColumns.INDEX_NAME,
                ReferenceIndicesColumns.INDEX_VALUE);
    String CODE_INDICES_TABLE_RESOURCE_TYPE_INDEX_NAME_INDEX_VALUE_SYSTE_INDEX_VALUE_CODE_INDEX =
        Joiner.on("_")
            .join(Tables.CODE_INDICES, CodeIndicesColumns.RESOURCE_TYPE,
                CodeIndicesColumns.INDEX_NAME,
                CodeIndicesColumns.INDEX_VALUE_SYSTEM,
                CodeIndicesColumns.INDEX_VALUE_CODE);
  }

  /** Query to create the {@link Tables#RESOURCES} table. */
  private static String CREATE_RESOURCES_TABLE =
      "CREATE TABLE " + Tables.RESOURCES + " ( " +
          ResourcesColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
          ResourcesColumns.RESOURCE_TYPE + " TEXT NOT NULL," +
          ResourcesColumns.RESOURCE_ID + " TEXT NOT NULL," +
          ResourcesColumns.RESOURCE + " TEXT NOT NULL);";
  /** Query to create the unique index for the {@link Tables#RESOURCES} table. */
  private static String CREATE_RESOURCE_TABLE_UNIQUE_INDEX =
      "CREATE UNIQUE INDEX " + UniqueIndices.RESOURCE_TYPE_RESOURCE_ID_UNIQUE_INDEX + " ON " +
          Tables.RESOURCES + " ( " +
          ResourcesColumns.RESOURCE_TYPE + ", " +
          ResourcesColumns.RESOURCE_ID + ");";

  /** Query to create the {@link Tables#STRING_INDICES} table. */
  private static String CREATE_STRING_INDICES_TABLE =
      "CREATE TABLE " + Tables.STRING_INDICES + " ( " +
          StringIndicesColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
          StringIndicesColumns.RESOURCE_TYPE + " TEXT NOT NULL," +
          StringIndicesColumns.INDEX_NAME + " TEXT NOT NULL," +
          StringIndicesColumns.INDEX_PATH + " TEXT NOT NULL," +
          StringIndicesColumns.INDEX_VALUE + " TEXT NOT NULL," +
          StringIndicesColumns.RESOURCE_ID + " TEXT NOT NULL);";
  /** Query to create the index for the {@link Tables#STRING_INDICES} table. */
  private static String CREATE_STRING_INDICES_TABLE_INDEX =
      "CREATE INDEX " + Indices.STRING_INDICES_TABLE_RESOURCE_TYPE_INDEX_NAME_INDEX_VALUE_INDEX +
          " ON " +
          Tables.STRING_INDICES + " ( " +
          StringIndicesColumns.RESOURCE_TYPE + ", " +
          StringIndicesColumns.INDEX_NAME + ", " +
          StringIndicesColumns.INDEX_VALUE + ");";

  /** Query to create the {@link Tables#REFERENCE_INDICES} table. */
  private static String CREATE_REFERENCE_INDICES_TABLE =
      "CREATE TABLE " + Tables.REFERENCE_INDICES + " ( " +
          ReferenceIndicesColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
          ReferenceIndicesColumns.RESOURCE_TYPE + " TEXT NOT NULL," +
          ReferenceIndicesColumns.INDEX_NAME + " TEXT NOT NULL," +
          ReferenceIndicesColumns.INDEX_PATH + " TEXT NOT NULL," +
          ReferenceIndicesColumns.INDEX_VALUE + " TEXT NOT NULL," +
          ReferenceIndicesColumns.RESOURCE_ID + " TEXT NOT NULL);";
  /** Query to create the index for the {@link Tables#REFERENCE_INDICES} table. */
  private static String CREATE_REFERENCE_INDICES_TABLE_INDEX =
      "CREATE INDEX " + Indices.REFERENCE_INDICES_TABLE_RESOURCE_TYPE_INDEX_NAME_INDEX_VALUE_INDEX +
          " ON " +
          Tables.REFERENCE_INDICES + " ( " +
          ReferenceIndicesColumns.RESOURCE_TYPE + ", " +
          ReferenceIndicesColumns.INDEX_NAME + ", " +
          ReferenceIndicesColumns.INDEX_VALUE + ");";

  /** Query to create the {@link Tables#CODE_INDICES} table. */
  private static String CREATE_CODE_INDICES_TABLE =
      "CREATE TABLE " + Tables.CODE_INDICES + " ( " +
          CodeIndicesColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
          CodeIndicesColumns.RESOURCE_TYPE + " TEXT NOT NULL," +
          CodeIndicesColumns.INDEX_NAME + " TEXT NOT NULL," +
          CodeIndicesColumns.INDEX_PATH + " TEXT NOT NULL," +
          CodeIndicesColumns.INDEX_VALUE_SYSTEM + " TEXT NOT NULL," +
          CodeIndicesColumns.INDEX_VALUE_CODE + " TEXT NOT NULL," +
          CodeIndicesColumns.RESOURCE_ID + " TEXT NOT NULL);";
  /** Query to create the index for the {@link Tables#CODE_INDICES} table. */
  private static String CREATE_CODE_INDICES_TABLE_INDEX =
      "CREATE INDEX " +
          Indices.CODE_INDICES_TABLE_RESOURCE_TYPE_INDEX_NAME_INDEX_VALUE_SYSTE_INDEX_VALUE_CODE_INDEX +
          " ON " +
          Tables.CODE_INDICES + " ( " +
          CodeIndicesColumns.RESOURCE_TYPE + ", " +
          CodeIndicesColumns.INDEX_NAME + ", " +
          CodeIndicesColumns.INDEX_VALUE_SYSTEM + ", " +
          CodeIndicesColumns.INDEX_VALUE_CODE + ");";

  private final IParser iParser;
  private final FhirIndexer fhirIndexer;

  @Inject
  DatabaseImpl(Context context, IParser iParser, FhirIndexer fhirIndexer) {
    super(context, DB_NAME, null, DB_VERSION);
    this.iParser = iParser;
    this.fhirIndexer = fhirIndexer;
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(CREATE_RESOURCES_TABLE);
    sqLiteDatabase.execSQL(CREATE_STRING_INDICES_TABLE);
    sqLiteDatabase.execSQL(CREATE_REFERENCE_INDICES_TABLE);
    sqLiteDatabase.execSQL(CREATE_CODE_INDICES_TABLE);
    sqLiteDatabase.execSQL(CREATE_RESOURCE_TABLE_UNIQUE_INDEX);
    sqLiteDatabase.execSQL(CREATE_STRING_INDICES_TABLE_INDEX);
    sqLiteDatabase.execSQL(CREATE_REFERENCE_INDICES_TABLE_INDEX);
    sqLiteDatabase.execSQL(CREATE_CODE_INDICES_TABLE_INDEX);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

  @Override
  public <R extends Resource> void insert(R resource) throws ResourceAlreadyExistsInDbException {
    String type = resource.getResourceType().name();
    String id = resource.getId();
    ContentValues contentValues = new ContentValues();
    contentValues.put(ResourcesColumns.RESOURCE_TYPE, type);
    contentValues.put(ResourcesColumns.RESOURCE_ID, id);
    contentValues.put(ResourcesColumns.RESOURCE, iParser.encodeResourceToString(resource));
    SQLiteDatabase database = getWritableDatabase();
    try {
      database.beginTransaction();

      // Insert resource itself.
      database.insertOrThrow(Tables.RESOURCES, null, contentValues);

      ResourceIndices resourceIndices = fhirIndexer.index(resource);

      // Insert string indices.
      for (StringIndex stringIndex : resourceIndices.getStringIndices()) {
        ContentValues stringIndexContentValues = new ContentValues();
        stringIndexContentValues.put(StringIndicesColumns.RESOURCE_TYPE, type);
        stringIndexContentValues.put(StringIndicesColumns.INDEX_NAME, stringIndex.name());
        stringIndexContentValues.put(StringIndicesColumns.INDEX_PATH, stringIndex.path());
        stringIndexContentValues.put(StringIndicesColumns.INDEX_VALUE, stringIndex.value());
        stringIndexContentValues.put(StringIndicesColumns.RESOURCE_ID, id);
        database.replaceOrThrow(Tables.STRING_INDICES, null, stringIndexContentValues);
      }

      // Insert reference indices.
      for (ReferenceIndex referenceIndex : resourceIndices.getReferenceIndices()) {
        ContentValues referenceIndexContentValues = new ContentValues();
        referenceIndexContentValues.put(ReferenceIndicesColumns.RESOURCE_TYPE, type);
        referenceIndexContentValues.put(ReferenceIndicesColumns.INDEX_NAME, referenceIndex.name());
        referenceIndexContentValues.put(ReferenceIndicesColumns.INDEX_PATH, referenceIndex.path());
        referenceIndexContentValues
            .put(ReferenceIndicesColumns.INDEX_VALUE, referenceIndex.value());
        referenceIndexContentValues.put(ReferenceIndicesColumns.RESOURCE_ID, id);
        database.replaceOrThrow(Tables.REFERENCE_INDICES, null, referenceIndexContentValues);
      }

      // Insert code indices.
      for (CodeIndex codeIndex : resourceIndices.getCodeIndices()) {
        ContentValues codeIndexContentValues = new ContentValues();
        codeIndexContentValues.put(CodeIndicesColumns.RESOURCE_TYPE, type);
        codeIndexContentValues.put(CodeIndicesColumns.INDEX_NAME, codeIndex.name());
        codeIndexContentValues.put(CodeIndicesColumns.INDEX_PATH, codeIndex.path());
        codeIndexContentValues
            .put(CodeIndicesColumns.INDEX_VALUE_SYSTEM, codeIndex.system());
        codeIndexContentValues
            .put(CodeIndicesColumns.INDEX_VALUE_CODE, codeIndex.value());
        codeIndexContentValues.put(CodeIndicesColumns.RESOURCE_ID, id);
        database.replaceOrThrow(Tables.CODE_INDICES, null, codeIndexContentValues);
      }

      database.setTransactionSuccessful();
      database.endTransaction();
    } catch (SQLiteConstraintException e) {
      throw new ResourceAlreadyExistsInDbException(type, id, e);
    } finally {
      database.close();
    }
  }

  @Override
  public <R extends Resource> void update(R resource) {
    String type = resource.getResourceType().name();
    String id = resource.getId();
    ContentValues contentValues = new ContentValues();
    contentValues.put(ResourcesColumns.RESOURCE_TYPE, type);
    contentValues.put(ResourcesColumns.RESOURCE_ID, id);
    contentValues.put(ResourcesColumns.RESOURCE, iParser.encodeResourceToString(resource));
    SQLiteDatabase database = getWritableDatabase();
    try {
      database.replaceOrThrow(Tables.RESOURCES, null, contentValues);
    } finally {
      database.close();
    }
  }

  @Override
  public <R extends Resource> R select(Class<R> clazz, String id)
      throws ResourceNotFoundInDbException {
    String type = ResourceUtils.getResourceType(clazz).name();

    String[] columns = new String[]{ResourcesColumns.RESOURCE};
    String whereClause =
        ResourcesColumns.RESOURCE_TYPE + " = ? AND " + ResourcesColumns.RESOURCE_ID + " = ?";
    String[] whereArgs = new String[]{type, id};
    SQLiteDatabase database = getReadableDatabase();
    Cursor cursor = database
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
      return iParser.parseResource(clazz, cursor.getString(0));
    } finally {
      cursor.close();
      database.close();
    }
  }

  @Override
  public <R extends Resource> void delete(Class<R> clazz, String id) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }
}
