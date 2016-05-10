package net.crevion.singgih.popularmoviesapp.main;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.PorterDuff;
import android.net.ParseException;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.crevion.singgih.popularmoviesapp.R;
import net.crevion.singgih.popularmoviesapp.model.Movies;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    public static final String ID_INTENT = "movie";
    @Bind(R.id.backdrop) ImageView backdrop;
    private Movies mMovie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_movie_container, fragment)
                    .commit();
        }
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        if (intent.hasExtra(ID_INTENT)) {
            mMovie = intent.getParcelableExtra(ID_INTENT);
        } else {
            throw new IllegalArgumentException("Detail activity must receive a movie parcelable");
        }
        getSupportActionBar().setTitle(mMovie.getTitle());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // update the location in our second pane using the fragment manager

    }
}
