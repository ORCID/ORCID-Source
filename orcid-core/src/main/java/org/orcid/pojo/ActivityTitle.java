package org.orcid.pojo;

import org.orcid.pojo.ajaxForm.Date;

public class ActivityTitle {

    private long putCode;
    private String title;
    private String translatedTitle;
    private boolean isDefault;
    private boolean isFeatured;
    private boolean isPublic;

    public long getPutCode() {
        return putCode;
    }

    public void setPutCode(long putCode) {
        this.putCode = putCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(String translatedTitle) {
        this.translatedTitle = translatedTitle;
    }


    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

}
