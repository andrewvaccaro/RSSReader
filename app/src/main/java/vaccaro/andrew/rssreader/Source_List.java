package vaccaro.andrew.rssreader;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import javax.xml.transform.Source;

public class Source_List extends AppCompatActivity {
    private CursorAdapter rssAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sources);
        ListView rssListView = (ListView) findViewById(R.id.listViewRssFeed);
        String[] from = new String[] { "name", "url" };
        int[] to = new int[] { R.id.rssFeedName, R.id.rssFeedUrl };
        rssAdapter = new SimpleCursorAdapter(Source_List.this, R.layout.rssfeed_listview_item, null, from, to, 0);
        rssListView.setAdapter(rssAdapter);

        rssListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3)
            {
                Cursor cursor = (Cursor)adapter.getItemAtPosition(position);
                String id = cursor.getString(cursor.getColumnIndex("_id"));
                Intent intent = new Intent(Source_List.this, RSS_Feed_List.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        rssListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View v, int position, long arg3) {
                Cursor cursor = (Cursor)adapter.getItemAtPosition(position);
                final String itemId = cursor.getString(cursor.getColumnIndex("_id"));

                AlertDialog.Builder builder = new AlertDialog.Builder(Source_List.this);
                builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        updateButtonHandler(itemId);
                    }
                });
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteRSSFeed(itemId);
                        Toast.makeText(Source_List.this, "RSS Feed deleted.",Toast.LENGTH_SHORT).show();
                        onResume();
                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onResume();
                    }
                });
                builder.setTitle("Modify Entry");
                builder.setMessage("What would you like to do to this entry?");
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_edit_sources, menu);
        return true;
    }

    // handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.addSource){
            final AlertDialog.Builder addSourcesDialog = new AlertDialog.Builder(this);
            addSourcesDialog.setView(R.layout.add_source_dialog);
            addSourcesDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Dialog thisDialog = (Dialog)dialog;
                    EditText rssDialogUrl = (EditText)thisDialog.findViewById(R.id.addRssFeedUrl);
                    String rssDialogUrlText = rssDialogUrl.getText().toString();
                    EditText rssDialogName = (EditText)thisDialog.findViewById(R.id.addRssTitle);
                    String rssDialogNameUrlText = rssDialogName.getText().toString();
                    saveRSSUrl(rssDialogUrlText, rssDialogNameUrlText);
                    onResume();
                }
            });
            addSourcesDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = addSourcesDialog.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        View emptyList = findViewById(R.id.empty_rss_list_message);
        ListView lv = (ListView)findViewById(R.id.listViewRssFeed);
        lv.setEmptyView(emptyList);
    }

    @Override
    protected void onResume()
    {
        super.onResume(); // call super's onResume method
        new GetRSSTask().execute((Object[]) null);
    } // end method onResume

    @Override
    protected void onStop()
    {
        Cursor cursor = rssAdapter.getCursor();

        if (cursor != null)
            cursor.close();

        rssAdapter.changeCursor(null);
        super.onStop();
    }


    private void saveRSSUrl(String url, String name)
    {
        DatabaseConnection databaseConnector = new DatabaseConnection(this);
        databaseConnector.insertUrl(url, name);
    }

    private void deleteRSSFeed(String id)
    {
        DatabaseConnection databaseConnector = new DatabaseConnection(this);
        databaseConnector.deleteRSSFeed(Integer.parseInt(id));
    }

    public void updateRSSFeed(String id, String url, String name){
        DatabaseConnection databaseConnector = new DatabaseConnection(this);
        databaseConnector.updateRSSFeed(id, url, name);
    }

    private String[] getRssFeed(String id){
        DatabaseConnection databaseConnector = new DatabaseConnection(this);
        databaseConnector.open();
        Cursor cursor = databaseConnector.getRSSFeed(id);
        cursor.moveToNext();
        String url = cursor.getString(cursor.getColumnIndex("url"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        String[] arr = new String[3];
        arr[0] = id;
        arr[1] = url;
        arr[2] = name;
        databaseConnector.close();
        return arr;

    }

    private void updateButtonHandler(String id){
        final String[] arr = getRssFeed(id);
        final AlertDialog.Builder addSourcesDialog = new AlertDialog.Builder(this);
//        addSourcesDialog.setView(R.layout.add_source_dialog);

        addSourcesDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Dialog thisDialog = (Dialog)dialog;
                final EditText rssDialogUrl = (EditText)thisDialog.findViewById(R.id.addRssFeedUrl);
                final EditText rssDialogName = (EditText)thisDialog.findViewById(R.id.addRssTitle);
                String rssDialogUrlText = rssDialogUrl.getText().toString();
                String rssDialogNameUrlText = rssDialogName.getText().toString();
                updateRSSFeed(arr[0], rssDialogUrlText, rssDialogNameUrlText);
                Toast.makeText(Source_List.this, "The entry was updated.", Toast.LENGTH_LONG);
                onResume();
            }
        });
        addSourcesDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        LayoutInflater inflater = Source_List.this.getLayoutInflater();
        View mView = inflater.inflate(R.layout.add_source_dialog, null);
        addSourcesDialog.setView(mView);
        AlertDialog alertDialog = addSourcesDialog.create();
        TextView tv = (TextView)mView.findViewById(R.id.addRSSTextView);
        tv.setText("Edit RSS Feed");
        final EditText rssDialogUrl = (EditText)mView.findViewById(R.id.addRssFeedUrl);
        rssDialogUrl.setText(arr[1]);
        final EditText rssDialogName = (EditText)mView.findViewById(R.id.addRssTitle);
        rssDialogName.setText(arr[2]);
        alertDialog.show();
    }

    private class GetRSSTask extends AsyncTask<Object, Object, Cursor>
    {
        DatabaseConnection databaseConnector = new DatabaseConnection(Source_List.this);

        @Override
        protected Cursor doInBackground(Object... params)
        {
            databaseConnector.open();
            return databaseConnector.getAllRSSFeeds();
        }

        @Override
        protected void onPostExecute(Cursor result)
        {
            rssAdapter.changeCursor(result);
            databaseConnector.close();
        }
    }
}
