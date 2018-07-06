package corregirpecs;

public class Resposta {
    public String nom;
    public String r;
    public float punt;
            
    
    public Resposta(Pregunta p, String r) {
    	this.r = r;
    	this.nom = p.nom;
    	this.punt = 0;
    }
}
