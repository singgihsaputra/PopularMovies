package net.crevion.singgih.popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by jose on 10/6/15.
 */
public class Movies implements Parcelable{
    @SerializedName("id")
    private String id;
    @SerializedName("original_title")
    private String title;
    @SerializedName("poster_path")
    private String poster;
    @SerializedName("overview")
    private String description;
    @SerializedName("backdrop_path")
    private String backdrop;
    @SerializedName("release_date")
    private String date;
    @SerializedName("popularity")
    private String popularity;
    @SerializedName("vote_average")
    private String rating;

    public Movies() {}
    public Movies(String id, String title, String poster, String description, String backdrop, String date, String popularity, String rating) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.description = description;
        this.backdrop = backdrop;
        this.date = date;
        this.popularity = popularity;
        this.rating = rating;
    }

    protected Movies(Parcel in) {
        id = in.readString();
        title = in.readString();
        poster = in.readString();
        description = in.readString();
        backdrop = in.readString();
        date = in.readString();
        rating = in.readString();
        popularity = in.readString();
    }

    public static final Creator<Movies> CREATOR = new Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public String getYear(String dateString) {
        SimpleDateFormat ori = new SimpleDateFormat("yy-MM-dd");
        Date date_ori = new Date();
        try {
            date_ori = ori.parse(dateString);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat convert = new SimpleDateFormat("yyyy");
        String years = convert.format(date_ori);
        return years;

    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getRating() {
        return rating+"/10";
    }

    public Double getRatingOri() {
        return Double.parseDouble(rating);
    }

    public Double getPopularityOri() {
        return Double.parseDouble(popularity);
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
    public String getPopularity() {
        DecimalFormat df = new DecimalFormat("#.##");
        Double pop = Double.parseDouble(popularity);
        return df.format(pop);
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getPoster() {
        return "http://image.tmdb.org/t/p/w342" + poster;
    }

    public String getLocalPoster() {
        return poster;
    }


    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBackdrop() {
        return "http://image.tmdb.org/t/p/w500"  + backdrop;
    }

    public String getLocalBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(poster);
        parcel.writeString(description);
        parcel.writeString(backdrop);
        parcel.writeString(date);
        parcel.writeString(rating);
        parcel.writeString(popularity);


    }



    public static class MovieResult {
        private List<Movies> results;

        public List<Movies> getResults() {
            return results;
        }
    }
}
