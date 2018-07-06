package corregirpecs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

public class CorregirController implements Initializable {

    @FXML
    private TextField dir;
    @FXML
    private CheckBox presencials;
    @FXML
    private CheckBox overwrite;

    //private final String C_DEFDIR = System.getProperty("user.home");
    private final String C_DEFDIR = "/Users/r/Desktop/CorregirPECs/2017-18_PEC4_DE0";
    //private final String C_DEFDIR = "/home/drslump/Escritorio/CorregirPECs/2017-18_PEC4_DE0";
    
    @FXML
    void getDir(ActionEvent event) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar carpeta de treball");
        directoryChooser.setInitialDirectory(new File(C_DEFDIR));
        File folder = directoryChooser.showDialog(null);
        if (folder != null) dir.setText(folder.getAbsolutePath());
        else dir.setText("");
    }

    @FXML
    void Descomprimir(ActionEvent event) {
    	Boolean lContinue = false;
    	if (dir.getText().isEmpty()) ShowAlert("Indicar la carpeta de treball","Error",AlertType.ERROR);
    	else lContinue = true;
    	
    	if (lContinue) {
    		// obtenir l'arxiu zip
            File directory = new File(dir.getText());
            Collection<File> files = FileUtils.listFiles(directory, new WildcardFileFilter("*.zip"), null);
                        
            if (files.isEmpty()) {
            	ShowAlert("No es troba l'arxiu comprimit","Error",AlertType.ERROR);
            } else if (files.size() > 1) {
            	ShowAlert("Hi ha més d'un arxiu comprimit","Error",AlertType.ERROR);
            } else {
            	File file = files.iterator().next();				// arxiu ZIP
            	File folder = new File(dir.getText(),"PDFs");		// carpeta a on es descomprimeix el ZIP 
            	
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
                    
	        		// comprovar els arxius descomprimits i generar problemes si necessari 
	        		List<String> problems = new ArrayList<String>();
	        		for (File f : folder.listFiles()) {
	        			// loop pels arxius no ocults
	        	        if (f.isFile() && !f.isHidden()) {
	        	        	// és un PDF?
	        	        	String ext = FilenameUtils.getExtension(f.getName());
	        	        	if (ext.equalsIgnoreCase("pdf")) {
	        	                PdfReader reader = new PdfReader(f.getAbsolutePath());
	        	                AcroFields form = reader.getAcroFields();
	        	                // té camps?
	        	                if (!(form.getFields().size()>0)) problems.add(f.getName()); 
	        	        	} else {
	        	        		problems.add(f.getName());
	        	        	}
	        	        }
	        	    }
	        		
	        		if (problems.size()>0) {
	        			Files.write(Paths.get(directory.getAbsolutePath() + File.separator + "problemes.txt"), problems, Charset.forName("UTF-8"));
	                	ShowAlert("Hi ha PECs amb problemes.\n\nVeure l'arxiu problemes.txt","Atenció",AlertType.WARNING);
	        		} else {
	        			ShowAlert("PECs descomprimides","Procés acabat",AlertType.INFORMATION);
	        		}
                } catch (Exception e) {
                	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
                }
            }
    	}
    }
    
    @FXML
    void Analitzar(ActionEvent event) {
    	if (dir.getText().isEmpty()) {
    		ShowAlert("Indicar la carpeta de treball","Error",AlertType.ERROR);
    	} else {
            File folder = new File(dir.getText());
    		File pecs = new File(dir.getText(),"PDFs");
            Collection<File> files = FileUtils.listFiles(folder, new WildcardFileFilter("sol.txt"), null);

            if (!pecs.exists()) {
            	ShowAlert("No es troba la carpeta PDFs","Error",AlertType.ERROR);
            } else if (files.isEmpty()) {
            	ShowAlert("No es troba l'arxiu sol.txt","Error",AlertType.ERROR);
            } else {
            	// SOLUCIÓ: arxiu sol.txt 
            	try {
            		// obtenir les files de la solució una a una
            		LineIterator it = FileUtils.lineIterator(files.iterator().next(), "UTF-8");
                	try {
                		Boolean lfirst = true;
                	    while (it.hasNext()) {
                	    	String line = it.nextLine();
                	    	if (!lfirst) {
	                	    	String[] t = line.split(",");
	                	        for (String c : t) {
	                	            System.out.println(c);
	                	        }
                	    	} else {
                	    		lfirst = false;
                	    	}
                	    }
                	} catch (Exception e) {
                    	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
                    } finally {
                	    it.close();
                	}
                } catch (Exception e) {
                	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
                }
            }
    	}
    }
    
    public void ShowAlert(String message, String title, AlertType type) {
    	Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    	dir.setText(C_DEFDIR);
    }
}
