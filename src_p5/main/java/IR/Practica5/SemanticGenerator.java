package IR.Practica5;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.base.Sys;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.VCARD;
import java.io.FileOutputStream;

// Se crea la clase Semantic Generator
public class SemanticGenerator {
 
    public static void main(String[] args) {

        comprobarParametros(args.length); // Se comprueba que el número de parámetros es correcto

        // args[0] = -rdf
        // args[1] = <rdfPath>
        // args[2] = -docs
        // args[3] = <docsPath>

        // Si los parámetros son correctos, se obtienen los valores de los mismos
        // String rdfPath = args[1];
        // String docsPath = args[3];

        // Se genera el model RDF a partir de la colección de docuemntos
        //Model rdfModel = generarModeloRDF(docsPath);

        System.out.println("FUNCIONA Y EJECUTA");
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
        // Implementar lógica

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
    private static void generarArchivoTxt(String rdfPath) {
        String fileName = "mensaje.txt";
        File txtFile = new File(rdfPath, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(txtFile))) {
            writer.write("Generado correctamente");
            System.out.println("Archivo de texto generado correctamente en " + fileName);
        } catch (IOException e) {
            System.out.println("Error al generar el archivo de texto: " + e.getMessage());
        }
    }

    /**
     * Función que lista todos los ficheros xml que se encuentran en el directorio de la colección
     */
    private static void listarXML(String directorio){
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
    }

    /**
     * Función que lista todos los ficheros xml que se encuentran en el directorio de la colección
     */
    private static void listarXML1(String directorio) {
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
    }

    /**
     * Función que lista otros archivos en la ubicación si no se puede acceder al directorio
     */
    private static void listarOtrosArchivosEnUbicacion(String ubicacion) {
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
    }
}