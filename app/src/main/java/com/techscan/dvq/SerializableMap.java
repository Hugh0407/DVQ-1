package com.techscan.dvq;

import java.io.Serializable;
import java.util.Map;
//–Ú¡–ªØmap
public class SerializableMap implements Serializable {
 
    private Map<String,Object> map;
 
    public Map<String, Object> getMap() {
        return map;
    }
 
    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}

