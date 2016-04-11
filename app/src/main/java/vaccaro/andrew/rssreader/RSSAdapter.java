package vaccaro.andrew.rssreader;

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

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;


public class RSSAdapter extends RecyclerView.Adapter<RSSAdapter.ViewHolder> {
    private List<RSSEntry> rssEntries;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView urlTextView;
        public ImageView photoImageView;
        public TextView headlineTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            urlTextView = (TextView)itemView.findViewById(R.id.urlTextView);
            headlineTextView = (TextView)itemView.findViewById(R.id.headlineTextView);
            photoImageView = (ImageView)itemView.findViewById(R.id.photoUrlImageView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RSSAdapter(List<RSSEntry> mRssEntries) {
        this.rssEntries = mRssEntries;
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
//        holder.photoImageView.setImageURI(Uri.parse(rssEntries.get(pos).getPhotoURL()));
        new DownloadImage(holder.photoImageView).execute(rssEntries.get(pos).getPhotoURL());
        holder.urlTextView.setText(rssEntries.get(pos).getUrl());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return rssEntries.size();
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImage(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}


