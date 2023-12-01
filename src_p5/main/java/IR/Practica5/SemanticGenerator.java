package IR.Practica5;

import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.VCARD;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

import java.io.IOException;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import org.xml.sax.SAXException;
import org.apache.jena.vocabulary.DC;



public class SemanticGenerator {
    /**
     * Main principal
     */
    public static void main (String args[]) {
        // Se comprueba que el número de parámetros es correcto
        SemanticGenerator.comprobarParametros(args.length);

        // args[0] = -rdf
        // args[1] = <rdfPath>
        // args[2] = -docs
        // args[3] = <docsPath>

        if (!args[0].equals("-rdf") || !args[2].equals("-docs")) {
            System.out.println("Argumentos incorrectos. Uso: SemanticSearcher -rdf <rdfPath> -docs <docsPath>");
            System.exit(1);
        }

        // Si el número de parámetros es correcto
        String rdfPath = args[1];
        String docsPath = args[3];

        // Bucle para leer cada fichero del directorio docsPath y comprobar que es un XML
        // Verificar si es un directorio válido
        SemanticGenerator.leer_ficheros_XML(docsPath, rdfPath);
    }


    /**
     * Función que comprueba que el número de parámetros es correcto
     */
    private static void comprobarParametros(int numParametros){
        if (numParametros != 4) {
            System.out.println("Número incorrecto de parámetros.\nUso: SemanticSearcher -rdf <rdfPath> -docs <docsPath>");
            System.exit(1);
        }
    }

    // Función que lee un fichero XML y extrae los valores que se buscan
    private static void parserXML(File file, String directorio_docs, String directorio_rdf){
        // Se declaran las variables de los datos que se buscan
        String contributor = "", creator = "", date = "", description = "";
        String identifier = "", language = "", publisher = "";
        String relation = "", rights = "", subject = "", title = "", type = "";
        
        // Lee el fichero XML y extrae los datos
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document documento = builder.parse(file);

            // Se obtiene el elemento raíz
            org.w3c.dom.Element raiz = documento.getDocumentElement();

            // Se obtiene la lista de nodos con nombre "dc:title"
            org.w3c.dom.NodeList listaTitle = raiz.getElementsByTagName("dc:title");
            // Se recorre la lista de nodos
            for (int i = 0; i < listaTitle.getLength(); i++) {
                org.w3c.dom.Node nodo = listaTitle.item(i); // Se obtiene el nodo actual
                title = nodo.getTextContent();  // Se obtiene el texto que contiene el nodo actual
            }



            // Se obtiene la lista de nodos con nombre "dc:description"
            org.w3c.dom.NodeList listaDescription = raiz.getElementsByTagName("dc:description");
            // Se recorre la lista de nodos
            for (int i = 0; i < listaDescription.getLength(); i++) {
                // Se obtiene el nodo actual
                org.w3c.dom.Node nodo = listaDescription.item(i);
                // Se obtiene el texto que contiene el nodo actual
                description = nodo.getTextContent();
            }

            // Se obtiene la lista de nodos con nombre "dc:date"
            org.w3c.dom.NodeList listaDate = raiz.getElementsByTagName("dc:date");
            // Se recorre la lista de nodos
            for (int i = 0; i < listaDate.getLength(); i++) {
                // Se obtiene el nodo actual
                org.w3c.dom.Node nodo = listaDate.item(i);
                // Se obtiene el texto que contiene el nodo actual
                date = nodo.getTextContent();
            }

            // Se obtiene la lista de nodos con nombre "dc:language"
            org.w3c.dom.NodeList listaLanguage = raiz.getElementsByTagName("dc:language");
            // Se recorre la lista de nodos
            for (int i = 0; i < listaLanguage.getLength(); i++) {
                // Se obtiene el nodo actual
                org.w3c.dom.Node nodo = listaLanguage.item(i);
                // Se obtiene el texto que contiene el nodo actual
                language = nodo.getTextContent();
            }

            // Se obtiene la lista de nodos con nombre "dc:identifier"
            org.w3c.dom.NodeList listaIdentifier = raiz.getElementsByTagName("dc:identifier");
            // Se recorre la lista de nodos
            for (int i = 0; i < listaIdentifier.getLength(); i++) {
                // Se obtiene el nodo actual
                org.w3c.dom.Node nodo = listaIdentifier.item(i);
                // Se obtiene el texto que contiene el nodo actual
                identifier = nodo.getTextContent();
            }

            // Se obtiene la lista de nodos con nombre "dc:creator"
            org.w3c.dom.NodeList listaCreator = raiz.getElementsByTagName("dc:creator");
            // Se recorre la lista de nodos
            for (int i = 0; i < listaCreator.getLength(); i++) {
                // Se obtiene el nodo actual
                org.w3c.dom.Node nodo = listaCreator.item(i);
                // Se obtiene el texto que contiene el nodo actual
                creator = nodo.getTextContent();
            }

            // Se obtiene la lista de nodos con nombre "dc:contributor"
            org.w3c.dom.NodeList listaContributor = raiz.getElementsByTagName("dc:contributor");
            // Se recorre la lista de nodos
            for (int i = 0; i < listaContributor.getLength(); i++) {
                // Se obtiene el nodo actual
                org.w3c.dom.Node nodo = listaContributor.item(i);
                // Se obtiene el texto que contiene el nodo actual
                contributor = nodo.getTextContent();
            }

            // Se obtiene la lista de nodos con nombre "dc:publisher"
            org.w3c.dom.NodeList listaPublisher = raiz.getElementsByTagName("dc:publisher");
            // Se recorre la lista de nodos
            for (int i = 0; i < listaPublisher.getLength(); i++) {
                // Se obtiene el nodo actual
                org.w3c.dom.Node nodo = listaPublisher.item(i);
                // Se obtiene el texto que contiene el nodo actual
                publisher = nodo.getTextContent();
            }

            // Se obtiene la lista de nodos con nombre "dc:relation"
            org.w3c.dom.NodeList listaRelation = raiz.getElementsByTagName("dc:relation");
            // Se recorre la lista de nodos
            for (int i = 0; i < listaRelation.getLength(); i++) {
                // Se obtiene el nodo actual
                org.w3c.dom.Node nodo = listaRelation.item(i);
                // Se obtiene el texto que contiene el nodo actual
                relation = nodo.getTextContent();
            }

            // Se obtiene la lista de nodos con nombre "dc:rights"
            org.w3c.dom.NodeList listaRights = raiz.getElementsByTagName("dc:rights");
            // Se recorre la lista de nodos
            for (int i = 0; i < listaRights.getLength(); i++) {
                // Se obtiene el nodo actual
                org.w3c.dom.Node nodo = listaRights.item(i);
                // Se obtiene el texto que contiene el nodo actual
                rights = nodo.getTextContent();
            }

            // Se obtiene la lista de nodos con nombre "dc:subject"
            org.w3c.dom.NodeList listaSubject = raiz.getElementsByTagName("dc:subject");
            // Lista para almacenar los textos de los nodos "dc:subject"
            List<String> subjects = new ArrayList<>();
            // Se recorre la lista de nodos
            for (int i = 0; i < listaSubject.getLength(); i++) {
                // Se obtiene el nodo actual
                org.w3c.dom.Node nodo = listaSubject.item(i);
                // Se obtiene el texto que contiene el nodo actual
                String subjectText = nodo.getTextContent();
                // Se agrega el texto a la lista
                subjects.add(subjectText);
            }

            // Se obtiene la lista de nodos con nombre "dc:type"
            org.w3c.dom.NodeList listaType = raiz.getElementsByTagName("dc:type");
            // Se recorre la lista de nodos
            for (int i = 0; i < listaType.getLength(); i++) {
                // Se obtiene el nodo actual
                org.w3c.dom.Node nodo = listaType.item(i);
                // Se obtiene el texto que contiene el nodo actual
                type = nodo.getTextContent();
            }

            // // Imprime por pantalla los datos extraidos
            // System.out.println("title: " + title);
            // System.out.println("description: " + description);
            // System.out.println("date: " + date);
            // System.out.println("language: " + language);
            // System.out.println("identifier: " + identifier);
            // System.out.println("creator: " + creator);
            // System.out.println("contributor: " + contributor);
            // System.out.println("publisher: " + publisher);
            // System.out.println("relation: " + relation);
            // System.out.println("rights: " + rights);
            // System.out.println("subject: " + subject);
            // System.out.println("type: " + type);

            // Se eliminan los saltos de linea y espacios por _
            title = title.replaceAll("\\r\\n|\\r|\\n", "_");
            description = description.replaceAll("\\r\\n|\\r|\\n", "_");
            date = date.replaceAll("\\r\\n|\\r|\\n", "_");
            language = language.replaceAll("\\r\\n|\\r|\\n", "_");
            identifier = identifier.replaceAll("\\r\\n|\\r|\\n", "_");
            creator = creator.replaceAll("\\r\\n|\\r|\\n", "_");
            contributor = contributor.replaceAll("\\r\\n|\\r|\\n", "_");
            publisher = publisher.replaceAll("\\r\\n|\\r|\\n", "_");
            relation = relation.replaceAll("\\r\\n|\\r|\\n", "_");
            rights = rights.replaceAll("\\r\\n|\\r|\\n", "_");
            subject = subject.replaceAll("\\r\\n|\\r|\\n", "_");
            type = type.replaceAll("\\r\\n|\\r|\\n", "_");
            
            // Se eliminan los saltos de line y espacios en subject
            for (int i = 0; i < subjects.size(); i++) {
                subjects.set(i, subjects.get(i).replaceAll("\\r\\n|\\r|\\n", "_"));
            }

            

            generarRDFModelo(file, directorio_docs, directorio_rdf, 
                            title, description, date, language, creator, 
                            contributor, relation, rights, type, subjects);

        } catch (ParserConfigurationException e) {
            // Handle ParserConfigurationException
            e.printStackTrace();
        } catch (IOException e) {
            // Handle IOException
            e.printStackTrace();
        } catch (SAXException e) {
            // Handle SAXException
            e.printStackTrace();
        }
    }
    
    /**
     * Función que genera un archivo RDF
     */
    private static void generarArchivoRdf(String rdfPath, String fileName, Model model_rdf) {
        //String fileName = "mensaje.rdf";
        File rdfFile = new File(rdfPath, fileName);
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rdfFile))) {
            //writer.write("Generado correctamente");
            //System.out.println("Archivo RDF generado correctamente en " + fileName);
            // Escirbir el modelo en el fichero
            model_rdf.write(writer, "RDF/XML-ABBREV");
        } catch (IOException e) {
            System.out.println("Error al generar el archivo RDF: " + e.getMessage());
        }
    }

    public static void generarRDFModelo(File file,
                                        String directorio_docs, String directorio_rdf, String title, String description, String date, String language,
                                        String creator, String contributor, String relation, String rights, String type, List<String> subjects) {

        // Se crea el modelo RDF
        Model modelRDF = ModelFactory.createDefaultModel();

        // Se crean las propiedades (atributos del documento)
        Property titleProperty = modelRDF.createProperty(DC.title.toString());
        Property languageProperty = modelRDF.createProperty(DC.language.toString());
        Property descriptionProperty = modelRDF.createProperty(DC.description.toString());
        Property dateProperty = modelRDF.createProperty(DC.date.toString());
        Property subjectProperty = modelRDF.createProperty(DC.subject.toString());
        Property publisherProperty = modelRDF.createProperty("http://xmlns.com/foaf/0.1/publisher"); // dc:contenido -> dc:publusher del xml
        Property contributorProperty = modelRDF.createProperty(DC.contributor.toString());
        
        Property rightsProperty = modelRDF.createProperty(DC.rights.toString());
        Property relationProperty = modelRDF.createProperty(DC.relation.toString());
        
        
        Property typeProperty = modelRDF.createProperty(DC.type.toString());
        Property nameProperty = modelRDF.createProperty("http://xmlns.com/foaf/0.1/name");
        
        // Se crean las clases
        Resource personClass = modelRDF.createResource("http://xmlns.com/foaf/0.1/Person");
        Resource urlClass = modelRDF.createResource("http://xmlns.com/foaf/0.1/Creator");
        Resource subjecClass = modelRDF.createResource("http://xmlns.com/foaf/0.1/Subject");
        Resource publisherClass = modelRDF.createResource("http://xmlns.com/foaf/0.1/Publisher");

        // Se crea la instancia de documento
        Resource documentInstance = modelRDF.createResource()
                .addProperty(titleProperty, title)
                .addProperty(languageProperty, language)
                .addProperty(descriptionProperty, description)
                .addProperty(dateProperty, date);
                

        // Se crean las instancias de subject
        for (int i = 0; i < subjects.size(); i++) {
            Resource subjectInstance = modelRDF.createResource()
                    .addProperty(subjectProperty, subjects.get(i));
        }

        // Se crea la instancia de persona (creador)
        Resource personaInstance = modelRDF.createResource()
                .addProperty(nameProperty, creator);
                //.addProperty(nameProperty, contributor);

        // Se crea la instancia de URL (relation)
        Resource urlInstance = modelRDF.createResource()
                .addProperty(relationProperty, relation)
                .addProperty(rightsProperty, rights);

        // Se añaden las relaciones
        documentInstance.addProperty(contributorProperty, contributor);
        //documentInstance.addProperty(relationProperty, urlInstance);
        documentInstance.addProperty(rightsProperty, rights);
        documentInstance.addProperty(typeProperty, type);

        // Se añade la relación entre documento y creador
        //documentInstance.addProperty(FOAF. + "creator", creatorInstance);


        // // Obtener el nombre del archivo sin extensión usando Apache Commons IO
        //System.out.println("Nombreficheri: "+file.getName());
        int posicionPunto = file.getName().lastIndexOf(".");
        String nombreSinExtension = "";
        String nombreFicheroCrear = "";
        if (posicionPunto != -1) {
            // Extraer el nombre del fichero sin la extensión
            nombreSinExtension = file.getName().substring(0, posicionPunto);
            //System.out.println("Nombre del fichero sin extensión: " + nombreSinExtension);

            // Almacenar el resultado en otra variable
            nombreFicheroCrear = nombreSinExtension + ".rdf";
            //System.out.println("Nombre para crear: " + nombreFicheroCrear );
            // escribe el modelo en un archivo
            
        } else {
            System.out.println("El fichero no tiene extensión.");
        }
        String nombre_fichero_rdf = nombreFicheroCrear;
        // // Crear el archivo RDF de salida con el mismo nombre pero con extensión ".rdf"
        // String outputFileName = nameWithoutExtension + ".rdf";
        // String outputFilePath = new File(outputDirectory, outputFileName).getAbsolutePath();

        //System.out.println("FICHERO: " + nombre_fichero_rdf);
        // Se imprime el modelo RDF
        SemanticGenerator.generarArchivoRdf(directorio_rdf, nombre_fichero_rdf, modelRDF);
    }
    

    /**
     * Función que lista todos los ficheros xml que se encuentran en el directorio de la colección
     */
    private static void leer_ficheros_XML(String directorio_docs, String directorio_rdf){
        File directory = new File(directorio_docs);
        //System.out.println(directory);
        File[] files = directory.listFiles();
        
        if (files != null) {
            //System.out.println("Documentos XML encontrados en el directorio:");
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".xml")) {

                        //System.out.println(file.getName()); // <- imprime el nombre del fichero xml 
                        SemanticGenerator.parserXML(file, directorio_docs, directorio_rdf);


                }
            }
        } else {
            System.out.println("El directorio no existe o no es accesible. Listando archivos del directorio actual:");
        }
    }
}
