package com.galaxybookpublication.modelapi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PdfLinkApiResponse {
    @SerializedName("file_url")
    @Expose
    private String file_url;

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }
}
