package vaccaro.andrew.rssreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class rss_list extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<RSSEntry> list = new ArrayList<>();

    private RSSEntry entryTest = new RSSEntry("Headline 1", "www.google.com", "http://placekitten.com/200/205");
    private RSSEntry entryTest2 = new RSSEntry("Headline 2", "www.google2.com", "http://placekitten.com/200/250");
    private RSSEntry entryTest3 = new RSSEntry("Headline 3", "www.google3.com", "http://placekitten.com/200/240");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_list);
        list.add(entryTest);
        list.add(entryTest);
        list.add(entryTest);
        list.add(entryTest);
        list.add(entryTest);
        list.add(entryTest3);
        list.add(entryTest3);
        list.add(entryTest3);
        list.add(entryTest3);
        list.add(entryTest3);
        list.add(entryTest3);
        list.add(entryTest3);
        list.add(entryTest2);
        list.add(entryTest2);
        list.add(entryTest2);
        list.add(entryTest2);
        mRecyclerView = (RecyclerView)findViewById(R.id.rssListRecView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RSSAdapter(list, this);
        mRecyclerView.setAdapter(mAdapter);

    }

}
