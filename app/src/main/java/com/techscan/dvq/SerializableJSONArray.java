package com.techscan.dvq;

import java.io.Serializable;

import org.json.JSONArray;


public class SerializableJSONArray implements Serializable {
   	 
        private JSONArray jsonArray;
     
        public JSONArray getJsArray() {
            return jsonArray;
        }
     
        public void setJsArray(JSONArray json) {
            this.jsonArray = json;
        }
    }
