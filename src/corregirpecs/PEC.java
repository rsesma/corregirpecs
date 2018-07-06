package corregirpecs;

import java.util.ArrayList;

public class PEC {
    public String dni;
    public ArrayList<Resposta> resp = new ArrayList<Resposta>();
    
    public PEC(ArrayList<Pregunta> plantilla, String c) {
    	String[] d = c.split(",");
    	
    	this.dni = d[3].replace("'", "");
    }
}
