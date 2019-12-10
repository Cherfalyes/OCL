package ocl.parser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.OCLInput;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.uml2.*;

public class ModelOCL {

	/**
	 * @param args
	 */
	private ResourceSet resSet;
	private Collection<Constraint> Contraintes;
	
	// Le constructeur (charger les variables et les instancier)
	public ModelOCL(){
		resSet = new ResourceSetImpl();
		resSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(XMIResource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
	}
	//Charger le modèle OCL (Le chemin du modèle OCL, et le chemin vers la contrainte OCL textuelle que l'on veut avoir
public void loadModel(String pathModel, String pathOCL){
	ResourceSet resourceSet = new ResourceSetImpl();
	
	// Register the default resource factory -- only needed for stand-alone!
	resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(XMIResource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
	//resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new XMLResourceFactoryImpl());
	// Get the URI of the model file.
	URI fileURI = URI.createFileURI(pathModel);
	
	// Demand load the resource for this file.
	Resource resource = resourceSet.getResource(fileURI, true);
	
		
		
		
		//EPackage metaModel = (EPackageImpl) resource.getContents().get(0);
		EList<EObject> modelContents=resource.getContents();
				// Load OCL file

		EPackage.Registry.INSTANCE.put(((EPackage) modelContents.get(0)).getName(), modelContents.get(0));
				
				//OCL<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Contrainte, EClass, EObject> ocl;
		OCL<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> ocl;
		ocl = OCL.newInstance(EcoreEnvironmentFactory.INSTANCE);
			InputStream in;
			Contraintes = new HashSet<Constraint>();
				
				try
				{
					in = new FileInputStream(pathOCL);

					// parse the contents as an OCL document
					try
					{
						OCLInput document = new OCLInput(in);
						Contraintes =  ocl.parse(document);
					}
					catch (ParserException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getCause());
					}
					finally
					{
				    try
					{
						in.close();
					}
				    catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				}catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						
	}
	
	
public void OCLToXML(String path) {		
		
		Resource newResource = resSet.createResource(URI.createURI(path));
		for (Constraint Contrainte:Contraintes )
			newResource.getContents().add(Contrainte);
		
		try {
		
			newResource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			//e.printStackTrace();
		}

	}


protected void XmlToOCL(String path) {
	
	File file = new File(path);
	String stringOCL = "package \n\n"; 

	for (Constraint Contrainte : Contraintes) {
				
		OCLExpression<EClassifier> body = Contrainte.getSpecification()
				.getBodyExpression();

		Variable<EClassifier, EParameter> context = Contrainte
				.getSpecification().getContextVariable();
		
			
		stringOCL += "context " + context.getType().getEPackage().getName()+
				"::"+context.getType().getName() + "\n"
				+ "inv " + Contrainte.getName() + ":\n\t" +	body.accept(ToString.getInstance(body)) + "\n\n";
		System.out.println(stringOCL);
	}
		
			
	
	
	FileWriter fw = null;
	try {
		fw = new FileWriter(file);
		fw.write(stringOCL);
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (fw != null) {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}


	public  static void main(String[] args) {
		// TODO Auto-generated method stub
		ModelOCL test =new ModelOCL();	
		
		//test.loadModel("file:./inputs/synoptic_Ecore.ecore", "./inputs/contr.ocl");
		//test.OCLToXML ("./inputs/contr-xml.xmi");
		//test.loadModel("src/BMethod.ecore", "src/BMethod.oclxmi");
		//test.XmlToOCL("./inputs/xmlToOCl.oclxmi");
		test.loadModel("./inputs/FlatUML.ecore", "./inputs/C9.ocl");
		test.OCLToXML("./outputs/C9.xmi");
		//test.XmlToOCL("./outputs/C3.xmi");
		//test.OCLToXML("/home/echerfa/Desktop/Bad Smells/MM/OMG/UML/C1.txt");
			
		   
	}

}
