package corregirpecs.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;

public class OpcioSol {
	private String valor;
	private String pct;
	private Boolean correcte;
	public Boolean solucio;
	
	public Opcio op;
	private TableView<OpcioSol> respostes;
	
    public OpcioSol(Opcio o, TableView<OpcioSol> respostes) {
        this.valor = o.value;
        NumberFormat formatter = new DecimalFormat("##0.000");     
        this.pct = formatter.format(o.pct);
        this.correcte = o.correcte;
        this.solucio = o.solucio;
        
        this.respostes = respostes;
        this.op = o;
    }
    
    public String getValor() {
        return this.valor;
    }

    public void setValor(String c) {
        this.valor = c;
    }

    public String getPCT() {
        return this.pct;
    }

    public void setPCT(String c) {
        this.pct = c;
    }
    
    public Boolean getCorrecte() {
        return this.correcte;
    }

    public void setCorrecte(Boolean l) {
    	// controlar que hi ha almenys una opció correcta, i que la solució és correcta
		Boolean last = true;
		Boolean solucio = false;
    	for (OpcioSol o : this.respostes.getItems()) {
    		if (!o.valor.equals(this.valor)) {
				if (o.correcte) last = false;
			} else {
				if (o.solucio) solucio = true;
			}
    	}
    	
    	if (last && !l) {
    		ShowAlert("Hi ha d'haver almenys una opció correcta","Atenció",AlertType.WARNING);
    		this.respostes.refresh();
    	} else if (solucio && !l) {
    		ShowAlert("La opció solució ha de ser també la correcta","Atenció",AlertType.WARNING);
    		this.respostes.refresh();
    	} else {
    		this.op.correcte = l;
    		this.correcte = l;
    	}
    }
    
    public Boolean getSolucio() {
        return this.solucio;
    }

    public void setSolucio(Boolean l) {
    	
    	if (this.solucio && !l) {
    		ShowAlert("Hi ha d'haver almenys una opció solució","Atenció",AlertType.WARNING);
    		this.respostes.refresh();
    	} else {
        	for (OpcioSol o : this.respostes.getItems()) {
        		if (o.solucio) {
    				o.solucio = false;
    				o.op.solucio = false;
    			}
        	}
    		this.op.solucio = l;
    		this.solucio = l;
    		
    		this.correcte = true;
    		this.op.correcte = true;
			this.respostes.refresh();
    	}
    }
    
    public void ShowAlert(String message, String title, AlertType type) {
    	Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
