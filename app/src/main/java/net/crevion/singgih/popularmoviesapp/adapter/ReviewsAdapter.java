package net.crevion.singgih.popularmoviesapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.crevion.singgih.popularmoviesapp.R;
import net.crevion.singgih.popularmoviesapp.model.MoviesReview;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by singgih on 15/04/2016.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private List<MoviesReview> mReviewList;
    private LayoutInflater mInflater;
    private Context mContext;
    private RelativeLayout relLayout;
    private View layoutView;
    private boolean isLoading = false;

    public ReviewsAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        //this.mListener = item;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_review, null);
        return new RecyclerViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            RecyclerViewHolders viewHolder = (RecyclerViewHolders) holder;
            MoviesReview reviews = mReviewList.get(position);
            viewHolder.reviewer.setText(reviews.getAuthor());
            String myData = String.valueOf(Html
                .fromHtml("<![CDATA[<body style=\"text-align:justify;color:#222222;font-size:14px \">"
                        + reviews.getContent()
                        + "</body>]]>"));
            viewHolder.webReview.loadData(myData, "text/html; charset=utf-8", "utf-8");
    }

    @Override
    public int getItemCount() {
        return (mReviewList == null) ? 0 : mReviewList.size();
    }

    public void setReviewList(List<MoviesReview> results) {
        if (this.mReviewList != null) {
            this.mReviewList.clear();
        }
        this.mReviewList = new ArrayList<>();
        this.mReviewList.addAll(results);
        notifyDataSetChanged();
    }


    public class RecyclerViewHolders extends RecyclerView.ViewHolder {
        @Bind(R.id.reviewer) TextView reviewer;
        @Bind(R.id.webReview) WebView webReview;


        public RecyclerViewHolders(final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            reviewer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(webReview.getVisibility()==View.GONE)
                        webReview.setVisibility(View.VISIBLE);
                    else
                        webReview.setVisibility(View.GONE);
                    //mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mTrailerList.get(getAdapterPosition()).getLink(key))));
                }
            });
        }



    }
}
