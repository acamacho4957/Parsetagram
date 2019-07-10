package com.example.parsetagram.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE= "image";
    private static final String KEY_USER= "user";
    private static final String KEY_CREATED_AT= "createdAt";
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
