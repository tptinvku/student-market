package sict.apps.studentmarket.models;

import java.io.Serializable;

public class AdSlide implements Serializable {
    private int slideId;

    public AdSlide(int slideId) {
        this.slideId = slideId;
    }

    public int getSlideId() {
        return slideId;
    }

    public void setSlideId(int slideId) {
        this.slideId = slideId;
    }
}
