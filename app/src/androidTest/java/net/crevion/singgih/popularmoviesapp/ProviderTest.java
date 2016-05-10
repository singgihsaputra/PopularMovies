package net.crevion.singgih.popularmoviesapp;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;

import net.crevion.singgih.popularmoviesapp.data.MoviesContract;

import java.util.Map;
import java.util.Set;

/**
 * Created by singgih on 09/05/2016.
 */
public class ProviderTest extends AndroidTestCase {
    private static final String TEST_MOVIE_ID = "209021";
    private static final String TEST_MOVIE_NAME = "Harry Potter and the Sorcerer's Stone";
    private static final String TEST_UPDATE_MOVIE_NAME = "Harry Potter and the Philosopher's Stone";
    private static final String TEST_MOVIE_POSTER_PATH = "coba/path";
    private static final String TEST_MOVIE_DESCRIPTION = "Harry HarryHarry Harry Potter and the Sorcerer's Stone Harry Potter and the Sorcerer's Stone Harry Potter and the Sorcerer's Stone Harry Potter and the Sorcerer's Stone Harry Potter and the Sorcerer's Stone";
    private static final String TEST_MOVIE_BACKDROP = "coba/path/backdrop";
    private static final String TEST_MOVIE_RELEASE_DATE = "2016-05-09";
    private static final double TEST_MOVIE_POPULARITY = 32.90;
    private static final double TEST_MOVIE_RATING = 8.5;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testDeleteAllRecords();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        testDeleteAllRecords();
    }

    public void testDeleteAllRecords(){
        // Delete movies
        mContext.getContentResolver().delete(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null
        );


        // Ensure movies were deleted
        Cursor cursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();


    }

    public void testGetType(){

        //-- MOVIE --//
        String type = mContext.getContentResolver().getType(MoviesContract.MovieEntry.CONTENT_URI);
        assertEquals(MoviesContract.MovieEntry.CONTENT_TYPE, type);

        //-- MOVIE_ID --//
        type = mContext.getContentResolver().getType(MoviesContract.MovieEntry.buildMovieUri(0));
        assertEquals(MoviesContract.MovieEntry.CONTENT_ITEM_TYPE, type);
    }



    public void testInsertReadMovie(){
        // We first insert a Genre
        // No need to verify this, we already have a test for inserting genre

        ContentValues movieContentValues = getMovieContentValues();
        Uri movieInsertUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, movieContentValues);
        long movieRowId = ContentUris.parseId(movieInsertUri);

        // Verify we got a row back
        assertTrue(movieRowId > 0);

        // Query for all and validate
        Cursor movieCursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        validateCursor(movieCursor, movieContentValues);
        movieCursor.close();

        movieCursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.buildMovieUri(movieRowId),
                null,
                null,
                null,
                null
        );
        validateCursor(movieCursor, movieContentValues);
        movieCursor.close();
    }

    public void testUpdateMovie(){

        ContentValues movieContentValues = getMovieContentValues();
        Uri movieInsertUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, movieContentValues);
        long movieRowId = ContentUris.parseId(movieInsertUri);

        // Update
        ContentValues updatedMovieContentValues = new ContentValues(movieContentValues);
        updatedMovieContentValues.put(MoviesContract.MovieEntry._ID, movieRowId);
        updatedMovieContentValues.put(MoviesContract.MovieEntry.COLUMN2_TITLE, TEST_UPDATE_MOVIE_NAME);
        mContext.getContentResolver().update(
                MoviesContract.MovieEntry.CONTENT_URI,
                updatedMovieContentValues,
                MoviesContract.MovieEntry._ID + " = ?",
                new String[]{String.valueOf(movieRowId)}
        );

        Cursor movieCursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.buildMovieUri(movieRowId),
                null,
                null,
                null,
                null
        );
        validateCursor(movieCursor, updatedMovieContentValues);
        movieCursor.close();
    }


    private ContentValues getMovieContentValues(){
        ContentValues values = new ContentValues();
        values.put(MoviesContract.MovieEntry.COLUMN1_MOVIE_ID, TEST_MOVIE_ID);
        values.put(MoviesContract.MovieEntry.COLUMN2_TITLE, TEST_MOVIE_NAME);
        values.put(MoviesContract.MovieEntry.COLUMN3_POSTER_PATH, TEST_MOVIE_POSTER_PATH);
        values.put(MoviesContract.MovieEntry.COLUMN5_BACKDROP, TEST_MOVIE_BACKDROP);
        values.put(MoviesContract.MovieEntry.COLUMN4_DESCRIPTION, TEST_MOVIE_DESCRIPTION);
        values.put(MoviesContract.MovieEntry.COLUMN6_DATE, TEST_MOVIE_RELEASE_DATE);
        values.put(MoviesContract.MovieEntry.COLUMN7_POPULARITY, TEST_MOVIE_POPULARITY);
        values.put(MoviesContract.MovieEntry.COLUMN8_RATING, TEST_MOVIE_RATING);
        return values;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void validateCursor(Cursor valueCursor, ContentValues expectedValues){
        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for(Map.Entry<String, Object> entry : valueSet){
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            switch(valueCursor.getType(idx)){
                case Cursor.FIELD_TYPE_FLOAT:
                    assertEquals(entry.getValue(), valueCursor.getDouble(idx));
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    assertEquals(Integer.parseInt(entry.getValue().toString()), valueCursor.getInt(idx));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    assertEquals(entry.getValue(), valueCursor.getString(idx));
                    break;
                default:
                    assertEquals(entry.getValue().toString(), valueCursor.getString(idx));
                    break;
            }
        }
        valueCursor.close();
    }
}