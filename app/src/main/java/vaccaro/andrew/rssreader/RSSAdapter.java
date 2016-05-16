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

    // Provide a suitable constructor (depends on the kind of dataset)
    public RSSAdapter(List<RSSEntry> mRssEntries, Context context) {
        this.rssEntries = mRssEntries;
        this.context = context;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public RSSAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recyclerview, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
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

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return rssEntries.size();
    }

}


