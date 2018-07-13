package corregirpecs.model;

import corregirpecs.model.Pregunta.Tipo;

public class Resposta {
	public Pregunta pregunta;
    public String resposta;
    public float punt;
    
    public Resposta(Pregunta p, String r) {
    	this.pregunta = p;
    	this.resposta = r;
    	this.punt = 0;
    }
    
    public void Corregeix() {
    	Tipo t = this.pregunta.tipo;
    	Float w = this.pregunta.w;
    	
    	if (!this.pregunta.anulada && !this.resposta.isEmpty()) {
	    	if (t.equals(Tipo.LLIURE)) {
	    		// reposta lliure: valoració directa del professor
	    		this.punt = Float.parseFloat(this.resposta) * w;
	    	} else {
	    		boolean found = true;
	    		for (Opcio o: this.pregunta.correctes) {
	        		if (t.equals(Tipo.TEST)) {
	        			// resposta tipus test: coincidència exacta per una de les opcions vàlides
	        			found = this.resposta.equalsIgnoreCase(o.value);
	        		}
	        		
	        		if (t.equals(Tipo.NUMERICA)) {
	        			// resposta numèrica: coincidència numèrica per valor
	        			try {
		        			Float r = Float.parseFloat(this.resposta);
		        			Float c = Float.parseFloat(o.value);
		        			found = r.equals(c);
	        			} catch (Exception e) {
	        				// do nothing: keep trying
	        			}
	        		}
	        		
	        		// si es troba coincidència, ja es pot sortir del loop
	        		if (found) {
        				this.punt = w;
        				break;
	        		}
	    		}
	    	}	    	
    	}
    }
}
