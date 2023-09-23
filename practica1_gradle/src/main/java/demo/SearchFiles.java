import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.document.Document;
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

            while ((queryString = queryReader.readLine()) != null) {
                queryString = queryString.trim();
                if (!queryString.isEmpty()) {
                    QueryParser parser = new QueryParser(field, analyzer); // Replace with the actual field name to search
                    Query query = parser.parse(queryString);
                    showResults(searcher, query, resultsWriter);
                }
            }
            System.out.println("Results are in the file: " + output);
        }
    }

    public static void showResults(IndexSearcher searcher, Query query, BufferedWriter writer) throws IOException {
        System.out.println("Searching for: " + query.toString());
        Sort sort = new Sort(new SortField(null, SortField.Type.SCORE, true));
        TopDocs results = searcher.search(query, INITIAL_HITS,sort);
        int numTotalHits = Math.toIntExact(results.totalHits.value);
        writer.write("Searching for: " + query.toString());
        writer.newLine();
        // Sort by score in descending order (higher scores first)


        if (numTotalHits > 0) {
            ScoreDoc[] hits = searcher.search(query, numTotalHits).scoreDocs;
            StoredFields storedFields = searcher.storedFields();
            for (int i = 0; i < hits.length; i++) {
                Document doc = storedFields.document(hits[i].doc);
                String identifier = doc.get("identifier");
                String resultLine = (i + 1) + ". " + (identifier != null ? identifier : "No identifier for this element.");
                writer.write(resultLine);
                writer.newLine();

//                String additionalInfoLine = "\tdoc=" + hits[i].doc + " score=" + hits[i].score;
//                writer.write(additionalInfoLine);
//                writer.newLine();
            }
        }
    }
}
