package vaccaro.andrew.rssreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection
{
    private static final String DATABASE_NAME = "RSSFeeds";
    private SQLiteDatabase database;
    private DatabaseOpenHelper databaseOpenHelper;

    /**
     * Creates database helper object
     * @param context
     */
    public DatabaseConnection(Context context)
    {
        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    }

    /**
     * Opens the database.
     * @throws SQLException
     */
    public void open() throws SQLException
    {
        database = databaseOpenHelper.getWritableDatabase();
    }

    /**
     * Closes database connection.
     */
    public void close()
    {
        if (database != null)
            database.close(); // close the database connection
    }

    /**
     * Update the RSS entry.
     * @param id
     * @param url
     * @param name
     */
    public void updateRSSFeed(String id, String url, String name){
        String filter = "_id=" + id;
        ContentValues newFeed = new ContentValues();
        newFeed.put("url", url);
        newFeed.put("name", name);
        open();
        database.update("RSSFeeds", newFeed, filter, null);
        close();
    }

    /**
     * Inserts a new RSS entry into the database
     * @param url
     * @param name
     */
    public void insertUrl(String url, String name)
    {
        ContentValues newFeed = new ContentValues();
        newFeed.put("url", url);
        newFeed.put("name", name);
        open();
        database.insert("RSSFeeds", null, newFeed);
        close();
    }

    /**
     * Gets all of the RSS Feeds from the database
     * @return
     */
    public Cursor getAllRSSFeeds()
    {
        return database.query("RSSFeeds", new String[] {"_id", "url", "name"}, null, null, null, null, "name");
    }

    /**
     * Gets a specific RSS feed
     * @param id
     * @return
     */
    public Cursor getRSSFeed(String id)
    {
        return database.query("RSSFeeds", null, "_id=" + id, null, null, null, null);
    }

    /**
     * Deletes a specific RSS feed
     * @param id
     */
    public void deleteRSSFeed(int id)
    {
        open();
        database.delete("RSSFeeds", "_id=" + id, null);
        close();
    }

    /**
     * Creates the database structure.
     */
    private class DatabaseOpenHelper extends SQLiteOpenHelper
    {
        // public constructor
        public DatabaseOpenHelper(Context context, String name,
                                  CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        } // end DatabaseOpenHelper constructor

        // creates the contacts table when the database is created
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            // query to create a new table named contacts
            String createQuery = "CREATE TABLE RSSFeeds" +
                    "(_id integer primary key autoincrement," +
                    "url TEXT, name TEXT);";
            db.execSQL(createQuery);
        } // end method onCreate

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}

