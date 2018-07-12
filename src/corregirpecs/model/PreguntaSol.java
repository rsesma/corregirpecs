package corregirpecs.model;

public class PreguntaSol {
	private String nom;
	private Boolean anulada;
	
	private Solucio sol;
	
    public PreguntaSol(Solucio s) {
        this.sol = s;
        this.nom = this.sol.pregunta;
        this.anulada = this.sol.anulada;
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
    	this.sol.anulada = l;
        this.anulada = l;
    }
}
