package corregirpecs;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;

import corregirpecs.model.Item;
import corregirpecs.model.Opcio;
import corregirpecs.model.OpcioSol;
import corregirpecs.model.PEC;
import corregirpecs.model.Pregunta;
import corregirpecs.model.Pregunta.Tipo;
import corregirpecs.model.PreguntaResposta;
import corregirpecs.model.PreguntaSol;
import corregirpecs.model.Resposta;
import corregirpecs.model.Solucio;
import corregirpecs.model.Stat;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AnalitzarController implements Initializable {
    
    private final String C_DEFDIR = System.getProperty("user.home");
    //private final String C_DEFDIR = "/Users/r/Desktop/CorregirPECs/2017-18_PEC4_DE0";
    //private final String C_DEFDIR = "/home/drslump/Escritorio/CorregirPECs/2017-18_PEC4_DE0";
    //private final String C_DEFDIR = "C:\\Users\\tempo\\Desktop\\CorregirPECs\\2017-18_PEC4_DE0";
    private final Boolean L_TEST = false ;

	
    @FXML
    private TextField plantillaFile;
    @FXML
    private TextField pecsDir;
	@FXML
    private TableView<PreguntaResposta> pregresp;
    @FXML
    private TableColumn<PreguntaResposta, Boolean> anulCol;
    @FXML
    private TableColumn<PreguntaResposta, String> nomCol;
    @FXML
    private TableColumn<PreguntaResposta, String> respCol;
    @FXML
    private TableColumn<PreguntaResposta, String> pctCol;
    @FXML
    private TableColumn<PreguntaResposta, Boolean> corrCol;
    @FXML
    private TableColumn<PreguntaResposta, Boolean> solCol;    

    private ArrayList<Solucio> sol = null;
    private ArrayList<Pregunta> Plantilla = null;
    private ArrayList<PEC> PECs = null;
    
    private String savedir = "";
    
    final ObservableList<PreguntaResposta> pr= FXCollections.observableArrayList();
    
    private final String C_ANALISI = "analisi.txt";
    private final String C_DADES_PECS = "dades_pecs.txt";
    private final String C_COMENTARIS_TXT = "comentaris.txt";
    private final String C_PDF = "*.pdf";
    private final String C_PROBLEMES = "problemes";
    
    private final String C_HI_HA_COMENTARIS = "Hi ha comentaris";
    private final String C_COMENTARIS_GRABATS = "Els comentaris s'han grabat a l'arxiu " + C_COMENTARIS_TXT;
    
    private final String C_ERROR = "Error";
    private final String C_ATENCIO = "Atenció";
    private final String C_INFORMACIO = "Informació";
    private final String C_COMENTARIS = "Comentaris";
    private final String C_SELECCIONAR_CARPETA = "Seleccionar carpeta amb les PECs";
    private final String C_SELECCIONAR_PLANTILLA = "Seleccionar la plantilla de la solució";
    private final String C_PLANTILLA_NO_EXISTEIX = "L'arxiu de plantilla no existeix";
    private final String C_HI_HA_PROBLEMES = "Hi ha PECs amb problemes.\n\nVeure la carpeta problemes";
    private final String C_FINAL = "Procés finalitzat";
    private final String C_INDICAR_CARPETA = "Indicar la carpeta de les PECs";
    private final String C_CARPETA_NO_EXISTEIX = "La carpeta amb les PECs no existeix";
    private final String C_GRABACIO_COMPLETADA = "Grabació completada";
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // config preguntas table
        this.pregresp.setEditable(true);
        this.pregresp.setItems(this.pr);
        this.nomCol.setCellValueFactory(new PropertyValueFactory<>("Nom"));
        this.respCol.setCellValueFactory(new PropertyValueFactory<>("Valor"));
        this.pctCol.setCellValueFactory(new PropertyValueFactory<>("PCT"));
  
        // checkbox anular
        this.anulCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PreguntaResposta, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<PreguntaResposta, Boolean> param) {
                PreguntaResposta p = param.getValue();
                if (!p.subresposta) {
	                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(p.getAnulada());
	                // when column change
	                booleanProp.addListener(new ChangeListener<Boolean>() {
	                    @Override
	                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
	                            Boolean newValue) { 
	                        p.setAnulada(newValue);
	                     }
	                });
	                return booleanProp;
                } else {
                	return null;
                }
            }
        });
        this.anulCol.setCellFactory(column -> {
        	return new CheckBoxTableCell<PreguntaResposta, Boolean>() {
        		@Override
				public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    setAlignment(Pos.CENTER);
        			if (this.getItem() == null) setVisible(false);
                }
        	};
        });

        // checkbox correcte
        this.corrCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PreguntaResposta, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<PreguntaResposta, Boolean> param) {
            	PreguntaResposta p = param.getValue();
                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(p.getCorrecte());
                // when column change
                booleanProp.addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                            Boolean newValue) { 
                       p.setCorrecte(newValue);
                    }
                });
                return booleanProp;
            }
        });
        this.corrCol.setCellFactory(column -> {
        	return new CheckBoxTableCell<PreguntaResposta, Boolean>() {
        		@Override
				public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    setAlignment(Pos.CENTER);
                }
        	};
        });

        // checkbox solucio
        this.solCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PreguntaResposta, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<PreguntaResposta, Boolean> param) {
            	PreguntaResposta p = param.getValue();
                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(p.getSolucio());
                // when column change
                booleanProp.addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                            Boolean newValue) { 
                        p.setSolucio(newValue);
                    }
                });
                return booleanProp;
            }
        });
        this.solCol.setCellFactory(column -> {
        	return new CheckBoxTableCell<PreguntaResposta, Boolean>() {
        		@Override
				public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    setAlignment(Pos.CENTER);
                }
        	};
        });

        // TEST
        if (L_TEST) {
//	        this.dir.setText(C_DEFDIR);
//	        Collection<File> files = FileUtils.listFiles(new File(dir.getText()), new WildcardFileFilter("analisi.txt"), null);
//	        this.CarregaAnalisi(files.iterator().next());
        }
    }

    @FXML
    private void getArxiuPlantilla(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(C_SELECCIONAR_PLANTILLA);
        fileChooser.setInitialDirectory(new File(C_DEFDIR));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
        	this.plantillaFile.setText(file.getAbsolutePath());
        	this.GetPlantilla();
        } else this.plantillaFile.setText("");
    }

    @FXML
    private void getDirPECs(ActionEvent event) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(C_SELECCIONAR_CARPETA);
        directoryChooser.setInitialDirectory(new File(C_DEFDIR));
        File folder = directoryChooser.showDialog(null);
        if (folder != null) {
        	this.pecsDir.setText(folder.getAbsolutePath());
        	String c = folder.getAbsolutePath();
        	this.savedir = c.substring(0,c.lastIndexOf(File.separator));
        	this.CheckPECs();
        	this.pr.clear();
        	Collection<File> files = FileUtils.listFiles(new File(this.savedir), new WildcardFileFilter(C_ANALISI), null);
        	if (!files.isEmpty()) this.CarregaAnalisi(files.iterator().next());
        }
    }
    
    @FXML
    private void pbExtreure(ActionEvent event) {
    	if (this.CheckPECs()) {
    		Boolean lcontinue = false;
    		if (this.Plantilla==null) lcontinue = this.GetPlantilla();
    		else lcontinue = true;
    		if (lcontinue) {
	        	this.GetPECs();
	        	// if there's no analysis, get default
	        	Collection<File> files = FileUtils.listFiles(new File(this.savedir), new WildcardFileFilter(C_ANALISI), null);
	        	if (files.isEmpty()) this.DefAnalisi();
	        }
    	}
    }

    @FXML
    private void pbGrabar(ActionEvent event) {
    	this.Graba(true);
    }

    @FXML
    private void pbNotes(ActionEvent event) {
    	// get computed Notes from the defined solució
    	Boolean lcontinue = true;
    	
    	if (this.Plantilla==null) lcontinue = this.GetPlantilla();
    	if (lcontinue) {
    		if (this.PECs==null) {
    			Collection<File> files = FileUtils.listFiles(new File(this.savedir), new WildcardFileFilter(C_DADES_PECS), null);
	        	if (files.isEmpty()) {
	        		if (this.CheckPECs()) {
	        			lcontinue = true;
	        			this.GetPECs();
	        		}
	        	} else {
	        		this.CarregaPECs(files.iterator().next());
	        	}
    		}
    	}

    	if (lcontinue && this.sol!=null) {
	    	// load solucions to each plantilla and sum of weights
			float wsum = 0;
	    	for (Pregunta p : this.Plantilla) {
	    		wsum = wsum + p.w;
	    		for (Solucio s : this.sol) {
	    			if (s.pregunta.equals(p.nom)) {
	    				p.SetSolucio(s);
	    				break;
	    			}
	    		}
	    	}
		            	
	    	// compute PEC nota  	
	    	for (PEC p : PECs) {
	    		p.CalculaNota(wsum);
	    	}
		            	
			// get curs, any, numpec from the first available PEC
	    	String anc = "";
	    	Collection<File> files = FileUtils.listFiles(new File(this.pecsDir.getText()), new WildcardFileFilter(C_PDF), null);
	        for (File f : files) {
	            if (f.isFile()) {
	            	// get curso, pec number & year from PEC name
	                String n = f.getName();
	            	String curso = n.substring(n.indexOf("_")+1,n.lastIndexOf("_"));
	            	String pec = n.substring(0,n.indexOf("_")).replace("PEC", "");
	            	if (pec.isEmpty()) pec = "1";
	            	
	            	int year = LocalDateTime.now().getYear();
	            	int month = LocalDateTime.now().getMonthValue();
	           		String any = (month>=10 ? String.valueOf(year)+"-"+String.valueOf(year+1-2000) : 
	               			String.valueOf(year-1)+"-"+String.valueOf(year-2000));
	
	           		anc = curso + ";" + pec + ";" + any;
	           		
	           		break;
	            }
	        }
		            	
	    	// show computed notes
	        try {
	            FXMLLoader fxml = new FXMLLoader(getClass().getResource("Notes.fxml"));
	            Parent r = (Parent) fxml.load();
	            Stage stage = new Stage();
	            stage.initModality(Modality.APPLICATION_MODAL);
	            stage.setScene(new Scene(r));
	            stage.setTitle("Notes");
	            NotesController notes = fxml.<NotesController>getController();
	            notes.SetData(this.PECs,this.Plantilla,anc,this.savedir);
	            stage.showAndWait();
	        } catch(Exception e) {
	            System.out.println(e.getMessage());
	        }
    	}
    }
    
    @FXML
    private void pbTancar(ActionEvent event) {
        Stage stage = (Stage) this.plantillaFile.getScene().getWindow();
        stage.close();
    }

	public Boolean GetPlantilla() {
		Boolean lreturn = false;
		if (!this.plantillaFile.getText().isEmpty()) {
			File f = new File(this.plantillaFile.getText());
			if (f.exists()) {
				this.Plantilla = new ArrayList<Pregunta>();
				try {
					LineIterator it = FileUtils.lineIterator(f, "UTF-8");
			    	try {
			    		it.nextLine();		// first line has field names, not useful
			    	    while (it.hasNext()) {
			    	    	this.Plantilla.add(new Pregunta(it.nextLine()));
			    	    }
			    	    lreturn = true;
			    	} catch (Exception e) {
			        	ShowAlert(e.getMessage(),C_ERROR,AlertType.ERROR);
			        } finally {
			    	    it.close();
			    	}
			    } catch (Exception e) {
			    	ShowAlert(e.getMessage(),C_ERROR,AlertType.ERROR);
			    }
			} else ShowAlert(C_PLANTILLA_NO_EXISTEIX,C_ERROR,AlertType.ERROR);
		} else ShowAlert(C_SELECCIONAR_PLANTILLA,C_ERROR,AlertType.ERROR);
		return lreturn;
	}
	
	public Boolean CheckPECs() {
		Boolean lreturn = false;
		if (this.CheckDir()) {
	        Boolean lproblems = false;
	        File folder = new File(this.pecsDir.getText());						// PECs folder
	        File problems = new File(this.pecsDir.getText(),C_PROBLEMES);		// folder for problem files
	    	try {
		        for (File f : folder.listFiles()) {
					// loop for not hidden files
			        if (f.isFile() && !f.isHidden()) {
			        	// PDF?
			        	String ext = FilenameUtils.getExtension(f.getName());
			        	if (ext.equalsIgnoreCase("pdf")) {
			                PdfReader reader = new PdfReader(f.getAbsolutePath());
			                AcroFields form = reader.getAcroFields();
			                int nsize = form.getFields().size();
			                reader.close();
			                // fields?
			                if (nsize==0) {
			                	FileUtils.moveFileToDirectory(f, problems, true);
			                	lproblems = true;
			                }
			        	} else {
			        		FileUtils.moveFileToDirectory(f, problems, true);
			        		lproblems = true;
			        	}
			        }
			    }
		        lreturn = true;
				
				if (lproblems) ShowAlert(C_HI_HA_PROBLEMES,C_ATENCIO,AlertType.WARNING);
	    	} catch (Exception e) {
		    	ShowAlert(e.getMessage(),C_ERROR,AlertType.ERROR);
		    }
		}
		return lreturn;
	}
	
    public void GetPECs() {
    	// get data from PDF files and build PECs arraylist
    	this.PECs = new ArrayList<PEC>();
    	File folder = new File(this.pecsDir.getText());
        List<String> lines = new ArrayList<>();
        Boolean lcomments = false;
        List<String> comments = new ArrayList<>();

        // loop through the PDF files of the PECs folder
    	Collection<File> pdfs = FileUtils.listFiles(folder, new WildcardFileFilter(C_PDF), null);
        try {
	        for (File f : pdfs) {
	            if (f.isFile()) {
	                String n = f.getName();
	                String dni = n.substring(n.lastIndexOf("_")+1);
	                dni = dni.substring(0,dni.indexOf("."));
	                // open PEC
                    PdfReader reader = new PdfReader(f.getAbsolutePath());
                    AcroFields form = reader.getAcroFields();
                    if (form.getFields().size()>0) {
                        // header with id data
                        String c = form.getField("APE1") + "\t" + form.getField("APE2") + "\t" + 
                                form.getField("NOMBRE") + "\t" + dni;
                        try {
                        	c = c + "\t" + (form.getField("HONOR").equalsIgnoreCase("Yes") ? "1" : "0");
                        } catch (Exception e) {
                        	// do nothing: the HONOR field is not present (PEC presencial)
                        }
                        
                        // build COMMENTS section
                        if (!form.getField("COMENT").isEmpty()) {
                            lcomments = true;
                            comments.add(dni + ": " + form.getField("COMENT"));
                        }

                        // loop to get field data
                        for (Pregunta p : Plantilla) {
                            c = c + ";" + form.getField(p.nom).replace(",", ".");
                        }
                        lines.add(c);
                        
                        this.PECs.add(new PEC(this.Plantilla, c));		// add PEC to arraylist
                    }
                    reader.close();
	            }
	        }
	        // write file with PEC data	        
	        Files.write(Paths.get(this.savedir + File.separator + C_DADES_PECS), lines, Charset.forName("UTF-8"));

	        if (lcomments) {
	        	// write file with comments	        
		        Files.write(Paths.get(this.savedir + File.separator + C_COMENTARIS_TXT), comments, Charset.forName("UTF-8"));
		        
		        // show expandable dialog with comments
		        Alert alert = new Alert(AlertType.INFORMATION);
		        alert.setTitle(C_COMENTARIS);
		        alert.setHeaderText(C_HI_HA_COMENTARIS);
		        alert.setContentText(C_COMENTARIS_GRABATS);
	
		        String txt = "";
		        for (String c : comments) {
		        	txt = txt + c + "\n\n";
		        }
		        
		        TextArea textArea = new TextArea(txt);
		        textArea.setEditable(false);
		        textArea.setWrapText(true);
	
		        textArea.setMaxWidth(Double.MAX_VALUE);
		        textArea.setMaxHeight(Double.MAX_VALUE);
		        GridPane.setVgrow(textArea, Priority.ALWAYS);
		        GridPane.setHgrow(textArea, Priority.ALWAYS);
		        
		        GridPane content = new GridPane();
		        content.setMaxWidth(Double.MAX_VALUE);
		        content.add(textArea, 0, 0);
	
		        // set expandable Exception into the dialog pane
		        alert.getDialogPane().setExpandableContent(content);
		        alert.showAndWait();
	        } else ShowAlert(C_FINAL,C_INFORMACIO,AlertType.INFORMATION);
        } catch (Exception e) {
        	ShowAlert(e.getMessage(),C_ERROR,AlertType.ERROR);
        }
    }

    public void DefAnalisi() {
    	// default analysis: most frequent answer is correcte & solucio
    	// respostes statistic for each pregunta no lliure (numèriques or tipus test)
    	Stat st = new Stat(this.Plantilla);
    	for (PEC p : PECs) {
    		for (Resposta r: p.resp) {
    			if (r.pregunta.tipo != Tipo.LLIURE) st.getItem(r.pregunta.nom).Add(r.resposta);
    		}
    	}
    	// get all possibles solucions sort in descendant order of % 
    	this.sol = new ArrayList<Solucio>();
    	for (Item i: st.items) {
    		this.sol.add(new Solucio(i,PECs.size()));
    	}
    	// default: the most frequent (% - the 1st) option is correcte & solucio
    	for (Solucio s: this.sol) {
    		if (!s.esLliure) {
            	Opcio o = s.opcions.get(0);
            	o.correcte = true;
            	o.solucio = true;
    		}
    	}
		        	
    	this.Graba(false);		// save to file
		        	
    	// load table views
    	this.SetPreguntes();
    }
    
    public void Graba(Boolean alert) {
		NumberFormat formatter = new DecimalFormat("##0.000");
    	if (this.CheckDir()) {
    		if (this.sol!=null) {
	            List<String> lines = new ArrayList<>();
	            for (Solucio s : this.sol) {
	            	String c = s.pregunta + ";" + (s.anulada ? "1" : "0") + ";";
	            	if (!s.esLliure) {
		            	Boolean lfirst = true;
		            	for (Opcio o : s.opcions) {
		            		c = c + (lfirst ? "" : ",") + o.value + "\t" + formatter.format(o.pct).replace(",", ".");
		            		c = c + "\t" + (o.correcte ? "1" : "0") + "\t" + (o.solucio ? "1" : "0");
		            		lfirst = false;
		            	}
	            	} else {
	            		c = c + "0";
	            	}
	            	
	            	lines.add(c);
	            }
	            
	            // write file
	            try {
	            	Files.write(Paths.get(this.savedir + File.separator + C_ANALISI), lines, Charset.forName("UTF-8"));
	            	if (alert) ShowAlert(C_GRABACIO_COMPLETADA,"",AlertType.INFORMATION);
	            } catch (Exception e) {
	            	ShowAlert(e.getMessage(),C_ERROR,AlertType.ERROR);
	            }
    		}
    	}
    }
    
    public void CarregaAnalisi(File f) {
    	this.sol = new ArrayList<Solucio>();
		try {
			LineIterator it = FileUtils.lineIterator(f, "UTF-8");
	    	try {
	    	    while (it.hasNext()) {
	    	    	String line = it.nextLine();
	    	    	String[] fields = line.split(";");
	    	    	Solucio s = new Solucio(null,0);
	    	    	s.pregunta = fields[0];
	    	    	s.anulada = (fields[1].equals("1"));
	    	    	s.esLliure = (fields[2].equals("0"));
	    	    	if (!s.esLliure) {
		    	    	String[] opcions = fields[2].split(",",-1);
		    	    	for (String op: opcions) {
		    	    		String [] v = op.split("\t",-1);
		    	    		Opcio o = new Opcio("",0,0);
		    	    		o.value = v[0];
		    	    		o.pct = Double.parseDouble(v[1]);
		    	    		o.correcte = (v[2].equals("1"));
		    	    		o.solucio = (v[3].equals("1"));
		    	    		s.opcions.add(o);
		    	    	}
	    	    	}
	    	    	this.sol.add(s);
	    	    }
	    	    
	    	    this.SetPreguntes();
	    	    //this.SetOpcions(this.sol.get(0).pregunta);
	    	} catch (Exception e) {
	        	ShowAlert(e.getMessage(),C_ERROR,AlertType.ERROR);
	        } finally {
	    	    it.close();
	    	}
	    } catch (Exception e) {
	    	ShowAlert(e.getMessage(),C_ERROR,AlertType.ERROR);
	    }
    }
    
    public void CarregaPECs(File f) {
    	this.PECs = new ArrayList<PEC>();
		try {
			LineIterator it = FileUtils.lineIterator(f, "UTF-8");
	    	try {
	    	    while (it.hasNext()) {
                    this.PECs.add(new PEC(this.Plantilla, it.nextLine()));		// add PEC to arraylist
	    	    }
	    	} catch (Exception e) {
	        	ShowAlert(e.getMessage(),C_ERROR,AlertType.ERROR);
	        } finally {
	    	    it.close();
	    	}
	    } catch (Exception e) {
	    	ShowAlert(e.getMessage(),C_ERROR,AlertType.ERROR);
	    }
    }
/*    
    public void SetOpcions(String p) {
    	this.resps.clear();
    	// get the chosen solucio
    	for (Solucio s: this.sol) {
    		if (s.pregunta.equals(p)) {
    			if (!s.esLliure ) {
	    			// load opcions
	    			for (Opcio o: s.opcions) {
	    				this.resps.add(new OpcioSol(o, this.respostes));
	    			}
    			}
    		}
    	}
    }
*/    
    public void SetPreguntes() {
/*        for (Solucio s: this.sol) {
        	PreguntaSol p = new PreguntaSol(s);
             this.pregs.add(p);
        }*/
        
        
        for (Solucio s: this.sol) {
        	if (!s.esLliure) {
	        	Boolean subitem = false;
				for (Opcio o: s.opcions) {
					this.pr.add(new PreguntaResposta(s, o, subitem));
					subitem = true;
				}
        	}
        }
        
/*        this.preguntas.requestFocus();
        this.preguntas.getSelectionModel().select(0);
        this.preguntas.getFocusModel().focus(0);

        for (PreguntaResposta p: this.pr) {
        	if (!p.subresposta) System.out.println(p.getNom());
        	else System.out.print("   ");
        	System.out.print(p.getValor() + " ");
        	System.out.print(p.getPCT() + " ");
        	System.out.print(p.getCorrecte() + " ");
        	System.out.println(p.getSolucio() + " ");
        }*/
    }

    public Boolean CheckDir() {
    	Boolean lreturn = false;
    	if (this.pecsDir.getText().isEmpty()) {
    		ShowAlert(C_INDICAR_CARPETA,C_ERROR,AlertType.ERROR);
    	} else {
    		File folder = new File(this.pecsDir.getText());
    		lreturn = folder.exists(); 
    		if (!lreturn) ShowAlert(C_CARPETA_NO_EXISTEIX,C_ERROR,AlertType.ERROR);
    	}
    	return lreturn;
    }

    public void ShowAlert(String message, String title, AlertType type) {
    	Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

/*	YA NO SE DESCOMPRIME: DEJO EL CÓDIGO COMO REFERENCIA    
    public void Descomprimir() {
    	if (this.CheckDir()) {
			// zip file
	        File directory = new File(dir.getText());
	        Collection<File> files = FileUtils.listFiles(directory, new WildcardFileFilter(C_ZIP), null);
	                    
	        if (files.isEmpty()) {
	        	ShowAlert(C_NO_ES_TROBA_ZIP,C_ERROR,AlertType.ERROR);
	        } else if (files.size() > 1) {
	        	ShowAlert(C_MASSA_ZIPS,C_ERROR,AlertType.ERROR);
	        } else {
	        	File file = files.iterator().next();				// arxiu ZIP
	        	File folder = new File(dir.getText(),C_PDFS);		// carpeta a on es descomprimeix el ZIP 
	        	
	        	try {
	            	// la carpeta es crea nova; si ja existeix, s'elimina
	            	if (folder.exists()) FileUtils.deleteDirectory(folder); 
	            	folder.mkdir();
	            	
	            	byte[] buffer = new byte[1024];
	                // get the zip file content
	                ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
	                //get the zipped file list entry
	                ZipEntry ze = zis.getNextEntry();
	                while (ze!=null) {
	                    String fileName = ze.getName();
	                    File newFile = new File(folder.getAbsolutePath() + File.separator + fileName);
	
	                    // create all non exists folders else you will hit FileNotFoundException for compressed folder
	                    new File(newFile.getParent()).mkdirs();
	
	                    FileOutputStream fos = new FileOutputStream(newFile);             
	                    int len;
	                    while ((len = zis.read(buffer)) > 0) {
	                        fos.write(buffer, 0, len);
	                    }
	                    fos.close();
	                    
	                    ze = zis.getNextEntry();
	                }
	                zis.closeEntry();
	                zis.close();
	                
	        		ShowAlert(C_FINAL,"",AlertType.INFORMATION);
	            } catch (Exception e) {
	            	ShowAlert(e.getMessage(),C_ERROR,AlertType.ERROR);
	            }
	        }
		}
	}
*/    

}
