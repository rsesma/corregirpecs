package corregirpecs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;

public class Solucio {
	public Integer num;
	public String pregunta;
	public ArrayList<Opcio> opcions = new ArrayList<Opcio>();
	
	public Solucio(Integer n, Item i, Integer size) {
		this.num = n;
		this.pregunta = i.pregunta;
		
		for (Entry<String, Integer> entry : i.hm.entrySet()) {
			opcions.add(new Opcio(entry.getKey(), entry.getValue(), size));
		}
		// ordenar de major a menor % d'aparació
    	Collections.sort(opcions, (o1, o2) -> o2.pct.compareTo(o1.pct));
	}
}
