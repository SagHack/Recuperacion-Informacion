package IR.Practica5;

import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.VCARD;
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
        SemanticGenerator.leer_ficheros_XML(docsPath);
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
    private static void parserXML(File file){
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
            // Se recorre la lista de nodos
            for (int i = 0; i < listaSubject.getLength(); i++) {
                // Se obtiene el nodo actual
                org.w3c.dom.Node nodo = listaSubject.item(i);
                // Se obtiene el texto que contiene el nodo actual
                subject = nodo.getTextContent();
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

            // Imprime por pantalla los datos extraidos
            System.out.println("title: " + title);
            System.out.println("description: " + description);
            System.out.println("date: " + date);
            System.out.println("language: " + language);
            System.out.println("identifier: " + identifier);
            System.out.println("creator: " + creator);
            System.out.println("contributor: " + contributor);
            System.out.println("publisher: " + publisher);
            System.out.println("relation: " + relation);
            System.out.println("rights: " + rights);
            System.out.println("subject: " + subject);
            System.out.println("type: " + type);

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

            

            generarRDFModelo(title, description, date, language, identifier, creator, contributor,
                                    relation, rights, type);
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
     * Función que crea un Modelo RDF dado los datos que ha de tener
     */
    private static void generarRDFModelo(String title, String description, String date, String language, 
                                String identifier, String creator, String contributor, String relation, String rights, String type){
       
       
                                    // Se crea el modelo RDF
        Model modelRDF = ModelFactory.createDefaultModel();

        // Se crean los properties (atributos del documento)
        Property titleProperty = modelRDF.createProperty(DC.title.toString());
        Property descriptionProperty = modelRDF.createProperty(DC.description.toString());
        Property dateProperty = modelRDF.createProperty(DC.date.toString());
        Property languageProperty = modelRDF.createProperty(DC.language.toString());
        Property identifierProperty = modelRDF.createProperty(DC.identifier.toString());
        
        Property contributorProperty = modelRDF.createProperty(DC.contributor.toString());
        Property relationProperty = modelRDF.createProperty(DC.relation.toString());
        Property rightsProperty = modelRDF.createProperty(DC.rights.toString());
        Property typeProperty = modelRDF.createProperty(DC.type.toString());
        Property name = modelRDF.createProperty("http://xmlns.com/foaf/0.1/name");
        Property publisherProperty = modelRDF.createProperty("http://xmlns.com/foaf/0.1/publisher");

        // Se crea la clase documento
        // Resource documento = modelRDF.createResource("http://xmlns.com/foaf/0.1/Documents");

        // Se crea la clase persona
        Resource persona = modelRDF.createResource("http://xmlns.com/foaf/0.1/Person");
        Resource creador = modelRDF.createResource("http://xmlns.com/foaf/0.1/Creator", persona)
            .addProperty(name, creator);

       // Resource departamento = modelRDF.createResource("http://xmlns.com/foaf/0.1/Departamento"+ publisher)
       //     .addProperty(publisherProperty, publisher);

        Resource url = modelRDF.createResource("http://xmlns.com/foaf/0.1/URL");

        Resource relacion = modelRDF.createResource(relation, url); 
        Resource tipo = modelRDF.createResource("http://xmlns.com/foaf/0.1/Type"+ type);


        
        
        // Property creatorProperty = modelRDF.createProperty(DC.creator.toString())
        //     .addProperty(persona, creator);
        
        // // Se añade el contributor en un bucle porque puede haber varios
        // Property contributorProperty = modelRDF.createProperty(DC.contributor.toString())
        //     .addProperty(persona, contributor);
        // for (int i = 0; i < contributor.length; i++) {
        //     contributorProperty.addProperty(contributorProperty, contributor[i]);
        // }

        // Se crea la clase publisher
        // Property publisherProperty = modelRDF.createProperty(DC.publisher.toString())
        //     .addProperty(publisherProperty, publisher);

        // // Se crea la clase subject
        // Property subjectProperty = modelRDF.createProperty(DC.subject.toString())
        //     .addProperty(subjectProperty, subject);

        // Se crea la clase Documento
        Resource documento = modelRDF.createResource("http://xmlns.com/foaf/0.1/Document")
            .addProperty(titleProperty, title)
            .addProperty(descriptionProperty, description)
            .addProperty(dateProperty, date)
            .addProperty(languageProperty, language);
            // .addProperty(identifierProperty, identifier)
            // .addProperty(contributorProperty, contributor)
            // .addProperty(relationProperty, relation)
            // .addProperty(rightsProperty, rights)
            // .addProperty(typeProperty, type);

        // Se añaden la relacion entre documento y creador
        modelRDF.add(documento, contributorProperty, creator);
        // documento.addProperty(departamento, documento);

        // Se añade la relacion entre documento y publisher
        //documento.addProperty(publisherProperty, publisher);
        


        // Se crea los recursos para la clase genérica Documento y sus atributos


        // // property nombre = modelRDF.createProperty(DC.nombre.toString());
        // Resource documento = modelRDF.createResource(ns + "Documento");
        // Property tieneDate = modelRDF.createProperty(ns + "tieneDate");
        // Property tieneDescription = modelRDF.createProperty(ns + "tieneDescription");
        // Property tieneLanguage = modelRDF.createProperty(ns + "tieneLanguage");
        // Property tieneTitle = modelRDF.createProperty(ns + "tieneTitle");

        // // Se asocian los atributos a la calse generica documento
        // modelRDF.add(documento, tieneDate, date);
        // modelRDF.add(documento, tieneDescription, description);
        // modelRDF.add(documento, tieneLanguage, language);
        // modelRDF.add(documento, tieneTitle, title);
        
        // // Se crean recuross para las subclases específicas
        // Resource tfg = modelRDF.createResource(ns + "TFG");
        // Resource tesis = modelRDF.createResource(ns + "Tesis");
        // Resource url = modelRDF.createResource(ns + "URL");
        // Resource persona = modelRDF.createResource(ns + "Persona");
        // // ""htto://www.w3.org/2000/01/rdf-schema#"" + "Resource"
        // Resource subject = modelRDF.createResource(ns + "Subject");
        // Resource publisher = modelRDF.createResource(ns + "Publisher");

        // // Asocia subclases a la clase genérica Documento
        // Property esSubclaseDe = modelRDF.createProperty(ns + "esSubclaseDe");
        // modelRDF.add(tfg, esSubclaseDe, documento);
        // modelRDF.add(tesis, esSubclaseDe, documento);
        // modelRDF.add(url, esSubclaseDe, documento);
        // modelRDF.add(persona, esSubclaseDe, documento);
        // modelRDF.add(subject, esSubclaseDe, documento);
        // modelRDF.add(publisher, esSubclaseDe, documento);

        // // Imprime el modelo RDF
        modelRDF.write(System.out, "TURTLE");
    
        

    }
    





    private static void add_to_RDF(Model model_rdf, String contenido,String tag) {
        // VCARD.FN: Nombre completo de una entidad.
        // VCARD.N: Componentes del nombre (familia, dado, prefijo, sufijo).
        // VCARD.EMAIL: Dirección de correo electrónico.
        // VCARD.TEL: Número de teléfono.
        // VCARD.ADDR: Dirección postal.
        // VCARD.GEO: Coordenadas geográficas.
        // VCARD.ORG: Nombre de la organización.
        // VCARD.TITLE: Título o cargo en la organización.
        // VCARD.URL: URL asociada con la entidad.
        // VCARD.BDAY: Fecha de nacimiento.
        // VCARD.PHOTO: Representa una imagen o fotografía de la entidad.
        // VCARD.LOGO: Representa el logotipo de una organización.
        // VCARD.ROLE: Representa el rol o posición en una organización.
        // VCARD.CATEGORIES: Categorías asociadas con la entidad.
        // VCARD.NICKNAME: Apodo o nombre familiar.
        // VCARD.REV: Fecha de revisión.

        String pruebaURI = "http://prueba/JohnSmith";
        System.out.println("TAG: "+ tag);

        if(tag == "dc:date"){


            Resource resource = model_rdf.createResource(pruebaURI)
                    .addProperty(VCARD.FN, contenido);
            System.out.println("IMPRIMIR MODELO");
            model_rdf.write(System.out);
        }
    }

    /**
     * Función que lista todos los ficheros xml que se encuentran en el directorio de la colección
     */
    private static void leer_ficheros_XML(String directorio){
        File directory = new File(directorio);
        System.out.println(directory);
        File[] files = directory.listFiles();
        Integer n = 0;
        if (files != null) {
            System.out.println("Documentos XML encontrados en el directorio:");
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".xml")) {
                    if(n == 0){
                        System.out.println(file.getName()); // <- imprime el nombre del fichero xml 
                        SemanticGenerator.parserXML(file);
                    } 
                    n = 1;
                }
            }
        } else {
            System.out.println("El directorio no existe o no es accesible. Listando archivos del directorio actual:");
        }
    }
}
