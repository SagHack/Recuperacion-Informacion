package IR.Practica5;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.base.Sys;

import org.apache.jena.vocabulary.VCARD;
import java.io.FileOutputStream;*/

// Se crea la clase Semantic Generator
public class SemanticGenerator {
 
    public static void main(String[] args) {

        comprobarParametros(args.length); // Se comprueba que el número de parámetros es correcto

        // args[0] = -rdf
        // args[1] = <rdfPath>
        // args[2] = -docs
        // args[3] = <docsPath>

        // Si los parámetros son correctos, se obtienen los valores de los mismos
        if (!args[0].equals("-rdf") || !args[2].equals("-docs")) {
            System.out.println("Invalid command-line arguments.\nUsage: SemanticSearcher -rdf <rdfPath> -docs <docsPath>");
            System.exit(1);
        }
        String rdfPath = args[1];
        String docsPath = args[3];

        // A_CreacionRDF.miMetodo();
        // Se genera el model RDF a partir de la colección de docuemntos
        Model rdfModel = generarModeloRDF(docsPath);


        // listarXML(docsPath);
        //generarArchivoTxt(rdfPath);


        // lógica para procesar las consultas SPARQL desde archivo XML


        //File queriesFile = new File("queries.xml");
        //if(queriesFile.exists()) {
        // Se lee el archivo XML y procesar las consultas SPARQL
        // Se ejecutan las consultas SPARQL en el grafo RDF
        // y se obtienen los resultados para cada necesidad de informacion

        // imprimir el contenido
        // } else {
        // System.out.println("El archivo de consultas XML no existe.");
        //System.exit(1);
        // }

        // lógica par agenerar el grafo RDF a partir de la colección de docuemntos


    }

    /**
     * Función que gnera el model RDF
     */
    private static Model generarModeloRDF(String docsPath){
        Model model = ModelFactory.createDefaultModel();

        // Se define un espacio de nombres para las propiedades
        String ns = "http://tu.esquema#";
        model.setNsPrefix("esquema", ns);

        // Se crea los recursos para la clase genérica Documento y sus atributos
        Resource documento = model.createResource(ns + "Documento");
        Property tieneDate = model.createProperty(ns + "tieneDate");
        Property tieneDescription = model.createProperty(ns + "tieneDescription");
        Property tieneLanguage = model.createProperty(ns + "tieneLanguage");
        Property tieneTitle = model.createProperty(ns + "tieneTitle");

        // Se asocian los atributos a la calse generica documento
        model.add(documento, tieneDate, "Valor de Date");
        model.add(documento, tieneDescription, "Valor de Description");
        model.add(documento, tieneLanguage, "Valor de Language");
        model.add(documento, tieneTitle, "Valor de Title");

        // Se crean recuross para las subclases específicas
        Resource tfg = model.createResource(ns + "TFG");
        Resource tesis = model.createResource(ns + "Tesis");
        Resource url = model.createResource(ns + "URL");
        Resource persona = model.createResource(ns + "Persona");
        Resource subject = model.createResource(ns + "Subject");
        Resource publisher = model.createResource(ns + "Publisher");

        // Asocia subclases a la clase genérica Documento
        Property esSubclaseDe = model.createProperty(ns + "esSubclaseDe");
        model.add(tfg, esSubclaseDe, documento);
        model.add(tesis, esSubclaseDe, documento);
        model.add(url, esSubclaseDe, documento);
        model.add(persona, esSubclaseDe, documento);
        model.add(subject, esSubclaseDe, documento);
        model.add(publisher, esSubclaseDe, documento);

        // Imprime el modelo RDF
        model.write(System.out, "TURTLE");

        return model;
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
     * Función que genera un archivo de texto con el mensaje "Generado correctamente" en el directorio especificado
     */
    /**private static void generarArchivoTxt(String rdfPath) {
        String fileName = "mensaje.txt";
        File txtFile = new File(rdfPath, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(txtFile))) {
            writer.write("Generado correctamente");
            System.out.println("Archivo de texto generado correctamente en " + fileName);
        } catch (IOException e) {
            System.out.println("Error al generar el archivo de texto: " + e.getMessage());
        }
    }*/

    /**
     * Función que lista todos los ficheros xml que se encuentran en el directorio de la colección
     */
    /**private static void listarXML(String directorio){
        File directory = new File(directorio);
        File[] files = directory.listFiles();

        if (files != null) {
            System.out.println("Documentos XML encontrados en el directorio:");
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".xml")) {
                    System.out.println(file.getName());
                }
            }
        } else {
            System.out.println(directorio);
            System.out.println("El directorio no existe o no es accesible.");
        }
    }*/

    /**
     * Función que lista todos los ficheros xml que se encuentran en el directorio de la colección
     */
    /**private static void listarXML1(String directorio) {
        File directory = new File(directorio);
        File[] files = directory.listFiles();

        if (files != null) {
            System.out.println("Documentos XML encontrados en el directorio:");
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".xml")) {
                    System.out.println(file.getName());
                }
            }
        } else {
            System.out.println("No se puede acceder al directorio: " + directorio);
            System.out.println("Listando otros archivos en la ubicación:");
            listarOtrosArchivosEnUbicacion(directorio);
        }
    }*/

    /**
     * Función que lista otros archivos en la ubicación si no se puede acceder al directorio
     */
    /**private static void listarOtrosArchivosEnUbicacion(String ubicacion) {
        File location = new File(ubicacion);
        File[] files = location.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    System.out.println(file.getName());
                }
            }
        } else {
            System.out.println("No se pueden listar otros archivos en la ubicación.");
        }
    }*/
}