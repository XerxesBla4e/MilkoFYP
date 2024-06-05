package com.example.test4.Models;

public class Record {
    private long id;
    private String sourceAddress;
    private String adulterant;


    public Record() {
    }

    public Record(long id, String sourceAddress, String adulterant) {
        this.id = id;
        this.sourceAddress = sourceAddress;
        this.adulterant = adulterant;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getAdulterant() {
        return adulterant;
    }

    public void setAdulterant(String adulterant) {
        this.adulterant = adulterant;
    }
}
