package vaccaro.andrew.rssreader;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import vaccaro.andrew.rssreader.*;


import java.util.ArrayList;
import java.util.List;

public class rss_list extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<RSSEntry> list = new ArrayList<>();
    private CursorAdapter rssAdapter;

//    private RSSEntry entryTest = new RSSEntry("Headline 1", "www.google.com", "http://placekitten.com/200/205");
//    private RSSEntry entryTest2 = new RSSEntry("Headline 2", "www.google2.com", "http://placekitten.com/200/250");
//    private RSSEntry entryTest3 = new RSSEntry("Headline 3", "www.google3.com", "http://placekitten.com/200/240");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_list);
        DatabaseConnection dbc = new DatabaseConnection(rss_list.this);
        ArrayList<String> urlList = dbc.selectAllRSSFeeds();
        ArrayList<RssItem> rssItems = new ArrayList<>();
        for(int i=0,l=urlList.size();i<l;i++){
            RssReader rssReader = new RssReader(urlList.get(i));
            try {
                rssItems = (ArrayList<RssItem>)rssReader.getItems();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for(int i=0,l=rssItems.size();i<l;i++){
            String desc = rssItems.get(i).getDescription();
            String img = rssItems.get(i).getImageUrl();
            String headline = rssItems.get(i).getTitle();
            String link = rssItems.get(i).getLink();
            RSSEntry rsse = new RSSEntry(headline, link, img);
            list.add(rsse);
        }




//        list.add(entryTest);
//        list.add(entryTest);
//        list.add(entryTest);
//        list.add(entryTest);
//        list.add(entryTest);
//        list.add(entryTest3);
//        list.add(entryTest3);
//        list.add(entryTest3);
//        list.add(entryTest3);
//        list.add(entryTest3);
//        list.add(entryTest3);
//        list.add(entryTest3);
//        list.add(entryTest2);
//        list.add(entryTest2);
//        list.add(entryTest2);
//        list.add(entryTest2);
        mRecyclerView = (RecyclerView)findViewById(R.id.rssListRecView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RSSAdapter(list, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_sources, menu);
        return true;
    }

    // handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // create a new Intent to launch the AddEditContact Activity
        Intent sourceList = new Intent(rss_list.this, edit_sources.class);
        startActivity(sourceList);
        return super.onOptionsItemSelected(item);
    }

    @Override
//called each time an Activity returns to the foreground including when it is
//first created
    protected void onResume()
    {
        super.onResume(); // call super's onResume method
        new GetRSSTask().execute((Object[]) null);
    } // end method onResume

    @Override
    //called when activity is no longer visible to user
    protected void onStop()
    {
        Cursor cursor = rssAdapter.getCursor(); // get current Cursor

        if (cursor != null)
            cursor.close(); // deactivate it

        rssAdapter.changeCursor(null); // adapted now has no Cursor
        super.onStop();
    } // end method onStop

    private class GetRSSTask extends AsyncTask<Object, Object, Cursor>
    {
        DatabaseConnection databaseConnector = new DatabaseConnection(rss_list.this);

        @Override
        protected Cursor doInBackground(Object... params)
        {
            databaseConnector.open();
            return databaseConnector.getAllRSSFeeds();
        }

        @Override
        protected void onPostExecute(Cursor result)
        {
            rssAdapter.changeCursor(result); // set the adapter's Cursor
            databaseConnector.close();
        }
    }



}
