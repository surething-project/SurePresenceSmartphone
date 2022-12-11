package pt.ulisboa.tecnico.surething.prover.classes;

import android.location.Location;

public class ContextualProof {
    private String id;
    private long time; // miliseconds from the Unix epoch
    private Location location;
    private String address;
    private String city;
    private int sentToVerifier;
    private boolean endorsed;
    private String witnessID;

    public ContextualProof(String id, long time, Location location, String address, String city, int sentToVerifier, boolean endorsed, String witnessID) {
        this.id = id;
        this.time = time;
        this.location = location;
        this.address = address;
        this.city = city;
        this.sentToVerifier = sentToVerifier;
        this.endorsed = endorsed;
        this.witnessID = witnessID;
    }

    public ContextualProof(String id, long time, Location location) {
        this.id = id;
        this.time = time;
        this.location = location;
    }

    public ContextualProof(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int isSentToVerifier() {
        return sentToVerifier;
    }

    public void setSentToVerifier(int sentToVerifier) {
        this.sentToVerifier = sentToVerifier;
    }

    public boolean isEndorsed() {
        return endorsed;
    }

    public void setEndorsed(boolean endorsed) {
        this.endorsed = endorsed;
    }

    public String getWitnessID() {
        return witnessID;
    }

    public void setWitnessID(String witnessID) {
        this.witnessID = witnessID;
    }
}
