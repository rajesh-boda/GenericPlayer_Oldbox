package com.ideabytes.qezytv.genericplayer.database;



/**
 * Created by Hareesh on 11/5/2016.
 */
public class Inactive_status {

    //private variables
    int _id;
    String _name;
    String _status;

    // Empty constructor
    public Inactive_status(){

    }
    // constructor
    public Inactive_status(int id, String name, String _status){
        this._id = id;
        this._name = name;
        this._status = _status;
    }

    // constructor
    public Inactive_status(String name, String _status){
        this._name = name;
        this._status = _status;
    }
    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting name
    public String getName(){
        return this._name;
    }

    // setting name
    public void setName(String name){
        this._name = name;
    }

    // getting phone number
    public String getInactiveStatus(){
        return this._status;
    }

    // setting phone number
    public void setInactiveStatus(String _status){
        this._status = _status;
    }
}
