package corregirpecs.model;

public class Opcio {
	public String value;
	public Double pct;
	public Boolean correcte;
	public Boolean solucio;
	
	public Opcio(String v, Integer f, Integer n) {
		if (n>0) {
			this.value = v;
			this.pct = 100 * ((double) f)/n;
			this.correcte = false;
			this.solucio = false;
		}
	}
}
