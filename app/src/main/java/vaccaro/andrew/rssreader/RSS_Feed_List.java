package vaccaro.andrew.rssreader;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.net.URLEncoder;
import java.util.ArrayList;

public class RSS_Feed_List extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView emptyText;
    private String id;
    private String url;
    private ArrayList<RSSEntry> list = new ArrayList<>();
    private DatabaseConnection db;
    private SwipeRefreshLayout srl;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_list);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        srl = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        srl.setEnabled(true);
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

    /**
     * Allows back button in action bar to return to main activity.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(RSS_Feed_List.this, Source_List.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    /**
     * Gets RSS URL from database on activity load.
     * @param id
     */
    public void getRssUrlOnLoad(String id){
        try{
            new GetRSSTask().execute(id);
        } catch (Exception e){
            Log.d("Exception=>", e.getMessage());
        }
    }

    /**
     * Gets RSS items from url on activity load
     * @param url
     */
    private void getRssItemsOnLoad(String url){
        try{
            new GetRssFeed().execute(url);
        } catch (Exception e){
            Log.d("Exception=>", e.getMessage());
        }
    }

    /**
     * Swipe to refresh component that grabs rss items from URL.
     * @param url
     */
    void refreshItems(String url) {
        getRssItemsOnLoad(url);
        onItemsLoadComplete();
    }

    /**
     * Cancels swipe refresh after loading complete.
     */
    void onItemsLoadComplete() {
        srl.setRefreshing(false);
    }


    /**
     * Runs on separate thread to grab RSS items from a URL
     */
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
            emptyText = (TextView)findViewById(R.id.empty_view);
            if (list.size() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                emptyText.setVisibility(View.VISIBLE);
                srl.setEnabled(false);
            } else {
                mRecyclerView.setAdapter(mAdapter);
                spinner.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Runs on separate thread to grab clicked item's ID and return it's url and name.
     */
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
            String name = result.getString(result.getColumnIndex("name"));
            setTitle(name);
            getRssItemsOnLoad(url);
            databaseConnector.close();
        }
    }
}
