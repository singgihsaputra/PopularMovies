package net.crevion.singgih.popularmoviesapp.main;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.crevion.singgih.popularmoviesapp.BuildConfig;
import net.crevion.singgih.popularmoviesapp.R;
import net.crevion.singgih.popularmoviesapp.adapter.PopularMoviesAdapter;
import net.crevion.singgih.popularmoviesapp.data.MoviesContract;
import net.crevion.singgih.popularmoviesapp.model.Movies;
import net.crevion.singgih.popularmoviesapp.network.ConnectionDetector;
import net.crevion.singgih.popularmoviesapp.network.MovieApi;
import net.crevion.singgih.popularmoviesapp.ui.EndlessRecyclerOnScrollListener;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment
{
    private SharedPreferences pref;
    public static String sort;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridLayoutManager lLayout;
    private PopularMoviesAdapter mAdapter;
    RecyclerView rView;
    private ProgressDialog pDialog;

    static final int COLUMN1_MOVIE_ID = 1;
    static final int COLUMN2_TITLE = 2;
    static final int COLUMN3_POSTER_PATH = 3;
    static final int COLUMN4_DESCRIPTION = 4;
    static final int COLUMN5_BACKDROP = 5;
    static final int COLUMN6_DATE = 6;
    static final int COLUMN7_POPULARITY = 7;
    static final int COLUMN8_RATING = 8;
    //MainActivity main = new MainActivity();
    public MainActivityFragment() {
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public  void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            showMenuSortBy();

        }else if (id == R.id.action_favorite) {
            SharedPreferences.Editor edt = pref.edit();
            edt.putString(getString(R.string.pref_sort_key), "My Favorite");
            edt.commit();
            sort = pref.getString(getString(R.string.pref_sort_key),getString(R.string.pref_sort_default));
            ((MainActivity) getActivity()).setActionBarTitle("Popular Movies (My Favorite)");
            refresh();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStop() {
        super.onStop();
        if(mAdapter.getItemCount()==0) {
            //new Dialog().buildDialogRefresh(getActivity()).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Refresh dialog");
            builder.setMessage("Do you want to reload data?");

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    refresh();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(sort.equals("My Favorite")){
            refresh();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        lLayout = new GridLayoutManager(getActivity(), 2);
        rView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
        mAdapter = new PopularMoviesAdapter(getActivity());
        rView.setAdapter(mAdapter);

        pref = getActivity().getPreferences(0);
        sort = pref.getString(getString(R.string.pref_sort_key),getString(R.string.pref_sort_default));

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

                refresh();
            }
        });
        refresh();
        rView.setOnScrollListener(new EndlessRecyclerOnScrollListener(lLayout) {
            @Override
            public void onLoadMore(int current_page) {
                //Toast.makeText(getContext(),Integer.toString(current_page), Toast.LENGTH_SHORT).show();
                if(!mAdapter.getLoading()) {
                    if(!sort.equals("My Favorite")) {
                        Log.d("hint", "Load More");
                        mAdapter.showLoading();
                        loadMovies(sort, current_page);
                    }
                }
            }
        });
        return rootView;
    }


    public void loadMovies(String sort, int currentPage){

        // Showing progress dialog before making http request
        if(!swipeRefreshLayout.isRefreshing() && !mAdapter.getLoading()) {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading...");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        }
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        if(!cd.isConnectingToInternet()){
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
            swipeRefreshLayout.setRefreshing(false);
            if(mAdapter.getLoading())
                mAdapter.hideLoading();
            hidePDialog();
        }else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Call<Movies.MovieResult> call;
            MovieApi movieApi = retrofit.create(MovieApi.class);
            final String page = Integer.toString(currentPage);
            if (sort.equals("Most Popular")) {
                call = movieApi.popularMovies(BuildConfig.OPEN_WEATHER_MAP_API_KEY, page);
            } else {
                call = movieApi.ratedMovies(BuildConfig.OPEN_WEATHER_MAP_API_KEY, page);
            }
            final int curr_page = currentPage;
            call.enqueue(new Callback<Movies.MovieResult>() {
                @Override
                public void onResponse(Call<Movies.MovieResult> movieResult, Response<Movies.MovieResult> response) {
                    if (response.isSuccessful()) {
                        Movies.MovieResult result = response.body();
                        if(mAdapter.getLoading())
                            mAdapter.hideLoading();
                        mAdapter.setMovieList(result.getResults(), curr_page);
                        if(swipeRefreshLayout.isRefreshing()){
                            Toast.makeText(getContext(),"Refresh successful", Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);

                        }else{
                            hidePDialog();
                        }
                    } else {
                        if(swipeRefreshLayout.isRefreshing()){
                            Toast.makeText(getContext(),"Refresh failed", Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }else{
                            if(mAdapter.getLoading())
                                mAdapter.hideLoading();
                            hidePDialog();
                            Toast.makeText(getContext(),"Process failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onFailure(Call<Movies.MovieResult> call, Throwable t) {
                    Toast.makeText(getContext(),"Process failed", Toast.LENGTH_SHORT).show();
                    hidePDialog();
                }
            });
        }
    }
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public void showMenuSortBy(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());

        builderSingle.setTitle("Sort By :");
        final String[] sort2 = {"Most Popular", "Highest Rated"};

        String sort_default = pref.getString(getString(R.string.pref_sort_key),getString(R.string.pref_sort_default));
        int item;
        if(sort_default.equals(sort2[0]))
            item=0;
        else if (sort_default.equals(sort2[1]))
            item=1;
        else
            item=3;
        builderSingle.setSingleChoiceItems(sort2, item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = sort2[which];
                ((MainActivity) getActivity()).setActionBarTitle("Popular Movies ("+strName+")");

                SharedPreferences.Editor edt = pref.edit();
                edt.putString(getString(R.string.pref_sort_key), strName);
                edt.commit();
                sort = pref.getString(getString(R.string.pref_sort_key),getString(R.string.pref_sort_default));
                refresh();
                dialog.dismiss();
            }
        });

        builderSingle.show();
    }

    public void refresh(){
        if(sort.equals("Most Popular") || sort.equals("Highest Rated")) {
            swipeRefreshLayout.setEnabled(true);
            loadMovies(sort, 1);
        }else{
            swipeRefreshLayout.setEnabled(false);
            loadFavorite();
        }
        lLayout.smoothScrollToPosition(rView, null, 1);
    }

    private void loadFavorite() {
        List<Movies> list = new ArrayList<>();
        Cursor movieCursor = getActivity().getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if (movieCursor.getCount() < 1) {
            Log.e("Provider", "Nothing to show");
            Toast.makeText(getContext(),"Your Favorite Movie is Empty", Toast.LENGTH_LONG).show();
        }else {
            while (movieCursor.moveToNext()) {
                String id = movieCursor.getString(COLUMN1_MOVIE_ID);
                String title = movieCursor.getString(COLUMN2_TITLE);
                String poster = movieCursor.getString(COLUMN3_POSTER_PATH);
                String description = movieCursor.getString(COLUMN4_DESCRIPTION);
                String backdrop = movieCursor.getString(COLUMN5_BACKDROP);
                String date = movieCursor.getString(COLUMN6_DATE);
                String popularity = Double.toString(movieCursor.getDouble(COLUMN7_POPULARITY));
                String rating = Double.toString(movieCursor.getDouble(COLUMN8_RATING));
                Movies mMovies = new Movies(id, title, poster, description, backdrop, date, popularity, rating);
                list.add(mMovies);
            }
        }
        mAdapter.setMovieList(list, 1);

    }

}
