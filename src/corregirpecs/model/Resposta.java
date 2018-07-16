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
	    		// lliure: valoració directa del professor
	    		this.punt = Float.parseFloat(this.resposta) * w;
	    	} else {
	    		boolean found = true;
	    		for (Opcio o: this.pregunta.correctes) {
        			// test: exact coincidence with one option
	    			if (t.equals(Tipo.TEST)) found = this.resposta.equalsIgnoreCase(o.value);
	        		
	        		if (t.equals(Tipo.NUMERICA)) {
	        			// numerica: value coincidence
	        			try {
		        			Float r = Float.parseFloat(this.resposta);
		        			Float c = Float.parseFloat(o.value);
		        			found = r.equals(c);
	        			} catch (Exception e) {
	        				// do nothing: keep trying
	        			}
	        		}
	        		
	        		// if coincidence, exit loop
	        		if (found) {
        				this.punt = w;
        				break;
	        		}
	    		}
	    	}	    	
    	}
    }
}
