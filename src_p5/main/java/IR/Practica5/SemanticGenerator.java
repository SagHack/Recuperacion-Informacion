package IR.Practica5;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

// Se crea la clase Semantic Generator
public class SemanticGenerator {
 
    public static void main(String[] args) {

        if (args.length < 4) {
            System.out.println("Uso: SemanticSearcher -rdf <rdfPath> -docs <docsPath>");
            System.exit(1);
        }

        // Si los parámetros son correctos, se obtienen los valores de los mismos
        String rdfPath = null;
        String docsPath = null;

        for (int i = 0; i < args.length; i++) {
            if ("-rdf".equals(args[i]) && i + 1 < args.length) {
                rdfPath = args[i + 1];

            } else if ("-docs".equals(args[i]) && i + 1 < args.length) {
                docsPath = args[i + 1];
            }
        }

        // Verificar que se proporcionaron ambas rutas
        if (rdfPath == null || docsPath == null) {
            System.out.println("Se requieren ambas rutas: -rdf <rdfPath> -docs <docsPath>");
            System.exit(1);
        }

        // listarXML(docsPath);
        generarArchivoTxt(rdfPath);


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
     * Función que genera un archivo de texto con el mensaje "Generado correctamente" en el directorio especificado
     */
    private static void generarArchivoTxt(String rdfPath) {
        File txtFile = new File(rdfPath, "mensaje.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(txtFile))) {
            writer.write("Generado correctamente en " + rdfPath);
            System.out.println("Archivo de texto generado correctamente en " + txtFile.getAbsolutePath());
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