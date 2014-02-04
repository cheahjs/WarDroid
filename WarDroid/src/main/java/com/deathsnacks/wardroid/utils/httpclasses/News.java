package com.deathsnacks.wardroid.utils.httpclasses;

/**
 * Created by Admin on 04/02/14.
 */
public class News {
    private String id;
    private String url;
    private long publish;
    private String text;

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public long getPublish() {
        return publish;
    }

    public String getText() {
        return text;
    }

    public News(String raw) {
        String[] data = raw.split("\\|");
        id = data[0];
        url = data[1];
        publish = Long.parseLong(data[2]);
        text = data[3];
    }
}
