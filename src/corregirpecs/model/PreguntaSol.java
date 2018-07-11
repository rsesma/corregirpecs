package corregirpecs.model;

public class PreguntaSol {
	private String nom;
	private Boolean anulada;
	private Boolean changed;
	
    public PreguntaSol() {
        this.anulada = false;
        this.changed = false;
    }
    
    public String getNom() {
        return this.nom;
    }

    public void setNom(String c) {
        this.nom = c;
    }

    public Boolean getAnulada() {
        return this.anulada;
    }

    public void setAnulada(Boolean l) {
        this.anulada = l;
    }
    
    public Boolean getChanged() {
        return this.changed;
    }

    public void setChanged(Boolean l) {
        this.changed = l;
    }
}
