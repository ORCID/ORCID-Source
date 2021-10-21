package org.orcid.core.orgs.load.source.fighshare.api;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "revision", "firstOnline", "posted" })
public class FigshareCollectionTimeline {
    
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @JsonProperty("revision")
    private String revision;
    @JsonProperty("firstOnline")
    private String firstOnline;
    @JsonProperty("posted")
    private String posted;

    @JsonProperty("revision")
    public String getRevision() {
        return revision;
    }

    @JsonProperty("revision")
    public void setRevision(String revision) {
        this.revision = revision;
    }

    @JsonProperty("firstOnline")
    public String getFirstOnline() {
        return firstOnline;
    }

    @JsonProperty("firstOnline")
    public void setFirstOnline(String firstOnline) {
        this.firstOnline = firstOnline;
    }

    @JsonProperty("posted")
    public String getPosted() {
        return posted;
    }
    
    public Date getPostedAsDate() {
        try {
            return DATE_FORMAT.parse(posted);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonProperty("posted")
    public void setPosted(String posted) {
        this.posted = posted;
    }

}