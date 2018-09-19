package com.example.haris.httpserve;

import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("id")
    public int id;

//    @SerializedName("item_name")
    public String name;

    public Item(int id, String name){
        this.id = id;
        this.name = name;
    }

}
