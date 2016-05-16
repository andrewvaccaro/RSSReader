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

    public DatabaseConnection(Context context)
    {
        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    }

    // open the database connection
    public void open() throws SQLException
    {
        database = databaseOpenHelper.getWritableDatabase();
    }

    // close the database connection
    public void close()
    {
        if (database != null)
            database.close(); // close the database connection
    }

    // inserts a new contact in the database
    public void insertUrl(String url, String name)
    {
        ContentValues newFeed = new ContentValues();
        newFeed.put("url", url);
        newFeed.put("name", name);
        open();
        database.insert("RSSFeeds", null, newFeed);
        Log.d("insert Url => ", "here");
        close();
    }

    public Cursor getAllRSSFeeds()
    {
        return database.query("RSSFeeds", new String[] {"_id", "url", "name"}, null, null, null, null, "name");
    }

    public ArrayList<String> selectAllRSSFeeds(){
        String query = "select * from RSSFeeds";
        database = databaseOpenHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(query,null);

        ArrayList<String> urlList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                urlList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return urlList;
    }

    public Cursor getRSSFeed(String id)
    {
        return database.query("RSSFeeds", null, "_id=" + id, null, null, null, null);
    }

    public void deleteRSSFeed(int id)
    {
        open();
        database.delete("RSSFeeds", "_id=" + id, null);
        close();
    }

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

