package com.ar.dto;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable{
	private String title;
	private String subtitle;
	private double price;
	private int availableQuantity;
	
	public Item(JSONObject json) throws JSONException{
		this.title = json.getString("title");
		if(json.getString("subtitle") != "null") this.subtitle = json.getString("subtitle");
		this.price = json.getDouble("price");
		this.availableQuantity = json.getInt("available_quantity");
	}
	
	public Item(Parcel in){
		this.title = in.readString();
		this.price = in.readDouble();
		this.availableQuantity = in.readInt();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.title);
		dest.writeDouble(this.price);
		dest.writeInt(this.availableQuantity);
		
	}

	public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

	public String getTitle() {
		return title;
	}
	
	public String getSubtitle() {
		return subtitle;
	}
	
	public int getAvailableQuantity() {
		return availableQuantity;
	}
	
	public double getPrice() {
		return price;
	}
}
