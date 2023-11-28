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

        // lógica para procesar las necesidades de información desde un archivo XML
        // realizar busqueda en el grafo RDF para generar los resultados

        // imprimir ruta del archivo
        // imprimir ruta del archivo de necesiadades de informacion
        // ruta del archivo de resutlados

    }
}