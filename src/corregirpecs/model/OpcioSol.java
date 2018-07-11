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
	private Boolean solucio;
	private Boolean changed;
	
	private Solucio sol;
	private TableView<OpcioSol> respostes;
	
    public OpcioSol(Opcio o, TableView<OpcioSol> r, Solucio sol) {
        this.valor = o.value;
        NumberFormat formatter = new DecimalFormat("##0.000");     
        this.pct = formatter.format(o.pct);
        this.correcte = o.correcte;
        this.solucio = o.solucio;
        
        this.changed = false;
        this.respostes = r;
        this.sol = sol;
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
    	if (this.sol.UpdateCorrecte(this.valor, l)) {
    		this.correcte = l;
    	} else {
    		ShowAlert("Hi ha d'haver almenys una opció correcte","Error",AlertType.ERROR);
    		this.respostes.refresh();
    	}
    }
    
    public Boolean getSolucio() {
        return this.solucio;
    }

    public void setSolucio(Boolean l) {
        this.solucio = l;
    }

    public Boolean getChanged() {
        return this.changed;
    }

    public void setChanged(Boolean l) {
        this.changed = l;
    }
    
    public void ShowAlert(String message, String title, AlertType type) {
    	Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
