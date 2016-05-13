package vaccaro.andrew.rssreader;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import java.util.ArrayList;

public class RSS_Feed_List extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String id;
    private String url;
    private ArrayList<RSSEntry> list = new ArrayList<>();
    private DatabaseConnection db;
    private SwipeRefreshLayout srl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_list);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        srl = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        getRssUrlOnLoad(id);
        mRecyclerView = (RecyclerView)findViewById(R.id.rssListRecView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RSSAdapter(list, this);

        srl.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        list.clear();
                        refreshItems(url);
                    }
                }
        );
    }

    public void getRssUrlOnLoad(String id){
        try{
            new GetRSSTask().execute(id);
        } catch (Exception e){
            Log.d("Exception=>", e.getMessage());
        }
    }

    private void getRssItemsOnLoad(String url){
        try{
            new GetRssFeed().execute(url);
        } catch (Exception e){
            Log.d("Exception=>", e.getMessage());
        }
    }

    void refreshItems(String url) {
        getRssItemsOnLoad(url);
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        srl.setRefreshing(false);
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
        Intent sourceList = new Intent(RSS_Feed_List.this, Source_List.class);
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

    private class GetRSSTask extends AsyncTask<String, Object, Cursor>
    {
        DatabaseConnection databaseConnector = new DatabaseConnection(RSS_Feed_List.this);

        @Override
        protected Cursor doInBackground(String... params)
        {
            databaseConnector.open();
            return databaseConnector.getRSSFeed(params[0]);
        }

        @Override
        protected void onPostExecute(Cursor result)
        {
            result.moveToNext();
            url = result.getString(result.getColumnIndex("url"));
            getRssItemsOnLoad(url);
            databaseConnector.close();
        }
    }
}
