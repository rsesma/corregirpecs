package corregirpecs.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;

public class Solucio {
	public String pregunta;
	public Boolean anulada;
	public ArrayList<Opcio> opcions = new ArrayList<Opcio>();
	
	public Solucio(Item i, Integer size) {
		if (i != null) {
			this.pregunta = i.pregunta;
			this.anulada = false;
			
			for (Entry<String, Integer> entry : i.hm.entrySet()) {
				this.opcions.add(new Opcio(entry.getKey(), entry.getValue(), size));
			}
			
			// ordenar de major a menor % d'aparació
	    	Collections.sort(this.opcions, (o1, o2) -> o2.pct.compareTo(o1.pct));
		}
	}
	
	public Boolean UpdateCorrecte(String value, Boolean l) {
		Boolean last = true;
		for (Opcio o: this.opcions) {
			if (!o.value.equals(value)) {
				if (o.correcte) last = false;
			}
		}

		if (last && !l) {
			return false;
		} else {
			for (Opcio o: this.opcions) {
				if (o.value.equals(value)) o.correcte = l;
			}
			return true;
		}
	}
}
