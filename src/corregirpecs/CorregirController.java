package corregirpecs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

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
import javafx.stage.FileChooser;

public class CorregirController implements Initializable {

    @FXML
    private TextField dir;
    @FXML
    private CheckBox presencials;
    @FXML
    private CheckBox overwrite;

    //private final String C_DEFDIR = System.getProperty("user.home");
    //private final String C_DEFDIR = "/Users/r/Desktop/CorregirPECs/2017-18_PEC4_DE0";
    private final String C_DEFDIR = "/home/drslump/Escritorio/CorregirPECs/2017-18_PEC4_DE0";
    
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
    	File folder;
    	Boolean lContinue = false;
    	if (dir.getText().isEmpty()) ShowAlert("Indicar la carpeta de treball","Error",AlertType.ERROR);
    	else lContinue = true;
    	if (lContinue) {
    		folder = new File(dir.getText(),"PDFs");
    		// AQUÍ ME QUEDO: DETECTAR ARCHIVO ZIP
    	}
/*        try {
        	// la carpeta es crea nova; si ja existeix, s'elimina
        	if (folder.exists()) FileUtils.deleteDirectory(folder); 
        	folder.mkdir();
        	
        	byte[] buffer = new byte[1024];
            // get the zip file content
            ZipInputStream zis = new ZipInputStream(new FileInputStream(f));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
            while (ze!=null) {
                String fileName = ze.getName();
                File newFile = new File(dir + File.separator + fileName);

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
            
            return true;
        } catch (Exception e) {
        	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
    		return false;    		
        }*/
    }
    
    @FXML
    void Analitzar(ActionEvent event) {
/*    	Boolean lContinue = false;
    	if (zip.getText().isEmpty()) ShowAlert("Indicar l'arxiu ZIP","Error",AlertType.ERROR);
    	else if (sol.getText().isEmpty()) ShowAlert("Indicar l'arxiu Solució (TXT)","Error",AlertType.ERROR);
    	else lContinue = true;
    	    	
    	if (lContinue) {
	    	File fzip = new File(zip.getText());
	    	File fsol = new File(sol.getText());
	    	
	        if (fzip.isFile()) {
	        	File dir = new File(fzip.getParent(), "PDFs");
	        	if (Descomprimeix(fzip, dir)) {
	        		// comprovar els arxius descomprimits i generar llistes 
	        		List<File> pdfs = new ArrayList<File>();
	        		List<String> problems = new ArrayList<String>();
	        		for (File f : dir.listFiles()) {
	        			// loop pels arxius no ocults
	        	        if (f.isFile() && !f.isHidden()) {
	        	        	// és un PDF?
	        	        	String ext = FilenameUtils.getExtension(f.getName());
	        	        	if (ext.equalsIgnoreCase("pdf")) {
	        	        		try {
		        	                PdfReader reader = new PdfReader(f.getAbsolutePath());
		        	                AcroFields form = reader.getAcroFields();
		        	                // té camps?
		        	                if (form.getFields().size()>0) pdfs.add(f);
		        	                else problems.add(f.getName()); 
	        	        		} catch (Exception e) {
	        	        			ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
	        	                }
	        	        	} else {
	        	        		problems.add(f.getName());
	        	        	}
	        	        }
	        	    }
	        		
	        		
	        		
	        		if (problems.size()>0) {
	        			try {
		        			Files.write(Paths.get(fzip.getParent() + "/problemes.txt"), problems, Charset.forName("UTF-8"));
		                	ShowAlert("Hi ha PECs amb problemes. Veure l'arxiu problemes.txt","Atenció",AlertType.WARNING);
		        		} catch (Exception e) {
		                	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
		                }
	        		}
	        	}
	        } else {
	        	ShowAlert("L'arxiu ZIP no existeix","Error",AlertType.ERROR);
	        }
    	}*/
    }
        
    public boolean Descomprimeix(File f, File dir) {
    	// descomprimir l'arxiu ZIP f a la carpeta dir
    	if (dir.exists() && !overwrite.isSelected()) {
    		// la carpeta ja existeix i no es sobreescriu: no descomprimir
    		return true;
    	} else {
	        try {
	        	// la carpeta es crea nova; si ja existeix, s'elimina
	        	if (dir.exists()) FileUtils.deleteDirectory(dir); 
	        	dir.mkdir();
	        	
	        	byte[] buffer = new byte[1024];
	            // get the zip file content
	            ZipInputStream zis = new ZipInputStream(new FileInputStream(f));
	            //get the zipped file list entry
	            ZipEntry ze = zis.getNextEntry();
	            while (ze!=null) {
	                String fileName = ze.getName();
	                File newFile = new File(dir + File.separator + fileName);
	
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
	            
	            return true;
	        } catch (Exception e) {
	        	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
	    		return false;    		
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
