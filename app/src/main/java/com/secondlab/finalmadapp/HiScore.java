package com.secondlab.finalmadapp;

public class HiScore<HiScore> {
    int _id;
    String _name;
    int _hiscore;
    public HiScore(){   }
    public HiScore(int id, String name, int hiscore){
        this._id = id;
        this._name = name;
        this._hiscore = hiscore;
    }

    public HiScore(String name, int hiscore){
        this._name = name;
        this._hiscore = hiscore;
    }

    public void setID(int id){
        this._id = id;
    }

    public String getName(){
        return this._name;
    }

    public void setName(String name){
        this._name = name;
    }

    public int getHiscore(){
        return this._hiscore;
    }

    public void setHiscore(int hiscore){
        this._hiscore = hiscore;
    }
}

