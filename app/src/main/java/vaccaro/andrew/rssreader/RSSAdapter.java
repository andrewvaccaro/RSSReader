package vaccaro.andrew.rssreader;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;


public class RSSAdapter extends RecyclerView.Adapter<RSSAdapter.ViewHolder> {
    private List<RSSEntry> rssEntries;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView urlTextView;
        public ImageView photoImageView;
        public TextView headlineTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            urlTextView = (TextView)itemView.findViewById(R.id.urlTextView);
            headlineTextView = (TextView)itemView.findViewById(R.id.headlineTextView);
            photoImageView = (ImageView)itemView.findViewById(R.id.photoUrlImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RSSAdapter(List<RSSEntry> mRssEntries, Context context) {
        this.rssEntries = mRssEntries;
        this.context = context;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public RSSAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.headlineTextView.setText(rssEntries.get(pos).getHeadline());
        Picasso.with(context).load(rssEntries.get(pos).getPhotoURL()).fit().centerCrop().into(holder.photoImageView);
        holder.urlTextView.setText(rssEntries.get(pos).getUrl());
        final String url = rssEntries.get(pos).getUrl();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(browserIntent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return rssEntries.size();
    }

}


