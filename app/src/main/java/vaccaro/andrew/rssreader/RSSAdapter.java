package vaccaro.andrew.rssreader;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class RSSAdapter extends RecyclerView.Adapter<RSSAdapter.ViewHolder> {
    private List<RSSEntry> rssEntries;
    private Context context;

    /**
     * Sets on click listener for recycler view items and instantiates textview/imageview
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView photoImageView;
        public TextView headlineTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            headlineTextView = (TextView)itemView.findViewById(R.id.headlineTextView);
            photoImageView = (ImageView)itemView.findViewById(R.id.photoUrlImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    /**
     * Constructor that takes in a list of the RSS items
     * @param mRssEntries
     * @param context
     */
    public RSSAdapter(List<RSSEntry> mRssEntries, Context context) {
        this.rssEntries = mRssEntries;
        this.context = context;
    }


    /**
     * Sets view to recycler view.
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RSSAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recyclerview, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * Sets the textview and imageview of the recyclerview layout and creates an onclick listener
     * to open the URL in a browser.
     * @param holder
     * @param pos
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        holder.headlineTextView.setText(rssEntries.get(pos).getHeadline());
        if(rssEntries.get(pos).getPhotoURL() == null)
            Picasso.with(context).load(R.drawable.placeholder).fit().centerCrop().into(holder.photoImageView);
        else
            Picasso.with(context).load(rssEntries.get(pos).getPhotoURL()).fit().centerCrop().into(holder.photoImageView);
        final String url = rssEntries.get(pos).getUrl();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(browserIntent);
            }
        });
    }

    /*
     * Return size of dataset.
     */
    @Override
    public int getItemCount() {
        return rssEntries.size();
    }

}


