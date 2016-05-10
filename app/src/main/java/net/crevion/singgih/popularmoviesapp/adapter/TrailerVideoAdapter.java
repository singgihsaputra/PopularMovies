package net.crevion.singgih.popularmoviesapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.crevion.singgih.popularmoviesapp.R;
import net.crevion.singgih.popularmoviesapp.main.DetailActivity;
import net.crevion.singgih.popularmoviesapp.main.MainActivity;
import net.crevion.singgih.popularmoviesapp.model.Movies;
import net.crevion.singgih.popularmoviesapp.model.MoviesVideo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static net.crevion.singgih.popularmoviesapp.ui.EndlessRecyclerOnScrollListener.current_page;
import static net.crevion.singgih.popularmoviesapp.ui.EndlessRecyclerOnScrollListener.previousTotal;

/**
 * Created by singgih on 15/04/2016.
 */
public class TrailerVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private List<MoviesVideo> mTrailerList;
    private LayoutInflater mInflater;
    private Context mContext;
    private RelativeLayout relLayout;
    private View layoutView;
    private boolean isLoading = false;

    public TrailerVideoAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        //this.mListener = item;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_video_trailer, null);
        return new RecyclerViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            RecyclerViewHolders viewHolder = (RecyclerViewHolders) holder;
            MoviesVideo trailer = mTrailerList.get(position);
            String key = trailer.getKey();
            viewHolder.trailerName.setText(trailer.getName());

            Picasso.with(mContext)
                    .load(trailer.getThumb(key))
                    .resize(220, 150)
                    .into(viewHolder.trailerThumb);
    }

    @Override
    public int getItemCount() {
        return (mTrailerList == null) ? 0 : mTrailerList.size();
    }

    public void setTrailerList(List<MoviesVideo> results) {
        if (this.mTrailerList != null) {
            this.mTrailerList.clear();
        }
        this.mTrailerList = new ArrayList<>();
        this.mTrailerList.addAll(results);
        notifyDataSetChanged();
    }


    public class RecyclerViewHolders extends RecyclerView.ViewHolder {
        @Bind(R.id.trailerName) TextView trailerName;
        @Bind(R.id.trailerThumb) ImageView trailerThumb;
        private final Context context;
        MainActivity main;
        private Movies movies;


        public RecyclerViewHolders(final View itemView) {
            super(itemView);

            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String key = mTrailerList.get(getAdapterPosition()).getKey();
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mTrailerList.get(getAdapterPosition()).getLink(key))));
                }
            });
        }



    }
}
