package corregirpecs.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class OpcioSol {
	private String valor;
	private String pct;
	private Boolean correcte;
	private Boolean solucio;
	private Boolean changed;
	
    public OpcioSol(Opcio o) {
        this.valor = o.value;
        NumberFormat formatter = new DecimalFormat("##0.000");     
        this.pct = formatter.format(o.pct);
        this.correcte = o.correcte;
        this.solucio = o.solucio;
        
        this.changed = false;
    }
    
    public String getValor() {
        return this.valor;
    }

    public void setValor(String c) {
        this.valor = c;
    }

    public String getPCT() {
        return this.pct;
    }

    public void setPCT(String c) {
        this.pct = c;
    }
    
    public Boolean getCorrecte() {
        return this.correcte;
    }

    public void setCorrecte(Boolean l) {
        this.correcte = l;
    }
    
    public Boolean getSolucio() {
        return this.solucio;
    }

    public void setSolucio(Boolean l) {
        this.solucio = l;
    }

    public Boolean getChanged() {
        return this.changed;
    }

    public void setChanged(Boolean l) {
        this.changed = l;
    }
}
