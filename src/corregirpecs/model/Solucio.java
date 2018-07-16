package corregirpecs.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;

public class Solucio {
	public String pregunta;
	public Boolean anulada;
	public Boolean esLliure;
	public ArrayList<Opcio> opcions = new ArrayList<Opcio>();
	
	public Solucio(Item i, Integer size) {
		if (i != null) {
			this.pregunta = i.pregunta;
			this.anulada = false;
			this.esLliure = i.esLliure;
			
			if (!this.esLliure) {
				for (Entry<String, Integer> entry : i.hm.entrySet()) {
					this.opcions.add(new Opcio(entry.getKey(), entry.getValue(), size));
				}
				// descendent sort
		    	Collections.sort(this.opcions, (o1, o2) -> o2.pct.compareTo(o1.pct));
			}
		}
	}
}
