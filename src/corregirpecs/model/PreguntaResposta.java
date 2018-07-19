package corregirpecs.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class PreguntaResposta {
	public Solucio sol = null;
	public Opcio opc = null;
	public Boolean subresposta;
	
	public NumberFormat formatter;
	
	
    public PreguntaResposta(Solucio s, Opcio o, Boolean sub) {
		this.sol = s;
        this.opc = o;
        this.subresposta = sub;
        
        this.formatter = new DecimalFormat("##0.000");     
    }
    
    public String getNom() {
        return (this.subresposta ? "" : this.sol.pregunta);
    }

    public Boolean getAnulada() {
        return this.sol.anulada;
    }
    
    public String getValor() {
        return this.opc.value;
    }

    public String getPCT() {
        return this.formatter.format(this.opc.pct);
    }
    
    public Boolean getCorrecte() {
        return this.opc.correcte;
    }
    
    public Boolean getSolucio() {
        return this.opc.solucio;
    }

    public void setAnulada(Boolean l) {
    	this.sol.anulada = l;
    }

    public void setCorrecte(Boolean l) {
    	this.opc.correcte = l;
/*    	// check for at least one correcte & solucio option
		Boolean last = true;
		Boolean solucio = false;
    	for (OpcioSol o : this.respostes.getItems()) {
    		if (!o.valor.equals(this.valor)) {
				if (o.correcte) last = false;
			} else {
				if (o.solucio) solucio = true;
			}
    	}
    	
    	if (last && !l) {
    		ShowAlert(C_UNA_CORRECTA,C_ATENCIO,AlertType.WARNING);
    		this.respostes.refresh();
    	} else if (solucio && !l) {
    		ShowAlert(C_SOLUCIO_CORRECTA,C_ATENCIO,AlertType.WARNING);
    		this.respostes.refresh();
    	} else {
    		this.op.correcte = l;
    		this.correcte = l;
    	}*/
    }
    

    public void setSolucio(Boolean l) {
    	this.opc.correcte = l;
/*    	if (this.solucio && !l) {
    		ShowAlert(C_UNA_SOLUCIO,C_ATENCIO,AlertType.WARNING);
    		this.respostes.refresh();
    	} else {
        	for (OpcioSol o : this.respostes.getItems()) {
        		if (o.solucio) {
    				o.solucio = false;
    				o.op.solucio = false;
    			}
        	}
    		this.op.solucio = l;
    		this.solucio = l;
    		
    		this.correcte = true;
    		this.op.correcte = true;
			this.respostes.refresh();
    	}*/
    }
}
