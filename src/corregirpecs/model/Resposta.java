package corregirpecs.model;

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
    	
    }
}
