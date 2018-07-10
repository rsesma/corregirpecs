package corregirpecs.model;

public class PreguntaSol {
	private String nom;
	private Boolean anulada;
	
    public PreguntaSol() {
        this.anulada = false;
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
}
