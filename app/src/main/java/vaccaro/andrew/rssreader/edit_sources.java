package vaccaro.andrew.rssreader;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class edit_sources extends AppCompatActivity {
    private CursorAdapter rssAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sources);


        ListView rssListView = (ListView) findViewById(R.id.listViewRssFeed);; // get the ListView

        String[] from = new String[] { "url" };
        int[] to = new int[] { R.id.rssFeedTextView };
        rssAdapter = new SimpleCursorAdapter(edit_sources.this, R.layout.rssfeed_listview_item, null, from, to, 0);
        rssListView.setAdapter(rssAdapter);

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
            final AlertDialog.Builder inputAlert = new AlertDialog.Builder(this);
            inputAlert.setTitle("Add Source URL");
            inputAlert.setMessage("Enter the RSS feed URL");
            final EditText urlInput = new EditText(this);
            urlInput.setHint("Paste URL here");
            inputAlert.setView(urlInput);
            inputAlert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String url = urlInput.getText().toString();
                    if(url.length() < 5) {
                        Toast.makeText(edit_sources.this, "URL is too short", Toast.LENGTH_LONG).show();
                    } else {
                        saveRSSUrl(url);
                        onResume();
                    }
                }
            });
            inputAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = inputAlert.create();
            alertDialog.show();
        } else if(item.getItemId() == R.id.editSource){
            //do stuff
        }
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


    private void saveRSSUrl(String url)
    {
        // get DatabaseConnector to interact with the SQLite database
        DatabaseConnection databaseConnector = new DatabaseConnection(this);

        if (getIntent().getExtras() == null)
        {
            databaseConnector.insertUrl(url);
        }
    } // end class saveContact

    private class GetRSSTask extends AsyncTask<Object, Object, Cursor>
    {
        DatabaseConnection databaseConnector = new DatabaseConnection(edit_sources.this);

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
