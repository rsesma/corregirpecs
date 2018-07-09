package corregirpecs;

import java.util.ArrayList;

public class PEC {
    public String dni;
    public Boolean honor;
    public ArrayList<Resposta> resp = new ArrayList<Resposta>();
    
    public PEC(ArrayList<Pregunta> plantilla, String c, Boolean presencial) {
    	String[] d = c.split(",");
    	
    	// dni de l'alumne: posició 3 (0-based)
    	this.dni = d[3].replace("'", "");
    	
    	// honor: si no és presencial, posició 4
    	Integer iConta = 5;
    	if (!presencial) {
    		this.honor = (d[4].equals("1"));
    	} else {
    		iConta = 4;
    		this.honor = null;
    	}

    	// loop per les respostes
    	for (Pregunta p : plantilla) {
    		this.resp.add(new Resposta(p.nom,d[iConta]));
    		iConta = iConta + 1;
    	}

    }
}
