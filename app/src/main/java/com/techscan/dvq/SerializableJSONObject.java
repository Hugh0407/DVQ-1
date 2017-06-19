package com.techscan.dvq;

import java.io.Serializable;

import org.json.JSONObject;

public class SerializableJSONObject implements Serializable {
	 
    private JSONObject json;
 
    public JSONObject getJs() {
        return json;
    }
 
    public void setJs(JSONObject json) {
        this.json = json;
    }
}
