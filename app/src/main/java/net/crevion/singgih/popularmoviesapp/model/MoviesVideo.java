package net.crevion.singgih.popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jose on 10/6/15.
 */
public class MoviesVideo implements Parcelable{
    @SerializedName("id")
    private String id;
    @SerializedName("key")
    private String key;
    @SerializedName("name")
    private String name;
    @SerializedName("site")
    private String site;

    public MoviesVideo() {}

    protected MoviesVideo(Parcel in) {
        id = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
    }

    public static final Creator<MoviesVideo> CREATOR = new Creator<MoviesVideo>() {
        @Override
        public MoviesVideo createFromParcel(Parcel in) {
            return new MoviesVideo(in);
        }

        @Override
        public MoviesVideo[] newArray(int size) {
            return new MoviesVideo[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getThumb(String key){
        return "http://img.youtube.com/vi/"+key+"/1.jpg";
    }

    public String getLink(String key){
        return "https://www.youtube.com/watch?v="+key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(key);
        parcel.writeString(site);
    }

    public static class VideosResult {
        private List<MoviesVideo> results;

        public List<MoviesVideo> getResults() {
            return results;
        }
    }
}
