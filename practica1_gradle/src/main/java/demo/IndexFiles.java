package demo;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;


/** Index all text files under a directory.
 * <p>
 * This is a command-line application demonstrating simple Lucene indexing.
 * Run it with no command-line arguments for usage information.
 */
public class IndexFiles {

    private IndexFiles() {}

    /** Index all text files under a directory. */
    public static void main(String[] args) {
        String usage = "java org.apache.lucene.demo.IndexFiles"
                + " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
                + "This indexes the documents in DOCS_PATH, creating a Lucene index"
                + "in INDEX_PATH that can be searched with SearchFiles";
        String indexPath = "index";
        String docsPath = null;
        boolean create = true;
        for(int i=0;i<args.length;i++) {
            if ("-index".equals(args[i])) {
                indexPath = args[i+1];
                i++;
            } else if ("-docs".equals(args[i])) {
                docsPath = args[i+1];
                i++;
            } else if ("-update".equals(args[i])) {
                create = false;
            }
        }

        if (docsPath == null) {
            System.err.println("Usage: " + usage);
            System.exit(1);
        }

        final File docDir = new File(docsPath);
        if (!docDir.exists() || !docDir.canRead()) {
            System.out.println("Document directory '" +docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
            System.exit(1);
        }

        Date start = new Date();
        try {
            System.out.println("Indexing to directory '" + indexPath + "'...");

            Directory dir = FSDirectory.open(Paths.get(indexPath));
            Analyzer analyzer = new SpanishAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

            if (create) {
                // Create a new index in the directory, removing any
                // previously indexed documents:
                iwc.setOpenMode(OpenMode.CREATE);
            } else {
                // Add new documents to an existing index:
                iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
            }

            // Optional: for better indexing performance, if you
            // are indexing many documents, increase the RAM
            // buffer.  But if you do this, increase the max heap
            // size to the JVM (eg add -Xmx512m or -Xmx1g):
            //
            // iwc.setRAMBufferSizeMB(256.0);

            IndexWriter writer = new IndexWriter(dir, iwc);
            indexDocs(writer, docDir);

            // NOTE: if you want to maximize search performance,
            // you can optionally call forceMerge here.  This can be
            // a terribly costly operation, so generally it's only
            // worth it when your index is relatively static (ie
            // you're done adding documents to it):
            //
            // writer.forceMerge(1);

            writer.close();

            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass() +
                    "\n with message: " + e.getMessage());
        }
    }

    /**
     * Indexes the given file using the given writer, or if a directory is given,
     * recurses over files and directories found under the given directory.
     *
     * @param writer Writer to the index where the given file/dir info will be stored
     * @param file The file to index, or the directory to recurse into to find files to index
     * @throws IOException If there is a low-level I/O error
     */
    static void indexDocs(IndexWriter writer, File file) throws IOException {
        if (file.canRead()) {
            if (file.isDirectory()) {
                String[] files = file.list();
                if (files != null) {
                    List<String> fileList = new LinkedList<>(Arrays.asList(files));
                    Collections.sort(fileList);
                    for (String fileName : fileList) {
                        indexDocs(writer, new File(file, fileName));
                    }
                }
            } else {
                FileInputStream fis;
                try {
                    fis = new FileInputStream(file);
                } catch (FileNotFoundException fnfe) {
                    return;
                }

                try {
                    Document doc = new Document();

                    // Add the last modified date of the file as a field named "modified".
                    //doc.add(new StoredField("modified", file.lastModified()));

                    // Add the contents of the file to a field named "contents".
                    //doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))));

                    // Call parserXML for each specific tag you want to extract and index.
                    parserXML(file, doc, "dc:title", "title", true);
                    parserXML(file, doc, "dc:identifier", "identifier", false);
                    parserXML(file, doc, "dc:subject", "subject", true);
                    parserXML(file, doc, "dc:type", "type", false);
                    parserXML(file, doc, "dc:description", "description", true);
                    parserXML(file, doc, "dc:creator", "creator", true);
                    parserXML(file, doc, "dc:publisher", "publisher", true);
                    parserXML(file, doc, "dc:format", "format", false);
                    parserXML(file, doc, "dc:language", "language", false);

                    if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
                        System.out.println("adding " + file);
                        writer.addDocument(doc);
                    } else {
                        System.out.println("updating " + file);
                        writer.updateDocument(new Term("path", file.getPath()), doc);
                    }
                } finally {
                    fis.close();
                }
            }
        }
    }

    private static void parserXML(File file, Document doc, String tag, String campo, boolean textField) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document documento = builder.parse(file);

            org.w3c.dom.NodeList nList = documento.getElementsByTagName(tag);

            int n = nList.getLength();
            for (int i = 0; i < n; i++) {
                org.w3c.dom.Node nodo = nList.item(i);
                String contenido = nodo.getTextContent();

                System.out.println(campo + ":" + contenido);
                if (textField) {
                    doc.add(new TextField(campo, contenido, Field.Store.NO));
                } else {
                    doc.add(new StringField(campo, contenido, Field.Store.YES));
                }
            }
        } catch (ParserConfigurationException e) {
            // Handle ParserConfigurationException
            e.printStackTrace();
        } catch (IOException e) {
            // Handle IOException
            e.printStackTrace();
        } catch (SAXException e) {
            // Handle SAXException
            e.printStackTrace();
        }
    }


}