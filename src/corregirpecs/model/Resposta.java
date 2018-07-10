package corregirpecs.model;

import corregirpecs.model.Pregunta.Tipo;

public class Resposta {
    public String nom;
    public String resp;
    public float punt;
    public Tipo tipo;
    
    public Resposta(String n, String r, Tipo t) {
    	this.resp = r;
    	this.nom = n;
    	this.tipo = t;
    	this.punt = 0;
    }
}
