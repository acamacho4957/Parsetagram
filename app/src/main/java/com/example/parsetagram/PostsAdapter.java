package com.example.parsetagram;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.parsetagram.model.Post;
import com.parse.ParseFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Post> posts;

    public PostsAdapter(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Post post = posts.get(position);

        viewHolder.tvUsername.setText(post.getUser().getUsername());
        viewHolder.tvDescription.setText(post.getDescription());

        ParseFile postedImage = post.getImage();
        if (postedImage != null) {
            String preURL = postedImage.getUrl();
            String completeURL = preURL.substring(0, 4) + "s" + preURL.substring(4, preURL.length());
            Glide.with(context)
                    .load(completeURL)
                    .into(viewHolder.ivPostedImage);
        }

        String rawDate = post.getCreatedAt().toString();
        String formattedDate = rawDate.substring(4, 10) + " , " + rawDate.substring(24, 28);
        viewHolder.tvCreatedAt.setText(formattedDate);

        ParseFile profileImage = post.getUser().getParseFile("profileImage");
        if (profileImage != null) {
            String preURL = profileImage.getUrl();
            String completeURL = preURL.substring(0, 4) + "s" + preURL.substring(4, preURL.length());
            Glide.with(context)
                    .load(completeURL)
                    .bitmapTransform(new RoundedCornersTransformation(context, 20, 0))
                    .into(viewHolder.ivProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.tvCreatedAt) TextView tvCreatedAt;
        @BindView(R.id.ivPostedImage) ImageView ivPostedImage;
        @BindView(R.id.tvDescription) TextView tvDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String abrevDate = relativeDate;

        Pattern pattern = Pattern.compile("[0-9]*\\s[a-z]");
        Matcher matcher = pattern.matcher(abrevDate);
        if (matcher.find()) {
            abrevDate =  matcher.group(0).replaceAll("\\s+","");
        }

        return abrevDate;
    }
}
