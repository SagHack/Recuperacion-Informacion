package IR.Practica5;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;

import java.io.File;

public class SemanticSearcher {

    /**
     * Main principal
     */

    public static void main(String[] args) {
        // Se comprueba que el número de parámetros es correcto
        SemanticSearcher.comprobarParametros(args.length);

        // args[0] = -rdf
        // args[1] = <rdfPath>
        // args[2] = -infoNeeds
        // args[3] = <infoNeedsFile>
        // args[4] = -output
        // args[5] = <resultsFile>

        if (!args[0].equals("-rdf") || !args[2].equals("-infoNeeds") || !args[4].equals("-output")) {
            System.out.println("Argumentos incorrectos. Uso: SemanticSearcher -rdf <rdfPath> -infoNeeds <infoNeedsFile> -output <resultsFile>");
            System.exit(1);
        }


        // Specify the directory for the TDB dataset
        String datasetDir = "tdb_dataset";

        // Specify the directory containing RDF files
        String rdfFilesDir = args[1];

        // Create a TDB dataset
        Dataset dataset = TDBFactory.createDataset(datasetDir);

        // Iterate over RDF files in the directory
        File rdfDirectory = new File(rdfFilesDir);
        File[] rdfFiles = rdfDirectory.listFiles((dir, name) -> name.endsWith(".rdf"));

        if (rdfFiles != null) {
            for (File rdfFile : rdfFiles) {
                System.out.println("Indexing RDF file: " + rdfFile.getName());

                // Get the model from the dataset
                dataset.begin(ReadWrite.WRITE);
                try {
                    Model model = dataset.getDefaultModel();
                    model.read(rdfFile.getAbsolutePath());
                    dataset.commit();
                } finally {
                    dataset.end();
                }
            }
        }

        // Perform SPARQL query
        String queryString = "SELECT ?subject ?predicate ?object WHERE { ?subject ?predicate ?object }";
        dataset.begin(ReadWrite.READ);
        try {
            Query query = QueryFactory.create(queryString);
            QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
            ResultSet results = qexec.execSelect();

            // Process the query results
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                // Access variables in the solution
                //RDFNode subject = soln.get("subject");
                //RDFNode predicate = soln.get("predicate");
                //RDFNode object = soln.get("object");

                // Process the results as needed
            //    System.out.println(subject + " " + predicate + " " + object);
            }
        } finally {
            dataset.end();
        }
    }





    /**
     * Función que comprueba que el número de parámetros es correcto
     */
    private static void comprobarParametros(int numParametros){
        if (numParametros != 6) {
            System.out.println("Número incorrecto de parámetros.\nUso: SemanticSearcher -rdf <rdfPath> -infoNeeds <infoNeedsFile> -output <resultsFile>");
            System.exit(1);
        }
    }
}
