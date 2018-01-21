package com.netsol.atoz.Model;

import java.util.ArrayList;

/**
 * Created by macmini on 12/7/17.
 */

public class AboutGroup {
    private String id;
    private String heading;
    private String last_saved;
    private String explanation;
    private AboutChild aboutChild;

    public AboutGroup() {
    }

    public AboutGroup(String heading, AboutChild aboutChild) {
        this.heading = heading;
        this.aboutChild = aboutChild;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public AboutChild getAboutChild() {
        return aboutChild;
    }

    public void setAboutChild(AboutChild aboutChild) {
        this.aboutChild = aboutChild;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLast_saved() {
        return last_saved;
    }

    public void setLast_saved(String last_saved) {
        this.last_saved = last_saved;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
