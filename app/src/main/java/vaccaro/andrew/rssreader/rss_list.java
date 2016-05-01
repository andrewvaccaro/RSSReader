package vaccaro.andrew.rssreader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;


import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

public class rss_list extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<RSSEntry> list = new ArrayList<>();
    private DatabaseConnection db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_list);
        db = new DatabaseConnection(rss_list.this);
        ArrayList<String> urlList = db.selectAllRSSFeeds();
        for(int i=0;i<urlList.size();i++){
            new GetRssFeed().execute(urlList.get(i));
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

    private class GetRssFeed extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                RssReader rssReader = new RssReader(params[0]);
                for (RssItem item : rssReader.getItems()){
                    String title = item.getTitle();
                    String img = item.getImageUrl();
                    String url = item.getLink();
                    RSSEntry rss = new RSSEntry(title,url,img);
                    list.add(rss);
                }
            } catch (Exception e) {
                Log.v("Error Parsing Data", e + "");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(mAdapter);
        }
    }


}
