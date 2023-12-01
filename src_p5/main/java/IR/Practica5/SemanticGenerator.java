package IR.Practica5;

import org.apache.jena.rdf.model.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import java.nio.file.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.VCARD;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.VCARD;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.util.Objects;


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



        // Se genera el model RDF
        //Model modelRDF = SemanticGenerator.generarModeloRDF();

        // Leer cada XML de docsPath y generar su modelo RDF
        // El modelo RDF se guarda en el archivo rdfPath con el mismo nombre del fichero xml
        

        // Bucle que lee cada fichero del directorio docsPath y comprueba si es un fichero xml
        // Si se trata de un fichero XML imprime el nombre por pantalla del fichero

        













        // Se genera el modelo RDF desde los archivos XML en el directorio docsPath
        // y se guarda en el archivo rdfPath
        // Por cada xml se guarda el correspondiente en rdfPath (su modelo)
        

        // Crear SPARQL







        // Llamar a la función generarModeloDesdeXML para cada archivo en el directorio docsPath
        // File docsDirectory = new File(docsPath);
        // if (docsDirectory.isDirectory()) {
        //     File[] listOfFiles = docsDirectory.listFiles();
        //     if (listOfFiles != null) {
        //         for (File file : listOfFiles) {
        //             if (file.isFile() && file.getName().endsWith(".xml")) {
        //                 System.out.println("Procesando archivo: " + file.getName());
        //                 SemanticGenerator.generarModeloDesdeXML(file.getAbsolutePath());
        //             }
        //         }
        //     } else {
        //         System.out.println("El directorio de documentos está vacío.");
        //     }
        // } else {
        //     System.out.println("La ruta especificada para documentos no es un directorio válido.");
        // }
        // Se escribe el modelo RDF en un archivo
        /**try {
            FileOutputStream output = new FileOutputStream(rdfPath);
            modelRDF.write(output, "TURTLE");
        } catch (FileNotFoundException e) {
            System.out.println("Error al escribir el modelo RDF en el archivo.");
            e.printStackTrace();
        }
        */

        // Aquí puedes agregar código para cargar datos RDF desde un archivo externo
        // SemanticGenerator.cargarDatosRDF(modelRDF, rdfPath);

        // Ejemplo de consulta SPARQL
        // SemanticGenerator.realizarConsultaSPARQL(modelRDF);

        // modificarArchivosXML(docsPath, rdfPath);
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


    /**
     * Genera un modelo RDF
     */
    public static Model generarModeloRDF(String title, String description, String date, String language, 
                                        String identifier, String creator, String contributor, String publisher,
                                         String relation, String rights, String subject, String type) {
        Model modelRDF = ModelFactory.createDefaultModel();

        String ns = "http://tu.esquema#";
        modelRDF.setNsPrefix("esquema", ns);

        // Se crea los recursos para la clase genérica Documento y sus atributos
        Resource documento = modelRDF.createResource(ns + "Documento");
        Property tieneDate = modelRDF.createProperty(ns + "tieneDate");
        Property tieneDescription = modelRDF.createProperty(ns + "tieneDescription");
        Property tieneLanguage = modelRDF.createProperty(ns + "tieneLanguage");
        Property tieneTitle = modelRDF.createProperty(ns + "tieneTitle");

        // Se asocian los atributos a la calse generica documento
        modelRDF.add(documento, tieneDate, "Valor de Date");
        modelRDF.add(documento, tieneDescription, "Valor de Description");
        modelRDF.add(documento, tieneLanguage, "Valor de Language");
        modelRDF.add(documento, tieneTitle, "Valor de Title");

        // Se crean recuross para las subclases específicas
        Resource tfg = modelRDF.createResource(ns + "TFG");
        Resource tesis = modelRDF.createResource(ns + "Tesis");
        Resource url = modelRDF.createResource(ns + "URL");
        Resource persona = modelRDF.createResource(ns + "Persona");
        Resource subject = modelRDF.createResource(ns + "Subject");
        Resource publisher = modelRDF.createResource(ns + "Publisher");

        // Asocia subclases a la clase genérica Documento
        Property esSubclaseDe = modelRDF.createProperty(ns + "esSubclaseDe");
        modelRDF.add(tfg, esSubclaseDe, documento);
        modelRDF.add(tesis, esSubclaseDe, documento);
        modelRDF.add(url, esSubclaseDe, documento);
        modelRDF.add(persona, esSubclaseDe, documento);
        modelRDF.add(subject, esSubclaseDe, documento);
        modelRDF.add(publisher, esSubclaseDe, documento);

        // Imprime el modelo RDF
        modelRDF.write(System.out, "TURTLE");

        return modelRDF;
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


            generarRDFModelo(title, description, date, language, identifier, creator, contributor,
                                    publisher, relation, rights, subject, type);
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
                                String identifier, String creator, String contributor, String 
                                String publisher, String relation, String rights, String subject, String type){
        // Se crea el modelo RDF
        Model modelRDF = ModelFactory.createDefaultModel();

        // Se crea el namespace
        String ns = "http://tu.esquema#";
        modelRDF.setNsPrefix("esquema", ns);

        // Se crea los recursos para la clase genérica Documento y sus atributos
        Resource documento = modelRDF.createResource(ns + "Documento");
        Property tieneDate = modelRDF.createProperty(ns + "tieneDate");
        Property tieneDescription = modelRDF.createProperty(ns + "tieneDescription");
        Property tieneLanguage = modelRDF.createProperty(ns + "tieneLanguage");
        Property tieneTitle = modelRDF.createProperty(ns + "tieneTitle");

        // Se asocian los atributos a la calse generica documento
        modelRDF.add(documento, tieneDate, date);
        modelRDF.add(documento, tieneDescription, description);
        modelRDF.add(documento, tieneLanguage, language);
        modelRDF.add(documento, tieneTitle, title);
        
        // Se crean recuross para las subclases específicas
        Resource tfg = modelRDF.createResource(ns + "TFG");
        Resource tesis = modelRDF.createResource(ns + "Tesis");
        Resource url = modelRDF.createResource(ns + "URL");
        Resource persona = modelRDF.createResource(ns + "Persona");
        Resource subject = modelRDF.createResource(ns + "Subject");
        Resource publisher = modelRDF.createResource(ns + "Publisher");

        // Asocia subclases a la clase genérica Documento
        Property esSubclaseDe = modelRDF.createProperty(ns + "esSubclaseDe");
        modelRDF.add(tfg, esSubclaseDe, documento);
        modelRDF.add(tesis, esSubclaseDe, documento);
        modelRDF.add(url, esSubclaseDe, documento);
        modelRDF.add(persona, esSubclaseDe, documento);
        modelRDF.add(subject, esSubclaseDe, documento);
        modelRDF.add(publisher, esSubclaseDe, documento);

        // Imprime el modelo RDF
        modelRDF.write(System.out, "TURTLE");
    
        

    }

    // // parserXML(file, doc, "dc:identifier", "identifier",);
    // private static void parserXML(Model model_rdf,File file, String tag, String campo) {
        
    //     try {
    //         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    //         DocumentBuilder builder = factory.newDocumentBuilder();
    //         org.w3c.dom.Document documento = builder.parse(file);
            
    //         org.w3c.dom.NodeList nList = documento.getElementsByTagName(tag);
    //         int n = nList.getLength();
    //         for (int i = 0; i < n; i++) {
    //             org.w3c.dom.Node nodo = nList.item(i);
    //             String contenido = nodo.getTextContent().toLowerCase();

    //             if(Objects.equals(campo, "type")){
    //                 int dashIndex = contenido.indexOf("-");
    //                 if (contenido.contains("-")) {
    //                     contenido = contenido.substring(contenido.indexOf("-") + 1).trim();
    //                 }
    //             }
    //             if(Objects.equals(campo, "language")){
    //                 //Si no al buscar por language:es, lo detecta como una palabra vacia y no busca
    //                 if(Objects.equals(contenido, "es") || Objects.equals(contenido, "spa")){
    //                     contenido = "spanish";
    //                 }else if(Objects.equals(contenido, "en")){
    //                     contenido = "english";
    //                 }
    //             }
    //             if(Objects.equals(campo, "created") || Objects.equals(campo, "issued")){
    //                 contenido = contenido.replaceAll("[/\\-]", ""); // Elimina "/" y "-"
    //             }

    //             add_to_RDF(model_rdf,contenido,tag);

    //         }
    //     } catch (ParserConfigurationException e) {
    //         // Handle ParserConfigurationException
    //         e.printStackTrace();
    //     } catch (IOException e) {
    //         // Handle IOException
    //         e.printStackTrace();
    //     } catch (SAXException e) {
    //         // Handle SAXException
    //         e.printStackTrace();
    //     }
    // }

    

       





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


    






    public static String extractDCDate(String xmlFileName) {
        try {
            File xmlFile = new File(xmlFileName);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML file
            Document document = builder.parse(xmlFile);

            // Find the dc:date element
            NodeList nodeList = document.getElementsByTagName("dc:date");

            if (nodeList.getLength() > 0) {
                Node dcdateNode = nodeList.item(0);

                // Extract the text content of dc:date element
                return dcdateNode.getTextContent();
            } else {
                System.out.println("No dc:date element found in the XML document.");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }











    public static Model generarModeloDesdeXML(String filepath) {
        
        Model modelRDF = ModelFactory.createDefaultModel();
        
        // Leer del fichero XML y obtener la informacion para el modelo RDF
        
        
        
        // definiciones
        String personURI    = "http://somewhere/JohnSmith";
        String givenName    = "John";
        String familyName   = "Smith";
        String fullName     = givenName + " " + familyName;
        
        String ns = "http://tu.esquema#";
        modelRDF.setNsPrefix("esquema", ns);

        // Se crea los recursos para la clase genérica Documento y sus atributos
        Resource documento = modelRDF.createResource(ns + "Documento");
        Property tieneDate = modelRDF.createProperty(ns + "tieneDate");
        Property tieneDescription = modelRDF.createProperty(ns + "tieneDescription");
        Property tieneLanguage = modelRDF.createProperty(ns + "tieneLanguage");
        Property tieneTitle = modelRDF.createProperty(ns + "tieneTitle");

        try {
            // Parsear el archivo XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(filepath));

            doc.getDocumentElement().normalize();

            // Obtener elementos del documento
            NodeList nodeList = doc.getElementsByTagName("oai_dc:dc");

            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Element element = (Element) nodeList.item(temp);

                // Asociar atributos del XML a los recursos RDF
                modelRDF.add(documento, tieneDate, getElementText(element, "dc:date"));
                modelRDF.add(documento, tieneDescription, getElementText(element, "dc:description"));
                modelRDF.add(documento, tieneLanguage, getElementText(element, "dc:language"));
                modelRDF.add(documento, tieneTitle, getElementText(element, "dc:title"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Imprimir el modelo RDF
        modelRDF.write(System.out, "TURTLE");

        return modelRDF;
    }

    private static String getElementText(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        } else {
            return null;
        }
    }

    // /**
    //  * Carga los datos RDF desde un archivo externo al modelo RDF existente
    //  */
    // public static void cargarDatosRDF(Model modelRDF, String rdfPath) {
    //     try {
    //         modelRDF.read(new FileInputStream(rdfPath), null, "TURTLE");
    //     } catch (FileNotFoundException e) {
    //         System.out.println("Error al cargar datos RDF desde el archivo.");
    //         e.printStackTrace();
    //     }
    // }

    // /**
    //  * Ejemplo de realizar una consulta SPARQL en el modelo RDF
    //  */
    // public static void realizarConsultaSPARQL(Model modelRDF) {
    //     // Aquí puedes agregar tu consulta SPARQL
    //     String sparqlQuery = "SELECT ?s WHERE { ?s <http://tu.esquema#tieneTitle> ?title }";

    //     // Ejecuta la consulta SPARQL en el modelo RDF
    //     Query query = QueryFactory.create(sparqlQuery);
    //     QueryExecution qexec = QueryExecutionFactory.create(query, modelRDF);
    //     ResultSet results = qexec.execSelect();

    //     // Imprime los resultados de la consulta
    //     ResultSetFormatter.out(System.out, results);

    //     // Libera los recursos de la consulta
    //     qexec.close();
    // }

    // /**
    //  * Modifica el contenido de los archivos XML en el directorio docsPath
    //  * y los guarda en el directorio rdfPath con consultas SPARQL
    //  */
    // public static void modificarArchivosXML(String docsPath, String rdfPath) {
    //     try {
    //         Path sourceDir = Paths.get(docsPath);
    //         Path targetDir = Paths.get(rdfPath);

    //         // Verifica si el directorio de documentos existe y es un directorio válido
    //         if (Files.exists(sourceDir) && Files.isDirectory(sourceDir)) {
    //             // Listar archivos en el directorio de documentos
    //             try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir, "*.xml")) {
    //                 for (Path sourceFile : stream) {
    //                     // Leer el contenido del archivo XML
    //                     String xmlContent = new String(Files.readAllBytes(sourceFile), StandardCharsets.UTF_8);

    //                     // Modificar el contenido del archivo XML con la consulta SPARQL
    //                     String modifiedContent = generarConsultaSPARQL(xmlContent);

    //                     // Crear el nuevo nombre del archivo en el directorio RDF
    //                     Path targetFile = targetDir.resolve(sourceFile.getFileName());

    //                     // Guardar el contenido modificado en el nuevo archivo
    //                     Files.write(targetFile, modifiedContent.getBytes(StandardCharsets.UTF_8));

    //                     System.out.println("Archivo modificado y guardado en: " + targetFile);
    //                 }
    //             }
    //         } else {
    //             System.out.println("El directorio de documentos especificado no existe o no es un directorio válido.");
    //         }
    //     } catch (IOException e) {
    //         System.out.println("Error al modificar archivos XML: " + e.getMessage());
    //         e.printStackTrace();
    //     }
    // }

    // /**
    //  * Genera una consulta SPARQL basada en el contenido del archivo XML
    //  */
    // private static String generarConsultaSPARQL(String xmlContent) {
    //     // Aquí puedes personalizar la lógica para generar la consulta SPARQL
    //     // En este ejemplo, simplemente se reemplaza el contenido con una consulta de ejemplo
    //     return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><informationNeeds>\n" +
    //             "<informationNeed>\n" +
    //             "<identifier>101-4</identifier>\n" +
    //             "<text>SELECT...</text>\n" +
    //             "</informationNeed>\n" +
    //             "<informationNeed>\n" +
    //             "<identifier>106-4</identifier>\n" +
    //             "<text>SELECT...</text>\n" +
    //             "</informationNeed>\n" +
    //             "</informationNeeds>";
    // }

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
