package corregirpecs.model;

import java.util.ArrayList;
import java.util.Collections;

public class Pregunta {
    public int num;
    public String nom;
    public Tipo tipo;
    public float w;
    public int numopc;
    public Boolean anulada;
    public ArrayList<Opcio> correctes = new ArrayList<Opcio>();

    public enum Tipo {
        NUMERICA, TEST, LLIURE
    }    
    
    public Pregunta(String c) {    	
    	String[] d = c.split(",");
    	this.num = Integer.parseInt(d[0]);
    	this.nom = d[1].replace("'", "");
    	switch (d[2]) {
	        case "1": this.tipo = Tipo.LLIURE;
	        	break;
	        case "2": this.tipo = Tipo.TEST;
	        	break;
	        case "3": this.tipo = Tipo.NUMERICA;
        		break;
    	}
    	this.w = Float.parseFloat(d[4]);
    	if (this.tipo == Tipo.TEST) this.numopc = Integer.parseInt(d[5]);
    	else this.numopc = 0;
    }
    
    public void SetSolucio(Solucio s) {
    	this.anulada = s.anulada;    	
    	// add opcions correctes & put the solucio first
    	for (Opcio o: s.opcions) {
    		if (o.correcte) this.correctes.add(o);
    	}
    	Collections.sort(this.correctes, (o1, o2) -> o2.solucio.compareTo(o1.solucio));
    }
    
    public String getSolucions() {
    	String c = "";
    	Boolean first = true;
    	if (!this.anulada && !this.tipo.equals(Tipo.LLIURE)) {
	    	for (Opcio o: this.correctes) {
	    		c = c + (!first ? "|" : "") + o.value;
	    		first = false;
	    	}
    	}
    	return c;
    }
}
