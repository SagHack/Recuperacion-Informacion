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
            Analyzer analyzer = new SpanishAnalyzer2();
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
                    doc.add(new StoredField("modified", file.lastModified()));

                    // Add the path of the file as a field named "path"
                    Field pathField = new StringField("path",file.getPath(),Field.Store.YES);
                    doc.add(pathField);

                    // Call parserXML for each specific tag you want to extract and index.
                    parserXML(file, doc, "dc:identifier", "identifier", false,true);
                    parserXML(file, doc, "dc:type", "type", false,false);
                    parserXML(file, doc, "dc:creator", "creator", true,false);
                    parserXML(file, doc, "dc:contributor", "contributor", true,false);
                    parserXML(file, doc, "dc:publisher", "publisher", true,false);
                    parserXML(file, doc, "dc:title", "title", true,false);
                    parserXML(file, doc, "dc:description", "description", true,false);
                    parserXML(file, doc, "dc:date", "date", false,false);
                    parserXML(file, doc, "dc:language", "language", false,false);

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

    /**
     * Parses an XML file and extracts content from specific XML tags to index in a Lucene Document.
     *
     * @param file       The XML file to be parsed.
     * @param doc        The Lucene Document where extracted content will be added.
     * @param tag        The XML tag to search for within the document.
     * @param campo      The name of the field in the Lucene Document where the content will be stored.
     * @param isTextField If true, the content will be stored as a TextField; if false, it will be stored as a StringField.
     * @param store      If true, the field will be stored in the index; if false, it won't be stored (only indexed for searching).
     * @throws IOException If there is a low-level I/O error while reading the XML file.
     */
    private static void parserXML(File file, Document doc, String tag, String campo, boolean isTextField,boolean store) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document documento = builder.parse(file);

            org.w3c.dom.NodeList nList = documento.getElementsByTagName(tag);
            int n = nList.getLength();
            for (int i = 0; i < n; i++) {
                org.w3c.dom.Node nodo = nList.item(i);
                String contenido = nodo.getTextContent();

                if(Objects.equals(campo, "type")){
                    contenido = contenido.substring(contenido.indexOf("-") + 1).trim();
                }
                if(Objects.equals(campo, "language")){
                    //Si no al buscar por language:es, lo detecta como una palabra vacia y no busca
                    if(Objects.equals(contenido, "es")){
                        contenido = "spanish";
                    }else if(Objects.equals(contenido, "en")){
                        contenido = "english";
                    }
                }
                if(Objects.equals(campo, "created") || Objects.equals(campo, "issued")){
                    contenido = contenido.replaceAll("[/\\-]", ""); // Elimina "/" y "-"
                }
                System.out.println(campo + ":" + contenido);
                if (isTextField) {
                    if(store){
                        doc.add(new TextField(campo, contenido, Field.Store.YES));
                    }else{
                        doc.add(new TextField(campo, contenido, Field.Store.NO));
                    }
                } else {
                    if(store){
                        doc.add(new StringField(campo, contenido, Field.Store.YES));
                    }else{
                        doc.add(new StringField(campo, contenido, Field.Store.NO));
                    }
                    
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