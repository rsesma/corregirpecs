package corregirpecs.model;

public class Pregunta {
    public int num;
    public String nom;
    public Tipo tipo;
    public float w;
    public int numopc;
    public Solucio sol;
            
    public enum Tipo {
        NUMERICA, TEST, LLIURE
    }
    
    public Pregunta(String c) {    	
    	String[] d = c.split(",");
    	this.num = Integer.parseInt(d[0]);
    	this.nom = d[1].replace("'", "");
    	switch (d[2]) {
	        case "1": this.tipo = Tipo.LLIURE;
	        	break;
	        case "2": this.tipo = Tipo.TEST;
	        	break;
	        case "3": this.tipo = Tipo.NUMERICA;
        		break;
    	}
    	this.w = Float.parseFloat(d[4]);
    	if (this.tipo == Tipo.TEST) this.numopc = Integer.parseInt(d[5]);
    	else this.numopc = 0;
    }
    
    public void SetSolucio(Solucio s) {
    	this.sol = s;
    }
}
