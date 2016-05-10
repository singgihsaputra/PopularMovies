package net.crevion.singgih.popularmoviesapp.main;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.crevion.singgih.popularmoviesapp.BuildConfig;
import net.crevion.singgih.popularmoviesapp.R;
import net.crevion.singgih.popularmoviesapp.adapter.ReviewsAdapter;
import net.crevion.singgih.popularmoviesapp.adapter.TrailerVideoAdapter;
import net.crevion.singgih.popularmoviesapp.data.MoviesContract;
import net.crevion.singgih.popularmoviesapp.model.Movies;
import net.crevion.singgih.popularmoviesapp.model.MoviesReview;
import net.crevion.singgih.popularmoviesapp.model.MoviesVideo;
import net.crevion.singgih.popularmoviesapp.network.ConnectionDetector;
import net.crevion.singgih.popularmoviesapp.network.MovieApi;
import net.crevion.singgih.popularmoviesapp.data.MoviesProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by singgih on 07/05/2016.
 */
public class DetailActivityFragment extends Fragment {
    public static final String ID_INTENT = "movie";
    private Movies mMovie;
    private ShareActionProvider mShareActionProvider;
    private ProgressDialog pDialog;
    private TrailerVideoAdapter mAdapterTrailer;
    private ReviewsAdapter mAdapterReview;
    private LinearLayoutManager lLayoutTrailer;
    private LinearLayoutManager lLayoutReviews;
    RecyclerView rViewTrailer;
    RecyclerView rViewReviews;
    private String trailer_share;
    ImageView backdrop;
    @Bind(R.id.title_inside) TextView title;
    @Bind(R.id.date) TextView date;
    @Bind(R.id.rating) TextView rating;
    @Bind(R.id.popularity) TextView popularity;
    @Bind(R.id.posterThumbnail) ImageView poster;
    @Bind(R.id.webDescription) WebView webDescription;
    @Bind(R.id.favorite) ImageView favorite;
    private boolean checkFavorite = false;
    private String LOCAL_PATH_POSTER = "Poster";
    private String LOCAL_PATH_BACKDROP = "Backdrop";
    public DetailActivityFragment() {
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    public  void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_movie, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        //mShareActionProvider.setShareIntent(createShareTrailerIntent());
    }
    private Intent createShareTrailerIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, trailer_share);

        return shareIntent;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        lLayoutTrailer = new LinearLayoutManager(getActivity());
        rViewTrailer = (RecyclerView) rootView.findViewById(R.id.recycler_view_trailer);
        rViewTrailer.setHasFixedSize(true);
        rViewTrailer.setLayoutManager(lLayoutTrailer);
        mAdapterTrailer = new TrailerVideoAdapter(getActivity());
        rViewTrailer.setAdapter(mAdapterTrailer);

        lLayoutReviews = new LinearLayoutManager(getActivity());
        rViewReviews = (RecyclerView) rootView.findViewById(R.id.recycler_view_reviews);
        rViewReviews.setHasFixedSize(true);
        rViewReviews.setLayoutManager(lLayoutReviews);
        mAdapterReview = new ReviewsAdapter(getActivity());
        rViewReviews.setAdapter(mAdapterReview);
        String sort = MainActivityFragment.sort;
        ButterKnife.bind(this, rootView);
        Intent intent = getActivity().getIntent();
        if (intent.hasExtra(ID_INTENT)) {
            mMovie = intent.getParcelableExtra(ID_INTENT);
        } else {
            throw new IllegalArgumentException("Detail activity must receive a movie parcelable");
        }
        backdrop = (ImageView) getActivity().findViewById(R.id.backdrop);


        String dateString = mMovie.getDate();
        SimpleDateFormat ori = new SimpleDateFormat("yy-MM-dd");
        Date date_ori = new Date();
        try {
            date_ori = ori.parse(dateString);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat convert = new SimpleDateFormat("MMM yyyy");
        String date_convert = convert.format(date_ori);
        title.setText(" "+mMovie.getTitle());
        date.setText(" "+date_convert);
        popularity.setText(" "+mMovie.getPopularity());
        rating.setText(" "+mMovie.getRating());

        String myData = String.valueOf(Html
                .fromHtml("<![CDATA[<body style=\"text-align:justify;color:#222222; \">"
                        + mMovie.getDescription()
                        + "</body>]]>"));
        webDescription.loadData(myData, "text/html; charset=utf-8", "utf-8");

        if(sort.equals("My Favorite")) {
            Picasso.with(getContext())
                    .load(new File(mMovie.getLocalPoster(), mMovie.getId()+".PNG"))
                    .into(poster);
            Picasso.with(getActivity())
                    .load(new File(mMovie.getLocalBackdrop(), mMovie.getId()+".PNG"))
                    .into(backdrop);
        }else {
            Picasso.with(getContext())
                    .load(mMovie.getPoster())
                    .into(poster);
            Picasso.with(getActivity())
                    .load(mMovie.getBackdrop())
                    .into(backdrop);
        }
        backdrop.setColorFilter(Color.GRAY, PorterDuff.Mode.DARKEN);
        Cursor movieCursor = getActivity().getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                MoviesContract.MovieEntry.TABLE_NAME+"."+MoviesContract.MovieEntry.COLUMN1_MOVIE_ID +" = "+mMovie.getId(),
                null,
                null
        );

        if (movieCursor.getCount() > 0) {
            favorite.setImageResource(R.drawable.ic_favorite_black_24dp);
            setFavorite(true);
        }

        movieCursor.close();
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(!getFavorite()) {
                    favorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                    poster.buildDrawingCache();
                    Bitmap bmp_poster = poster.getDrawingCache();
                    backdrop.buildDrawingCache();
                    Bitmap bmp_backdrop = backdrop.getDrawingCache();
                    String saved_poster = "";
                    String saved_backdrop = "";
                    try {
                        saved_poster = saveToInternalStorage(bmp_poster, LOCAL_PATH_POSTER, mMovie.getId());
                        saved_backdrop = saveToInternalStorage(bmp_backdrop, LOCAL_PATH_BACKDROP, mMovie.getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("PATH POSTER", saved_poster);

                    ContentValues values = MoviesProvider.getMovieContentValues(
                            mMovie.getId(),
                            mMovie.getTitle(),
                            saved_poster,
                            saved_backdrop,
                            mMovie.getDescription(),
                            mMovie.getDate(),
                            mMovie.getPopularityOri(),
                            mMovie.getRatingOri());
                    Uri newUri = getContext().getContentResolver().insert(
                            MoviesContract.MovieEntry.CONTENT_URI,
                            values
                    );
                    long newID = ContentUris.parseId(newUri);
                    if (newID > 0) {
                        setFavorite(true);
                        Toast.makeText(getContext(),"Mark as favorite", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(),"Process failed", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    deleteFileBitmap(mMovie.getLocalPoster()+"/"+mMovie.getId()+".PNG");
                    deleteFileBitmap(mMovie.getLocalBackdrop()+"/"+mMovie.getId()+".PNG");
                    int row = getContext().getContentResolver().delete(
                            MoviesContract.MovieEntry.CONTENT_URI,
                            MoviesContract.MovieEntry.TABLE_NAME+"."+MoviesContract.MovieEntry.COLUMN1_MOVIE_ID +" = "+mMovie.getId(),
                            null
                    );
                    if (row > 0) {
                        favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        setFavorite(false);
                        Toast.makeText(getContext(),"Unmark from favorite", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(),"Process failed", Toast.LENGTH_SHORT).show();
                    }
                }
                // Query for all and validate

                //Check database
                Cursor movieCursor = getActivity().getContentResolver().query(
                        MoviesContract.MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
                if (movieCursor == null) {
                    Log.e("Provider", "Cursor Error");
                }
                else if (movieCursor.getCount() < 1) {
                    Log.e("Provider", "Nothing to show");
                }
                else {
                    while(movieCursor.moveToNext()){
                        Log.d("Provider",movieCursor.getString(0)+"-"+movieCursor.getString(1)+"-"+movieCursor.getString(2));
                    }
                }
                movieCursor.close();

            }
        });
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Get Detail Information. . .");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

        Log.d("SORT", sort);
        if(!sort.equals("My Favorite")) {
            if (checkConnection())
                loadTrailer();
        }else{
            ConnectionDetector cd = new ConnectionDetector(getActivity());
            if(!cd.isConnectingToInternet()) {
                RelativeLayout lv = (RelativeLayout) rootView.findViewById(R.id.layout_video);
                lv.setVisibility(View.GONE);
                RelativeLayout rv = (RelativeLayout) rootView.findViewById(R.id.layout_reviews);
                rv.setVisibility(View.GONE);
                Toast.makeText(getContext(),"Connection not available", Toast.LENGTH_SHORT).show();
                hidePDialog();
            }else{
                loadTrailer();
            }
        }

       return rootView;
    }

    public boolean checkConnection(){
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        if(!cd.isConnectingToInternet()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("No Internet connection.");
            builder.setMessage("Seems your internet settings is wrong. Go to settings menu?");

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();

            hidePDialog();
            return false;
        }else{
            return true;
        }
    }
    public void loadTrailer(){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/3/movie/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            String url_add = mMovie.getId()+"/videos";
            Call<MoviesVideo.VideosResult> call;
            MovieApi movieApi = retrofit.create(MovieApi.class);
            call = movieApi.videoMovies(url_add, BuildConfig.OPEN_WEATHER_MAP_API_KEY);
            Log.d("URL JSON", call.toString());
            call.enqueue(new Callback<MoviesVideo.VideosResult>() {
                @Override
                public void onResponse(Call<MoviesVideo.VideosResult> trailerResult, Response<MoviesVideo.VideosResult> response) {
                    if (response.isSuccessful()) {
                        Log.d("Data JSON", response.body().toString());
                        MoviesVideo.VideosResult result = response.body();
                        mAdapterTrailer.setTrailerList(result.getResults());
                        String key = result.getResults().get(0).getKey();
                        trailer_share = "Check out trailer movie '"+title.getText()+"' at "+result.getResults().get(0).getLink(key);
                        mShareActionProvider.setShareIntent(createShareTrailerIntent());
                        loadReviews();
                        hidePDialog();
                    } else {
                        Log.e("JSON Fail", "Failed load data");
                        Toast.makeText(getContext(),"Load Data failed", Toast.LENGTH_SHORT).show();
                        hidePDialog();
                    }

                }

                @Override
                public void onFailure(Call<MoviesVideo.VideosResult> call, Throwable t) {
                    Toast.makeText(getContext(),"Process failed", Toast.LENGTH_SHORT).show();
                    hidePDialog();
                }
            });
    }

    public void loadReviews(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        String url_add = mMovie.getId()+"/reviews";
        Call<MoviesReview.ReviewsResult> call;
        MovieApi movieApi = retrofit.create(MovieApi.class);
        call = movieApi.reviewMovies(url_add, BuildConfig.OPEN_WEATHER_MAP_API_KEY);
        Log.d("URL JSON", call.toString());
        call.enqueue(new Callback<MoviesReview.ReviewsResult>() {
            @Override
            public void onResponse(Call<MoviesReview.ReviewsResult> reviewResult, Response<MoviesReview.ReviewsResult> response) {
                if (response.isSuccessful()) {
                    Log.d("Data JSON", response.body().toString());
                    MoviesReview.ReviewsResult result = response.body();
                    mAdapterReview.setReviewList(result.getResults());

                } else {
                    Log.e("JSON Fail", "Failed load data");
                    Toast.makeText(getContext(),"Load data failed", Toast.LENGTH_SHORT).show();
                    hidePDialog();
                }
            }
            @Override
            public void onFailure(Call<MoviesReview.ReviewsResult> call, Throwable t) {
                Toast.makeText(getContext(),"Process failed", Toast.LENGTH_SHORT).show();
                hidePDialog();
            }
        });
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void setFavorite(boolean favorite) {
        this.checkFavorite = favorite;
    }

    public boolean getFavorite() {
        return checkFavorite;
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String dir, String name) throws IOException {

        ContextWrapper cw = new ContextWrapper(getActivity());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(dir, Context.MODE_PRIVATE);

        // Create imageDir
        File mypath=new File(directory, name+".PNG");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
        return directory.getAbsolutePath();
    }

    private void deleteFileBitmap(String path){
        File file = new File(path);
        if(file.exists())
            file.delete();
    }
}
