package corregirpecs;

import java.util.HashMap;

public class Item {
    public String pregunta;
    HashMap<String, Integer> hm;

    public Item(String p) {
    	this.pregunta= p;
    	this.hm = new HashMap<>();
    }
    
    public void Add(String value) {
		if (this.hm.containsKey(value)) {
			Integer frec = hm.get(value);
			hm.put(value, frec + 1);
		} else {
			hm.put(value, 1);
		}
    }
}
