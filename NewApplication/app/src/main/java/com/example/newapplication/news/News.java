package com.example.newapplication.news;

import java.util.List;

public class News {
    private String status;
    private List<Sources> sources;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Sources> getSources() {
        return sources;
    }

    public void setSources(List<Sources> sources) {
        this.sources = sources;
    }

    public News(String status, List<Sources> sources) {
        this.status = status;
        this.sources = sources;
    }
}
