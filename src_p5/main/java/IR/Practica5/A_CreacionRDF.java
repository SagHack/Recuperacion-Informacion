package IR.Practica5;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.VCARD;

import java.io.FileOutputStream;

/**
 * Ejemplo de como construir un modelo de Jena y añadir nuevos recursos 
 * mediante la clase Model
 */
public class A_CreacionRDF {
	
	/**
	 * muestra un modelo de jena de ejemplo por pantalla
	 */
	public static void main (String args[]) {
        // Model model = A_CreacionRDF.generarEjemplo(); // Modelo del enunciado

        // Modelo modificado
        Model model = A_CreacionRDF.generarEjemploConTipo();
        // write the model in the standar output
        //model.write(System.out);

        // escribe el modelo en un archivo
        try(FileOutputStream output = new FileOutputStream("nombre.rdf")){
            model.write(output, "RDF/XML-ABBREV");
            System.out.println("FICHERO CREADO");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * Genera un modelo de jena de ejemplo
	 */
	public static Model generarEjemplo(){
		// definiciones
        String personURI    = "http://somewhere/JohnSmith";
        String givenName    = "John";
        String familyName   = "Smith";
        String fullName     = givenName + " " + familyName;

        // crea un modelo vacio
        Model model = ModelFactory.createDefaultModel();

        // le a�ade las propiedades
        Resource johnSmith  = model.createResource(personURI)
             .addProperty(VCARD.FN, fullName)
             .addProperty(VCARD.N, 
                      model.createResource()
                           .addProperty(VCARD.Given, givenName)
                           .addProperty(VCARD.Family, familyName));
        return model;
	}

    /**
     * Genera un modelo de jena de ejemplo como se solicita en el enunciado de la práctica
     */
    public static Model generarEjemploConTipo(){
        String personURI    = "http://somewhere/JohnSmith";
        String givenName    = "John";
        String familyName   = "Smith";
        String fullName     = givenName + " " + familyName;

        // Datos persona nueva-1
        String personURI_1    = "http://somewhere/SimonAloso";
        String givenName_1    = "Simon";
        String familyName_1   = "Alonso";
        String fullName_1     = givenName + " " + familyName;

        // Datos persona nueva-2
        String personURI_2    = "http://somewhere/PilarFierro";
        String givenName_2    = "Pilar";
        String familyName_2   = "Fierro";
        String fullName_2     = givenName + " " + familyName;

        // crea un modelo vacio
        Model model = ModelFactory.createDefaultModel();

        // le a�ade las propiedades
        Resource johnSmith  = model.createResource(personURI)
                .addProperty(VCARD.FN, fullName)
                .addProperty(VCARD.N,
                        model.createResource()
                                .addProperty(VCARD.Given, givenName)
                                .addProperty(VCARD.Family, familyName));

        // Se añaden las propiedades de la nueva persona (1)
        Resource simonAlonso  = model.createResource(personURI_1)
                .addProperty(VCARD.FN, fullName_1)
                .addProperty(VCARD.N,
                        model.createResource()
                                .addProperty(VCARD.Given, givenName_1)
                                .addProperty(VCARD.Family, familyName_1));

        // Se añaden las propiedades de la nueva persona (2)
        Resource pilarFierro  = model.createResource(personURI_2)
                .addProperty(VCARD.FN, fullName_2)
                .addProperty(VCARD.N,
                        model.createResource()
                                .addProperty(VCARD.Given, givenName_2)
                                .addProperty(VCARD.Family, familyName_2));

        // Se añade la relación del recurso con valor http://xmlns.com/foaf/0.1/person
        Property rdfType = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        model.add(johnSmith, rdfType, ResourceFactory.createResource("http://xmlns.com/foaf/0.1/person"));

        // Se añade la relación con las dos nuevas personas
        Property newPeople = ResourceFactory.createProperty("http://xmlns.com/foaf/0.1/knows");
        model.add(johnSmith, newPeople, simonAlonso);
        model.add(johnSmith, newPeople, pilarFierro);
        model.add(simonAlonso, newPeople, pilarFierro);

        // Se añade la nueva tripleta al modelo
        Resource resource = model.createResource("http://example.com/someResource");
        Property property = ResourceFactory.createProperty("http://example.com/someProperty");
        Literal value = ResourceFactory.createStringLiteral("Some value");
        Statement statement = ResourceFactory.createStatement(resource, property, value);
        model.add(statement);

        return model;
    }
	
	
}
