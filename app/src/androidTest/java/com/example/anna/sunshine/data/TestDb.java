/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.anna.sunshine.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.widget.Toast;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }



    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPoleLocationValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */
    public void testLocationTable() {

        insertLocation();

    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createWeatherValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testWeatherTable() {
        // First insert the location, and then use the locationRowId to insert
        // the weather. Make sure to cover as many failure cases as you can.

        // Instead of rewriting all of the code we've already written in testLocationTable
        // we can move this code to insertLocation and then call insertLocation from both
        // tests. Why move it? We need the code to return the ID of the inserted location
        // and our testLocationTable can only return void because it's a test.

        long locationRowId = insertLocation();

        // Make sure we have a valid row ID.
        assertFalse("Error: Location Not Inserted Correctly", locationRowId == -1L);

        // 1.Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 2.Create ContentValues of  weather values
        // (you can use the createWeatherValues TestUtilities function if you wish)
        ContentValues weatherValues = TestUtilities.createWeatherValues(locationRowId);

        // 3.Insert ContentValues into database and get a row ID back
        long weatherRowId= db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues);
        //verify the rowId
        assertTrue(weatherRowId != -1);

        // 4.Query the database and receive a Cursor back
        Cursor cursor = db.query(WeatherContract.WeatherEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null); // sort order

        // 5.Move the cursor to a valid database row and check whether we got any rows from the query
        assertTrue("Error: no records found", cursor.moveToFirst());

        // 6.Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("testInsertReadDb weatherEntry failed to validate", cursor, weatherValues);



        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from location query", cursor.moveToNext());
        // 7.Finally, close the cursor and database
        cursor.close();
        db.close();

    }


    /*
        Students: This is a helper method for the testWeatherTable quiz. You can move your
        code from testLocationTable to here so that you can call this code from both
        testWeatherTable and testLocationTable.
     */
    public long insertLocation() {

        // 1.First step: Get reference to writable database
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        // 2.Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)

        ContentValues locationValues = new ContentValues();
        //tableValues.put(WeatherContract.LocationEntry._ID, 1);
        locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, "94043");
        locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "Bangalore");
        locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, 12.9667);
        locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, -77.5667);

        // 3.Insert ContentValues into database and get a row ID back
        long locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, locationValues );

        //verify the rowId
        assertTrue(locationRowId != -1);

        // 4.Query the database and receive a Cursor back
        Cursor cursor = db.query(WeatherContract.LocationEntry.TABLE_NAME,
                                        null, //all columns
                                        null, //columns for where clause
                                        null, //values for the where clause
                                        null, //groupby
                                        null, //columns to filter by row groups
                                        null); //sort


        // 5.Move the cursor to a valid database row and check whether we got any rows from the query
        assertTrue("Error: no records found", cursor.moveToFirst());

        // 6.Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Location table query validation failed !! ", cursor, locationValues);



        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from location query", cursor.moveToNext());
        // 7.Finally, close the cursor and database
        cursor.close();
        db.close();

        return locationRowId;
    }
}
