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
import android.widget.Toast;

public class Source_List extends AppCompatActivity {
    private CursorAdapter rssAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sources);

        ListView rssListView = (ListView) findViewById(R.id.listViewRssFeed);; // get the ListView

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
                int count = cursor.getInt(cursor.getColumnIndex("_id"));
                
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
                    if(rssDialogUrlText.length() < 5) {
                        Toast.makeText(Source_List.this, "URL is too short", Toast.LENGTH_LONG).show();
                    } else {
                        saveRSSUrl(rssDialogUrlText, rssDialogNameUrlText);
                        onResume();
                    }
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
        } else if(item.getItemId() == R.id.editSource){
            //do stuff
        }
        return super.onOptionsItemSelected(item);
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

        if (getIntent().getExtras() == null)
        {
            databaseConnector.insertUrl(url, name);
        }
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
