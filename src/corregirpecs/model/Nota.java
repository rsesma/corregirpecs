package corregirpecs.model;

public class Nota {
    public String dni;
    public float nota;
    
    public Nota(String dni, Float nota) {
    	this.dni = dni;
    	this.nota = nota;
    }

    public String getDNI() {
        return this.dni;
    }

    public void setValor(String c) {
        this.dni = c;
    }

    public float getNota() {
        return this.nota;
    }

    public void setNota(float f) {
        this.nota = f;
    }
}
