package org.orcid.pojo.ajaxForm;

public abstract class VisibilityForm implements ErrorsInterface {
    protected Visibility visibility;

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
}
