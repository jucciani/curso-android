package com.ar.dto;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class Item implements Serializable {

    public final static String CONDITION_NEW = "new";
    private String id;
	private String title;
	private String subtitle;
	private double price;
	private int availableQuantity;
    private String thumbnailURL;
    private String pictureURL;
    private String condition;
    private String city;
    private String state;

    public Item(String itemId){
        this.id = itemId;
    }

	public Item(JSONObject json) throws JSONException {
        this.id = json.getString("id");
		this.title = json.getString("title");
		if(json.getString("subtitle") != "null") this.subtitle = json.getString("subtitle");
		this.price = json.getDouble("price");
		this.availableQuantity = json.getInt("available_quantity");
        this.thumbnailURL = json.getString("thumbnail");
        if(json.has("pictures")){
            this.pictureURL = json.getJSONArray("pictures").getJSONObject(0).getString("url");
        }
        if(json.has("condition")){
            this.condition = json.getString("condition");
        }
        if(json.has("seller_address")){
            if(json.getJSONObject("seller_address").has("state") &&
                    json.getJSONObject("seller_address").getJSONObject("state").has("name")){
                this.state = json.getJSONObject("seller_address").getJSONObject("state").getString("name");
            }
            if(json.getJSONObject("seller_address").has("city") &&
                    json.getJSONObject("seller_address").getJSONObject("city").has("name")){
                this.city = json.getJSONObject("seller_address").getJSONObject("city").getString("name");
            }
        }
	}

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

    public void setPrice(double price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public URL getThumbnailURL() {
        if(thumbnailURL == null || thumbnailURL.isEmpty()) return null;
        try {
            return new URL(thumbnailURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public URL getPictureURL() {
        if(pictureURL == null || pictureURL.isEmpty()) return null;
        try {
            return new URL(pictureURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCondition() {
        return condition;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }
}
