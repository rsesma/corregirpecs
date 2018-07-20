package corregirpecs.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;


public class PreguntaResposta {
	public Solucio sol = null;
	public Opcio opc = null;
	public Boolean subresposta;
	
	public NumberFormat formatter;
	
	public TableView<PreguntaResposta> pregresp;
	
	private final String C_ATENCIO = "Atenció";
	private final String C_CORRECTA_SOLUCIO = "La resposta solució ha de ser correcta";
	private final String C_UNA_SOLUCIO = "Hi ha d'haver una resposta solució";
	
	
    public PreguntaResposta(Solucio s, Opcio o, Boolean sub, TableView<PreguntaResposta> p) {
		this.sol = s;
        this.opc = o;
        this.subresposta = sub;
        
        this.formatter = new DecimalFormat("##0.000");     
        
        this.pregresp = p;
    }
    
    public String getNom() {
        return (this.subresposta ? "" : this.sol.pregunta);
    }

    public Boolean getAnulada() {
        return (this.subresposta ? false : this.sol.anulada);
    }
    
    public String getValor() {
        return this.opc.value;
    }

    public String getPCT() {
        return this.formatter.format(this.opc.pct);
    }
    
    public Boolean getCorrecte() {
        return this.opc.correcte;
    }
    
    public Boolean getSolucio() {
        return this.opc.solucio;
    }

    public void setAnulada(Boolean l) {
    	this.sol.anulada = l;
    	this.pregresp.refresh();
    }

    public void setCorrecte(Boolean l) {
    	if (this.opc.solucio && !l) {
    		// la resposta correcta ha de ser solució
    		ShowAlert(C_CORRECTA_SOLUCIO,C_ATENCIO,AlertType.WARNING);
    		this.pregresp.refresh();
    	} else {
    		this.opc.correcte = l;
    	}
    }

    public void setSolucio(Boolean l) {
    	if (this.opc.solucio && !l) {
    		// hi ha d'haver al menys una solució
    		ShowAlert(C_UNA_SOLUCIO,C_ATENCIO,AlertType.WARNING);
    	} else {
    		// toggle: només hi ha una resposta solució 
        	for (Opcio o : this.sol.opcions) {
        		if (o.solucio) o.solucio = false;
        	}
    		this.opc.solucio = l;
    		this.opc.correcte = l;    		
    	}
		this.pregresp.refresh();
    }
    
    public void ShowAlert(String message, String title, AlertType type) {
    	Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
