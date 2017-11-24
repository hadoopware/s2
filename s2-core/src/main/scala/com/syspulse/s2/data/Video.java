package com.syspulse.s2.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Video implements Serializable {
    protected String id;
    protected String title;
    protected String description;
    protected String category;
    protected List<String> subcategories;
    protected String titleExtended;
    protected List<Credit> credits;
    protected String rating;
    protected String ratingMPAA;
    protected Boolean adult;

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getSubcategories() {
        if (subcategories == null) {
            subcategories = new ArrayList<String>();
        }
        return this.subcategories;
    }

    public List<Credit> getCredits() {
        if (credits == null) {
            credits = new ArrayList<Credit>();
        }
        return this.credits;
    }

    public String getTitleExtended() {
        return titleExtended;
    }

    public void setTitleExtended(String titleExtended) {
        this.titleExtended = titleExtended;
    }

    public String getRating() {
        return rating;
    }

    public String getRatingMPAA() {
        return ratingMPAA;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
    public void setRatingMPAA(String ratingMPAA) {
        this.ratingMPAA = ratingMPAA;
    }

    public Boolean getAdult() {
        return adult;
    }
    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
