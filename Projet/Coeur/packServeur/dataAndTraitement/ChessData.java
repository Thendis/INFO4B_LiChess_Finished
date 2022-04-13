package dataAndTraitement;

import java.util.*;
import java.io.*;

public class ChessData implements Serializable {
    private Hashtable<String,Integer>dataInt;
    private Hashtable<String,String>dataString;
    private Hashtable<String,ArrayList<Long>>dataPlayer;
    private Hashtable<Integer,Long> dataHashedTable;
    private int id;
    private String owner;
    private boolean isReturned;
    private int nbPartieMax;



    /*Getters and Setters*/
    

    public ChessData(String owner, int id){
        this.dataInt = null;
        this.dataString = null;
        this.id = id;
        this.owner = owner;
        this.isReturned = false;
        this.dataHashedTable = null;
    }

    public Hashtable<String, ArrayList<Long>> getDataPlayer() {
        return dataPlayer;
    }

    public void setDataPlayer(Hashtable<String, ArrayList<Long>> dataPlayer) {
        this.dataPlayer = dataPlayer;
    }

    public Hashtable<String, Integer> getDataInt() {
        return dataInt;
    }

    public void setDataInt(Hashtable<String, Integer> dataInt) {
        this.dataInt = dataInt;
    }

    public Hashtable<String, String> getDataString() {
        return dataString;
    }

    public void setDataString(Hashtable<String, String> dataString) {
        this.dataString = dataString;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean isReturned) {
        this.isReturned = isReturned;
    }

    public int getNbPartieMax() {
        return nbPartieMax;
    }

    public void setNbPartieMax(int nbPartieMax) {
        this.nbPartieMax = nbPartieMax;
    }

    public Hashtable<Integer, Long> getDataHashedTable() {
        return dataHashedTable;
    }

    public void setDataHashedTable(Hashtable<Integer, Long> dataHashedTable) {
        this.dataHashedTable = dataHashedTable;
    }


}
