import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

public class SearchFiles {

    public static final int INITIAL_HITS = 100;

    public SearchFiles() {
    }

    public static void main(String[] args) throws Exception {
        String index = "index";
        String infoNeeds = "consultas.txt";
        String output = "resultados.txt";
        String field = "content";
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new SpanishAnalyzer();

        try (BufferedReader queryReader = new BufferedReader(new FileReader(infoNeeds, StandardCharsets.UTF_8));
             BufferedWriter resultsWriter = new BufferedWriter(new FileWriter(output, StandardCharsets.UTF_8))) {

            String queryString;
            int n_query = 1;

            while ((queryString = queryReader.readLine()) != null) {
                queryString = queryString.trim();
                if (!queryString.isEmpty()) {
                    Double west = null, east = null, south = null, north = null;
                    // Obtenemos las partes de la consulta
                    String[] parts = queryString.split(",");
                    // Comprobamos que la consulta sea correcta
                    if (parts.length == 4 && parts[0].startsWith("spatial:")) {
                        try {
                            // Parse the values
                            west = Double.parseDouble(parts[0].substring(8)); //Quitamos la parte de spatial
                            east = Double.parseDouble(parts[1]);
                            south = Double.parseDouble(parts[2]);
                            north = Double.parseDouble(parts[3]);
                        } catch (NumberFormatException e) {
                            // Handle parsing errors
                            System.err.println("Error parsing spatial query values: " + e.getMessage());
                        }
                    } else {
                        // Handle invalid spatial query format
                        System.err.println("Invalid spatial query format: " + queryString);
                    }

                    // Check if all values were successfully extracted
                    if (west != null && east != null && south != null && north != null) {
                        Query westRangeQuery = DoublePoint.newRangeQuery("west", Double.NEGATIVE_INFINITY, east);
                        Query eastRangeQuery = DoublePoint.newRangeQuery("east", west, Double.POSITIVE_INFINITY);
                        Query southRangeQuery = DoublePoint.newRangeQuery("south", Double.NEGATIVE_INFINITY, north);
                        Query northRangeQuery = DoublePoint.newRangeQuery("north", south, Double.POSITIVE_INFINITY);

                        BooleanQuery query = new BooleanQuery.Builder()
                                .add(westRangeQuery, BooleanClause.Occur.MUST)
                                .add(eastRangeQuery, BooleanClause.Occur.MUST)
                                .add(southRangeQuery, BooleanClause.Occur.MUST)
                                .add(northRangeQuery, BooleanClause.Occur.MUST)
                                .build();
                        // NO SE PUEDE PONER EN ESTO MISMO EL NORMAL, ya que queremos que sea spatial or normalquery


                        showResults(searcher, query, n_query, resultsWriter);
                        n_query++;
                    }
                }
            }
        }
    }

    public static void showResults(IndexSearcher searcher, Query query,int n_query,BufferedWriter writer) throws IOException {
        TopDocs results = searcher.search(query, INITIAL_HITS);
        int numTotalHits = Math.toIntExact(results.totalHits.value);
        // Sort by score in descending order (higher scores first)

 
        if (numTotalHits > 0) {
            ScoreDoc[] hits = searcher.search(query, numTotalHits).scoreDocs;
            StoredFields storedFields = searcher.storedFields();
            for (int i = 0; i < hits.length; i++) {
                Document doc = storedFields.document(hits[i].doc);
                String identifier = doc.get("identifier");
                String resultLine =n_query + "  " + (identifier != null ? identifier : "No identifier for this element.");
                writer.write(resultLine);
                writer.newLine();


            }
        }
    }
}
