package corregirpecs.model;

import java.util.HashMap;

import corregirpecs.model.Pregunta.Tipo;

public class Item {
    public String pregunta;
    public Boolean esLliure;
    HashMap<String, Integer> hm;

    public Item(Pregunta p) {
    	this.pregunta= p.nom;
    	this.esLliure = (p.tipo == Tipo.LLIURE);
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
