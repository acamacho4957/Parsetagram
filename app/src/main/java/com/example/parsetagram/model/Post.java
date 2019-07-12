package com.example.parsetagram.model;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

@ParseClassName("Post")
public class Post extends ParseObject {
    private static final String TAG = "POST";

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE= "image";
    public static final String KEY_USER= "user";
    public static final String KEY_CREATED_AT= "createdAt";
    public static final String KEY_LIKES= "likes";
    private static final Integer POSTS_PER_PAGE = 20;

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public Integer getLikes() {
        JSONArray likedBy = getJSONArray("likedBy");
        if (likedBy == null) {
            return 0;
        } else {
            return likedBy.length();
        }
    }

//    public void setLikes(Integer count) {
//        put(KEY_LIKES, count);
//    }

    public boolean isLikedBy(String userId) {
        JSONArray likedBy = getJSONArray("likedBy");
        if (likedBy == null) {
            return false;
        } else {
            for (int i = 0; i < likedBy.length(); i++) {
                try {
                    String currentId = likedBy.getString(i);
                    if (currentId.equals(userId)) {
                        return true;
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "Error while reading JSONArray liked by");
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

    public void addLike(String userId) {
        Integer currentLikes = getLikes();
//        if (currentLikes != null) {
//            setLikes(getLikes() + 1);
//        } else {
//            setLikes(1);
//        }

        JSONArray currentLikers = getJSONArray("likedBy");
        ArrayList<String> newLikers = new ArrayList<>();
        if (currentLikers != null) {
            for (int i = 0; i < currentLikers.length(); i++) {
                try {
                    newLikers.add(currentLikers.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            newLikers.add(userId);
        } else {
            newLikers.add(userId);
        }
        put("likedBy", newLikers);
    }

    public void removeLike(String userId) {
        if (isLikedBy(userId)) {
            Log.d(TAG, "called remove like");
//            Integer currentLikes = getLikes();
//            if (currentLikes != null) {
//                setLikes(getLikes() - 1);
//            } else {
//                setLikes(0);
//            }

            JSONArray currentLikers = getJSONArray("likedBy");
            ArrayList<String> newLikers = new ArrayList<>();
            if (currentLikers != null) {
                for (int i = 0; i < currentLikers.length(); i++) {
                    try {
                        String currentId = currentLikers.getString(i);
                        if (currentId != userId) {
                            newLikers.add(currentId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            put("likedBy", newLikers);
            Log.d(TAG, newLikers.toString());
        }
    }

    public static class Query extends ParseQuery {
        public Query() {
            super(Post.class);
        }

        public Query getTop() {
            setLimit(POSTS_PER_PAGE);
            addDescendingOrder(KEY_CREATED_AT);
            return this;
        }

        public Query withUser() {
            include("user");
            return this;
        }

        public Query onPage(int page) {
            setSkip(page * POSTS_PER_PAGE);
            return this;
        }
    }
}
