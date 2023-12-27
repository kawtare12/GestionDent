package com.machmoum.projetsnina.models;

import java.util.ArrayList;
import java.util.List;

public class Tooth {

    private long id;
    private String name;
    private List<PW> pws = new ArrayList<>();

    public Tooth() {
        // Default constructor
    }

    public Tooth(long id, String name, List<PW> pws) {
        this.id = id;
        this.name = name;
        this.pws = pws;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PW> getPws() {
        return pws;
    }

    public void setPws(List<PW> pws) {
        this.pws=pws;
}
}
