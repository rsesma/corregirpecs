package corregirpecs.model;

import java.util.ArrayList;

public class PEC {
	public String ape1;
	public String ape2;
	public String nom;
    public String dni;
    public Boolean honor;
    public ArrayList<Resposta> resp = new ArrayList<Resposta>();
    public float nota;
    
    public PEC(ArrayList<Pregunta> plantilla, String c) {
    	String[] d = c.split(";",-1);

    	// first token is header: ape1, ape2, nom, dni [, honor]
    	String[] t = d[0].split("\t");
    	this.ape1 = t[0];
    	this.ape2 = t[1];
    	this.nom = t[2];
    	this.dni = t[3];
    	if (t.length>3) this.honor = (t[4].equals("1"));
    	else this.honor = null;

    	// respostes loop
    	Integer iConta = 1;
    	for (Pregunta p : plantilla) {
    		this.resp.add(new Resposta(p,d[iConta]));
    		iConta = iConta + 1;
    	}
    }
    
    public void CalculaNota(Float wsum) {
		this.nota = 0;
    	for (Resposta r : this.resp) {
			r.Corregeix();
			this.nota = this.nota + r.punt;
		}
    	this.nota = 10* (this.nota/wsum);
    	this.nota = Math.round(this.nota*10f)/10f;
    }
}
