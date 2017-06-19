package com.techscan.dvq;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
//–Ú¡–ªØlist
public class SerializableList implements Serializable {
	 
    private List<Map<String,Object>> list;
 
    public List<Map<String,Object>> getList() {
        return list;
    }
 
    public void setList(List<Map<String,Object>> list) {
        this.list = list;
    }
}
