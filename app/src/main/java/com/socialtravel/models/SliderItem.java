package com.socialtravel.models;

public class SliderItem {
    String imageURL;
    long timestamp;

    public SliderItem(){

    }
    public SliderItem(String imageURL, long timestamp) {
        this.imageURL = imageURL;
        this.timestamp = timestamp;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
