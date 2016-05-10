package net.crevion.singgih.popularmoviesapp.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Movie;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.crevion.singgih.popularmoviesapp.R;
import net.crevion.singgih.popularmoviesapp.main.DetailActivity;
import net.crevion.singgih.popularmoviesapp.main.DetailActivityFragment;
import net.crevion.singgih.popularmoviesapp.main.MainActivity;
import net.crevion.singgih.popularmoviesapp.main.MainActivityFragment;
import net.crevion.singgih.popularmoviesapp.model.Movies;
import net.crevion.singgih.popularmoviesapp.ui.EndlessRecyclerOnScrollListener;

import static net.crevion.singgih.popularmoviesapp.ui.EndlessRecyclerOnScrollListener.current_page;
import static net.crevion.singgih.popularmoviesapp.ui.EndlessRecyclerOnScrollListener.previousTotal;

/**
 * Created by singgih on 15/04/2016.
 */
public class PopularMoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private List<Movies> mMoviesList;
    private LayoutInflater mInflater;
    private Context mContext;
    private RelativeLayout relLayout;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private View layoutView;
    private boolean isLoading = false;

    public PopularMoviesAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        //this.mListener = item;
    }
    @Override
    public int getItemViewType(int position) {
        return mMoviesList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_view, null);
            return new RecyclerViewHolders(layoutView);
        } else if (viewType == VIEW_TYPE_LOADING) {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_item, parent, false);
            return new LoadingViewHolder(layoutView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //holder.countryName.setText(itemList.get(position).getName());
        if (holder instanceof RecyclerViewHolders) {
            RecyclerViewHolders viewHolder = (RecyclerViewHolders) holder;
//            viewHolder.rl.setVisibility(View.INVISIBLE);
            Movies movie = mMoviesList.get(position);
            viewHolder.movieTitle.setText(movie.getTitle());
            viewHolder.movieTitle.setSelected(true);
            viewHolder.movieYear.setText(movie.getYear(movie.getDate()));
            viewHolder.movieRate.setText(movie.getRating());
            viewHolder.moviePopular.setText(movie.getPopularity());
            Activity ac = (Activity) mContext;
            SharedPreferences pref = ac.getPreferences(0);
            String sort = pref.getString(mContext.getString(R.string.pref_sort_key), mContext.getString(R.string.pref_sort_default));
            if (sort.equals("Most Popular")) {
                viewHolder.moviePopular.setVisibility(View.VISIBLE);
                viewHolder.movieRate.setVisibility(View.GONE);
                viewHolder.iconPop.setVisibility(View.VISIBLE);
                viewHolder.iconRate.setVisibility(View.GONE);
            } else if (sort.equals("Highest Rated")) {
                viewHolder.moviePopular.setVisibility(View.GONE);
                viewHolder.movieRate.setVisibility(View.VISIBLE);
                viewHolder.iconPop.setVisibility(View.GONE);
                viewHolder.iconRate.setVisibility(View.VISIBLE);
            } else {
                viewHolder.moviePopular.setVisibility(View.GONE);
                viewHolder.movieRate.setVisibility(View.GONE);
                viewHolder.iconPop.setVisibility(View.GONE);
                viewHolder.iconRate.setVisibility(View.GONE);
            }
            final int position2 = position;
            final RecyclerViewHolders holder2 = viewHolder;
            if(!sort.equals("My Favorite")) {
                Picasso.with(mContext)
                        .load(movie.getPoster())
                        .placeholder(R.drawable.background)
                        .fit()
                        .centerCrop()
                        .into(viewHolder.moviePoster);
            }else{

                Picasso.with(mContext)
                        .load(new File(movie.getLocalPoster(), movie.getId()+".PNG"))
                        .placeholder(R.drawable.background)
                        .fit()
                        .centerCrop()
                        .into(viewHolder.moviePoster);
            }
        }else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {

        return (mMoviesList == null) ? 0 : mMoviesList.size();
    }

    public void setMovieList(List<Movies> results, int page) {
        if (this.mMoviesList != null && page == 1) {
            this.mMoviesList.clear();
            current_page=1;
            previousTotal = 0;
        }else if(this.mMoviesList == null && page == 1){
            this.mMoviesList = new ArrayList<>();
        }

        this.mMoviesList.addAll(results);
        notifyDataSetChanged();
    }


    public void showLoading() {
        this.mMoviesList.add(null);
        isLoading = true;
        notifyItemInserted(mMoviesList.size()-1);
    }
    public void hideLoading(){
        mMoviesList.remove(mMoviesList.size()-1);
        notifyItemRemoved(mMoviesList.size());
        isLoading = false;
    }
    public boolean getLoading() {
        return isLoading;
    }


    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder {
        public ImageView moviePoster;
        public ImageView iconRate;
        public ImageView iconPop;
        public TextView movieTitle;
        public TextView movieYear;
        public TextView movieRate;
        public TextView moviePopular;
        public RelativeLayout rl;
        private final Context context;
        MainActivity main;
        private Movies movies;


        public RecyclerViewHolders(final View itemView) {
            super(itemView);

            context = itemView.getContext();
            rl = (RelativeLayout) itemView.findViewById(R.id.tooltip);
            moviePoster = (ImageView) itemView.findViewById(R.id.poster_photo);
            iconRate = (ImageView) itemView.findViewById(R.id.icon_rate);
            iconPop = (ImageView) itemView.findViewById(R.id.icon_pop);
            movieTitle = (TextView) itemView.findViewById(R.id.title_movie);
            movieYear = (TextView) itemView.findViewById(R.id.year);
            movieRate = (TextView) itemView.findViewById(R.id.rate);
            moviePopular = (TextView) itemView.findViewById(R.id.pop);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(DetailActivity.ID_INTENT, mMoviesList.get(getAdapterPosition()));
                    mContext.startActivity(intent);


                }
            });
        }



    }
}
