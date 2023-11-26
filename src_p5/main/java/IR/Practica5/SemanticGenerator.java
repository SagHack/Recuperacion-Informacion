import java.io.File;

// Se crea la clase Semantic Generator
public class SemanticGenerator {
 
    public static void main(String[] args){

        if(args.length < 4){
            System.out.println("Uso: SemanticSearcher -rdf <rdfPath> -docs <docsPath>");
            System.exit(1);
        }

        // Si los parámetros son correctos, se obtienen los valores de los mismos
        String rdfPath = null;
        String docsPath = null;

        for(int i = 0; i < args.length; i++){
            if("-rdf".equals(args[i]) && i + 1 < args.length){
                rdfPath = args[i+1];

            } else if("-docs".equals(args[i]) && i + 1 < args.length){
                docsPath = args[i+1];
            }
        }

        // Verificar que se proporcionaron ambas rutas
        if (rdfPath == null || docsPath == null) {
            System.out.println("Se requieren ambas rutas: -rdf <rdfPath> -docs <docsPath>");
            System.exit(1);
        }
    }
}