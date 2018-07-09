package corregirpecs;

import java.util.ArrayList;

public class PEC {
    public String dni;
    public Boolean honor;
    public ArrayList<Resposta> resp = new ArrayList<Resposta>();
    
    public PEC(ArrayList<Pregunta> plantilla, String c) {
    	String[] d = c.split(";",-1);

    	// dni de l'alumne i honor: primer token
    	String[] t = d[0].split(",");
    	this.dni = t[0];
    	if (t.length>1) this.honor = (t[1].equals("1"));

    	// loop per les respostes
    	Integer iConta = 1;
    	for (Pregunta p : plantilla) {
    		this.resp.add(new Resposta(p.nom,d[iConta]));
    		iConta = iConta + 1;
    	}
    }
}
