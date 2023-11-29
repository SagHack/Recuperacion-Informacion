package IR.Practica5;
import java.io.File;


public class SemanticSearcher {
    

    public static void main(String[] args){

        if(args.length < 6){
            System.out.println("Uso: java SemanticSearcher -rdf <rdfPath> -infoNeeds <infoNeedsFile> -output <resultsFile>");
            System.exit(1);
        }

        // Si los parámetros son correctos, se obtienen los valores de los mismos
        String rdfPath = null;
        String infoNeedsFile = null;
        String resultsFile = null;

        for (int i = 0; i < args.length; i++) {
            if ("-rdf".equals(args[i]) && i + 1 < args.length) {
                rdfPath = args[i + 1];

            } else if ("-infoNeeds".equals(args[i]) && i + 1 < args.length) {
                infoNeedsFile = args[i + 1];
            } else if ("-output".equals(args[i]) && i + 1 < args.length) {
                resultsFile = args[i + 1];
            }
        }

        // Verificar que se proporcionaron todas las rutas necesarias
        if (rdfPath == null || infoNeedsFile == null || resultsFile == null) {
            System.out.println("Se requieren todas las rutas: -rdf <rdfPath> -infoNeeds <infoNeedsFile> -output <resultsFile>");
            System.exit(1);
        }

        // Validar la existencia de archivos
        if (!validarExistenciaArchivos(rdfPath, infoNeedsFile)) {
            System.out.println("Alguno de los archivos proporcionados no existe.");
            System.exit(1);
        }

        // lógica para procesar las necesidades de información desde un archivo XML
        // realizar busqueda en el grafo RDF para generar los resultados
        // TODO

        // imprimir ruta del archivo
        System.out.println("Archivo RDF: " + rdfPath);
        // imprimir ruta del archivo de necesiadades de informacion
        System.out.println("Archivo de Necesidades de Información: " + infoNeedsFile);
        // ruta del archivo de resutlados
        System.out.println("Archivo de Resultados: " + resultsFile);

    }
    private static boolean validarExistenciaArchivos(String... paths) {
        for (String path : paths) {
            File file = new File(path);
            if (!file.exists()) {
                return false;
            }
        }
        return true;
    }
}