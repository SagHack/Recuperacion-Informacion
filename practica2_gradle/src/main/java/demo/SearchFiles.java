import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import demo.SpanishAnalyzer2;
import org.apache.lucene.analysis.Analyzer;
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
        Analyzer analyzer = new SpanishAnalyzer2();

        try (BufferedReader queryReader = new BufferedReader(new FileReader(infoNeeds, StandardCharsets.UTF_8));
             BufferedWriter resultsWriter = new BufferedWriter(new FileWriter(output, StandardCharsets.UTF_8))) {

            String queryString="",spatialString="",normalString="";
            int n_query = 1;
            while ((queryString = queryReader.readLine()) != null) {
                BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
                // Guardamos, si no es una query string
                String[] queries = queryString.split(" ");
                String normalQuery = "";


                for (int i = 0; i < queries.length; i++) {
                    //Si es una consulta espacial
                    if (queries[i].startsWith("spatial:")) {
                        Double west = null, east = null, south = null, north = null;
                        try {
                            String[] parts = queries[i].split(",");
                            // Parse the values
                            west = Double.parseDouble(parts[0].substring(8)); //Quitamos la parte de spatial
                            east = Double.parseDouble(parts[1]);
                            south = Double.parseDouble(parts[2]);
                            north = Double.parseDouble(parts[3]);
                        } catch (NumberFormatException e) {
                            // Handle parsing errors
                            System.err.println("Error parsing spatial query values: " + e.getMessage());
                        }

                        // Check if all values were successfully extracted
                        if (west != null && east != null && south != null && north != null) {
                            Query westRangeQuery = DoublePoint.newRangeQuery("west", Double.NEGATIVE_INFINITY, east);
                            Query eastRangeQuery = DoublePoint.newRangeQuery("east", west, Double.POSITIVE_INFINITY);
                            Query southRangeQuery = DoublePoint.newRangeQuery("south", Double.NEGATIVE_INFINITY, north);
                            Query northRangeQuery = DoublePoint.newRangeQuery("north", south, Double.POSITIVE_INFINITY);

                            BooleanQuery.Builder spatialQuery = new BooleanQuery.Builder();
                            // Query espacial
                            spatialQuery.add(westRangeQuery, BooleanClause.Occur.MUST)
                                    .add(eastRangeQuery, BooleanClause.Occur.MUST)
                                    .add(southRangeQuery, BooleanClause.Occur.MUST)
                                    .add(northRangeQuery, BooleanClause.Occur.MUST);

                            finalQuery.add(spatialQuery.build(), BooleanClause.Occur.SHOULD);
                        }
                    }else { // Consulta normal
                        normalQuery += queries[i] + " ";
                    }
                }

                if(normalQuery != ""){
                    System.out.println("entro");
                    QueryParser parser = new QueryParser("", analyzer);
                    Query query = parser.parse(normalQuery);
                    finalQuery.add(query, BooleanClause.Occur.SHOULD);
                }

                showResults(searcher, finalQuery.build(), n_query, resultsWriter);
                
                n_query++;
            }
        }
    }






    public static void showResults(IndexSearcher searcher, Query query,int n_query,BufferedWriter writer) throws IOException {
        System.out.println(query.toString());
        TopDocs results = searcher.search(query, INITIAL_HITS);
        int numTotalHits = Math.toIntExact(results.totalHits.value);
        // Sort by score in descending order (higher scores first)

        if (numTotalHits > 0) {
            ScoreDoc[] hits = searcher.search(query, numTotalHits).scoreDocs;
            StoredFields storedFields = searcher.storedFields();
            String resultLine = n_query + "\t";
            for (int i = 0; i < hits.length; i++) {
                Document doc = storedFields.document(hits[i].doc);
                String identifier = doc.get("identifier").split("-")[0];
                resultLine +=identifier;
                if(i!=hits.length-1){
                    resultLine += ",";
                }
                // Si quiero mostrar información respecto a los documentos devueltos
                //System.out.println(searcher.explain ( query , hits [i].doc ));
            }
            writer.write(resultLine);

            writer.newLine();
        }
    }
}
