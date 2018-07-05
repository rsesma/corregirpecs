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
import javafx.stage.FileChooser;

public class CorregirController implements Initializable {

    @FXML
    private TextField zip;
    @FXML
    private TextField odt;
    @FXML
    private CheckBox presencials;
    @FXML
    private CheckBox overwrite;

    
    @FXML
    void getZip(ActionEvent event) {
    	FileChooser chooser = new FileChooser();
    	chooser.setTitle("Obrir arxiu comprimit");
    	//chooser.setInitialDirectory(new File(System.getProperty("user.home")));
    	chooser.setInitialDirectory(new File("/Users/r/Desktop/CorregirPECs/2017-18_PEC4_DE0"));
    	chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arxiu comprimit", "*.zip"));
    	File f = chooser.showOpenDialog(null);
        if (f != null) zip.setText(f.getAbsolutePath());
        else zip.setText("");    	
    }
    
    @FXML
    void getODT(ActionEvent event) {
    	FileChooser chooser = new FileChooser();
    	chooser.setTitle("Obrir plantilla");
    	//chooser.setInitialDirectory(new File(System.getProperty("user.home")));
    	chooser.setInitialDirectory(new File("/Users/r/Desktop/CorregirPECs/2017-18_PEC4_DE0"));
    	chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("LibreOffice Writer", "*.odt"));
    	File f = chooser.showOpenDialog(null);
        if (f != null) odt.setText(f.getAbsolutePath());
        else odt.setText("");    	
    }

    @FXML
    void Analisi(ActionEvent event) {
    	Boolean lContinue = false;
    	if (zip.getText().isEmpty()) ShowAlert("Indicar l'arxiu ZIP","Error",AlertType.ERROR);
    	else if (odt.getText().isEmpty()) ShowAlert("Indicar l'arxiu ODT","Error",AlertType.ERROR);
    	else lContinue = true;
    	    	
    	if (lContinue) {
	    	File fzip = new File(zip.getText());
	    	File fodt = new File(odt.getText());
	    	
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
    	}
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
    
    public void GeneraSolucio() {
/*    	try {
            XComponentContext xLocalContext = Bootstrap.createInitialComponentContext(null);
            System.out.println("xLocalContext");

            XMultiComponentFactory xLocalServiceManager = xLocalContext.getServiceManager();
            System.out.println("xLocalServiceManager");

            Object urlResolver = xLocalServiceManager.createInstanceWithContext(
                    "com.sun.star.bridge.UnoUrlResolver", xLocalContext);
            System.out.println("urlResolver");

            XUnoUrlResolver xUrlResolver =
                (XUnoUrlResolver) UnoRuntime.queryInterface(XUnoUrlResolver.class, urlResolver);            
            System.out.println("xUrlResolve");

            try {
                String uno = "uno:" + unoMode + ",host=" + unoHost + ",port=" + unoPort + ";" + unoProtocol + ";" + unoObjectName;
                Object rInitialObject = xUrlResolver.resolve(uno);
                System.out.println("rInitialObject");

                if (null != rInitialObject) {
                    XMultiComponentFactory xOfficeFactory = (XMultiComponentFactory) UnoRuntime.queryInterface(
                        XMultiComponentFactory.class, rInitialObject);
                    System.out.println("xOfficeFactory");

                    Object desktop = xOfficeFactory.createInstanceWithContext("com.sun.star.frame.Desktop", xLocalContext);
                    System.out.println("desktop");

                    XComponentLoader xComponentLoader = (XComponentLoader)UnoRuntime.queryInterface(
                        XComponentLoader.class, desktop);
                    System.out.println("xComponentLoader");
                    
                    PropertyValue[] loadProps = new PropertyValue[3];

                    loadProps[0] = new PropertyValue();
                    loadProps[0].Name = "Hidden";
                    loadProps[0].Value = Boolean.TRUE;

                    loadProps[1] = new PropertyValue();
                    loadProps[1].Name = "ReadOnly";
                    loadProps[1].Value = Boolean.FALSE;

                    loadProps[2] = new PropertyValue();
                    loadProps[2].Name = "MacroExecutionMode";
                    loadProps[2].Value = new Short(com.sun.star.document.MacroExecMode.ALWAYS_EXECUTE_NO_WARN);
                    
                    try {
                        XComponent xComponent = xComponentLoader.loadComponentFromURL("file:///" + inputFile, "_blank", 0, loadProps);              
                        System.out.println("xComponent from " + inputFile);

                        String macroName = "Standard.Module1.MYMACRONAME?language=Basic&location=application";
                        Object[] aParams = null;

                        XScriptProviderSupplier xScriptPS = (XScriptProviderSupplier) UnoRuntime.queryInterface(XScriptProviderSupplier.class, xComponent);
                        XScriptProvider xScriptProvider = xScriptPS.getScriptProvider(); 
                        XScript xScript = xScriptProvider.getScript("vnd.sun.star.script:"+macroName); 

                        short[][] aOutParamIndex = new short[1][1]; 
                        Object[][] aOutParam = new Object[1][1];

                        @SuppressWarnings("unused")
                        Object result = xScript.invoke(aParams, aOutParamIndex, aOutParam);
                        System.out.println("xScript invoke macro" + macroName);

                        XStorable xStore = (XStorable)UnoRuntime.queryInterface(XStorable.class, xComponent);
                        System.out.println("xStore");

                        if (outputFileType.equalsIgnoreCase("pdf")) {
                            System.out.println("writer_pdf_Export");
                            loadProps[0].Name = "FilterName";
                            loadProps[0].Value = "writer_pdf_Export";
                        }
                        xStore.storeToURL("file:///" + outputFile, loadProps);
                        System.out.println("storeToURL to file " + outputFile);
                        
                        xComponent.dispose();

                        xComponentLoader = null;
                        rInitialObject = null;

                        System.out.println("done.");

                        System.exit(0);

                    } catch(IllegalArgumentException e) {
                        System.err.println("Error: Can't load component from url " + inputFile);
                    }
                } else {
                    System.err.println("Error: Unknown initial object name at server side");
                }           
            } catch(NoConnectException e) {
                System.err.println("Error: Server Connection refused: check server is listening...");           }
        } catch(java.lang.Exception e) {
            System.err.println("Error: Java exception:");
            e.printStackTrace();
        }*/
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
    	
    }
}
