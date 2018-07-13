package corregirpecs.model;

import java.util.ArrayList;

public class PEC {
    public String dni;
    public Boolean honor;
    public ArrayList<Resposta> resp = new ArrayList<Resposta>();
    public float nota;
    
    public PEC(ArrayList<Pregunta> plantilla, String c) {
    	String[] d = c.split(";",-1);

    	// dni de l'alumne & honor: primer token
    	String[] t = d[0].split(",");
    	this.dni = t[0];
    	if (t.length>1) this.honor = (t[1].equals("1"));

    	// loop per les respostes
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
