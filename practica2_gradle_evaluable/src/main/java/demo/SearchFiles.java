import demo.SpanishAnalyzer2;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;  
import opennlp.tools.postag.POSTaggerME;

import opennlp.tools.util.Span;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.Collator;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.text.Normalizer;

public class SearchFiles {
    private static final int INITIAL_HITS = 1000;

    public enum Estado {
        TIPO_TRABAJO,
        IDIOMA,
        ACERCA_DE,
        FECHA,
        HECHO_POR,
        PERSONA,
        DEPARTAMENTO,
        SIGLO,
        EJEMPLO,
        SIN_DETERMINAR
    }

    static Analyzer analyzer = new SpanishAnalyzer2();
    static BooleanQuery.Builder boolQuery = new BooleanQuery.Builder();
    static int agnoActual = 2023;

    private static Map<String, String> idiomasConCodigo = new HashMap<>();

    static {
        // Inicializar el mapa de idiomas con códigos
        idiomasConCodigo.put("espaol", "spanish");
        idiomasConCodigo.put("ingles", "english");
        idiomasConCodigo.put("chino", "chinese");
        idiomasConCodigo.put("italiano", "italian");
    }

    private static Map<Integer, String> numerosRomanos = new HashMap<>();

    static{
    // Agregar números romanos desde 1 hasta 21 al Map
        numerosRomanos.put(1,"I");
        numerosRomanos.put(2,"II");
        numerosRomanos.put(3,"III");
        numerosRomanos.put(4,"IV");
        numerosRomanos.put(5,"V");
        numerosRomanos.put(6,"VI");
        numerosRomanos.put(7,"VII");
        numerosRomanos.put(8,"VIII");
        numerosRomanos.put(9,"IX");
        numerosRomanos.put(10,"X");
        numerosRomanos.put(11,"XI");
        numerosRomanos.put(12,"XII");
        numerosRomanos.put(13,"XIII");
        numerosRomanos.put(14,"XIV");
        numerosRomanos.put(15,"XV");
        numerosRomanos.put(16,"XVI");
        numerosRomanos.put(17,"XVII");
        numerosRomanos.put(18,"XVIII");
        numerosRomanos.put(19,"XIX");
        numerosRomanos.put(20,"XX");
        numerosRomanos.put(21,"XXI");
    }




    public static boolean esTrabajo(String input) {
            // Expresión regular para "trabajo" o "trabajos" o "Trabajos" o "Trabajo"
            String regex = "(?i)trabajos?";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);
            return matcher.find();
        }

       

        public static void addQuery(String query_string) throws ParseException {
            //System.out.println("QUERY: " + query_string);
            if(!Objects.equals(query_string, "")) {
                QueryParser parser = new QueryParser("", analyzer);
                Query query = parser.parse(query_string);
                boolQuery.add(query, BooleanClause.Occur.MUST);
            }
        }

        public static void addQueryAcercaDE(String query_string) throws ParseException {
            String[] fields = {"title", "subject", "description"};
            QueryParser multifieldParser = new MultiFieldQueryParser(fields, analyzer);
            Query query = multifieldParser.parse(query_string);
            boolQuery.add(query, BooleanClause.Occur.MUST);
        }



        public static boolean esUnIdioma(String idioma) {
            if(idioma.equals("lenguaje")){
                return true;
            }else{
                return false;
            }
        }

        public static String obtenerCodigoDeIdioma(String idioma) {
            String normalizedIdioma = idioma.replaceAll("[^\\p{ASCII}]", "");
            normalizedIdioma = normalizedIdioma.toLowerCase();
            return idiomasConCodigo.get(normalizedIdioma);
        }


        public static boolean esUnVerboAcercaDelTrabajo(String verbo) {
            //Se podría ampliar la lista de verbos simplemente modificando esta lista, y funcionaria para todos los demás
            List<String> listaVerbos = List.of("traten", "sobre", "relacionados", "ser");
            return listaVerbos.contains(verbo);
        }

        public static boolean esUnaPalabraFecha(String palabra) {
            List<String> listaPalabras = List.of( "entre","ultimos","últimos","ltimos");
            palabra = palabra.replaceAll("[^\\p{ASCII}]", "");
            return listaPalabras.contains(palabra);
        }

        public static boolean esUnaPersona(String palabra, String[] names) {
            //Iteramos sobre el array de nombres que hemos obtenido con el nameFinder
            for (String name : names) {
                if (name.equals(palabra)) {
                    return true;
                }
            }
            return false;
        }

        public static boolean esUnEjemplo(String palabra){
            return Objects.equals(palabra, "ejemplo");
        }
        

        
        public static boolean esUnSiglo(String palabra) {
            return Objects.equals(palabra, "siglo");
        }

        public static boolean esUnDeparatamento(String word) {
            return word.equalsIgnoreCase("departamento") || word.equalsIgnoreCase("departamentos");
        }

        public static boolean esMaster(String word) {
            word = word.replaceAll("[^\\p{ASCII}]", "");
            return word.equalsIgnoreCase("máster") || word.equalsIgnoreCase("master") || word.equalsIgnoreCase("tfm") || word.equalsIgnoreCase("mster");
        }

        public static boolean esTFG(String word) {
            return word.equalsIgnoreCase("grado") || word.equalsIgnoreCase("tfg");
        }

        public static boolean esProyectoFin(String word) {
            return word.equalsIgnoreCase("proyecto") || word.equalsIgnoreCase("pfc");
        }

        public static boolean esTesis(String word) {
            return word.equalsIgnoreCase("tesis");
        }

        public static String generarConsultaORDesdeNumeroRomano(String numeroRomano) {
            StringBuilder consulta = new StringBuilder();
            boolean primerElemento = true;
            boolean out = false;

            
            Iterator<Map.Entry<Integer, String>> iterator = numerosRomanos.entrySet().iterator();
            while (iterator.hasNext() && !out) {
                Map.Entry<Integer, String> entry = iterator.next();
                Integer numeroDecimal = entry.getKey();
                String numeroRomanoEnMap = entry.getValue();
                if (numeroRomano.equals(numeroRomanoEnMap)) {
                    consulta.append(" OR ");
                    consulta.append("title:").append(numeroRomanoEnMap);
                    out = true;
                }else{
                    if(primerElemento){
                        consulta.append("title:").append(numeroRomanoEnMap);
                        primerElemento = false;
                    }else{
                        consulta.append(" OR ");
                        consulta.append("title:").append(numeroRomanoEnMap);
                    }
                }


            }
            return consulta.toString();
        }

        public static BooleanQuery procesarNecesidadInformacion(String input, String identifier) throws IOException, ParseException {
        boolQuery = new BooleanQuery.Builder();  
        Estado prevEstado = Estado.SIN_DETERMINAR;
        Estado estado = Estado.SIN_DETERMINAR;

        // Cargamos el modelo de POS tagger
        POSModel model = new POSModelLoader().load(new File("opennlp-es-pos-perceptron-pos-es.model"));  
  
        // Inicializamos el POS tagger
        POSTaggerME tagger = new POSTaggerME(model);  

        // Cargamos el modelo para identificar personas
        TokenNameFinderModel nameFinderModel = new TokenNameFinderModel(new File("es-ner-person.bin"));

        // Inicializamos el nameFinder
        NameFinderME nameFinder = new NameFinderME(nameFinderModel);
        input = input.replaceAll("[¿?.,]", "");


        String[] tokens = input.split(" ");
        String normalizedToken = "";
        for (int k=0;k<tokens.length;k++) {
            normalizedToken = tokens[k].replaceAll("[^\\p{ASCII}]", "");
            // Hay problemas con las tildes, y si no esta con tilde el nameFinder no lo reconoce.
            if(normalizedToken.equals("Lpez")){
                tokens[k] = "López";
            }
        }
        // Perform POS tagging  
        String[] tags = tagger.tag(tokens);  

        // Extraemos los nombres y los guardamos en el array names
        Span[] nameSpans = nameFinder.find(tokens);
        String[] names = new String[nameSpans.length];

        // Guardamos en el array names todos los nombres
        for (int i = 0; i < nameSpans.length; i++) {
            int start = nameSpans[i].getStart();
            int end = nameSpans[i].getEnd();
            names[i] = String.join(" ", Arrays.copyOfRange(tokens, start, end));
        }

        StringBuilder subquery = new StringBuilder();
        boolean es_una_or = false; 
        boolean es_una_not = false;

        // Recorremos todos los tokens
        for(int i = 0;i<tokens.length;i++) {
            //Es una OR
            if (Objects.equals(tags[i], "CC") && estado != Estado.SIN_DETERMINAR) {
                if (Objects.equals(tokens[i], "o")) {
                    subquery.append(" OR ");
                    es_una_or = true;
                    i++;
                }
            }
            // Es una NOT
            if (Objects.equals(tags[i], "RN")) {
                es_una_not = true;
                prevEstado = estado;
                // Añado la subquery a la global
                if(prevEstado == Estado.ACERCA_DE){
                    addQueryAcercaDE(String.valueOf(subquery));
                }else{
                    addQuery(String.valueOf(subquery));
                }
                subquery = new StringBuilder("");
                estado = Estado.SIN_DETERMINAR;
            }

            //System.out.println("ITERACION: " + estado + " -> " + tokens[i] + "OR:" +  es_una_or);

            if (esUnIdioma(tokens[i]) || (es_una_or && estado == Estado.IDIOMA)) {
                prevEstado = estado;
                estado = Estado.IDIOMA;
                i++;
            } else if (esUnaPalabraFecha(tokens[i]) || Objects.equals(tags[i], "Z") || (es_una_or && estado == Estado.FECHA)) {
                prevEstado = estado;
                estado = Estado.FECHA;
            } else if (esTrabajo(tokens[i]) || (es_una_or && estado == Estado.TIPO_TRABAJO)) { // Para detectar el tipo de trabajo o sobre que trata
                prevEstado = estado;
                estado = Estado.TIPO_TRABAJO;
                if((esTrabajo(tokens[i]))){
                    i++;
                }
            }else if(esUnEjemplo(tokens[i]) || (es_una_or && estado == Estado.EJEMPLO)){
                i++;
                prevEstado = estado;
                estado = Estado.EJEMPLO;
            }else if(esUnSiglo(tokens[i]) || (es_una_or && estado == Estado.SIGLO)){
                i++;
                prevEstado = estado;
                estado = Estado.SIGLO;
            }else if(esUnaPersona(tokens[i],names) || (es_una_or && estado == Estado.PERSONA)){
                prevEstado = estado;
                estado = Estado.PERSONA;
            }else if(esUnDeparatamento(tokens[i]) || (es_una_or && estado == Estado.DEPARTAMENTO)){
                prevEstado = estado;
                estado = Estado.DEPARTAMENTO;
                i++;
            }else if ((esUnVerboAcercaDelTrabajo(tokens[i]) || (es_una_or && estado == Estado.ACERCA_DE)) && !es_una_not) {
                prevEstado = estado;
                estado = Estado.ACERCA_DE;
                if(esUnVerboAcercaDelTrabajo(tokens[i])){
                    i++;
                }
            }else{ 
                prevEstado = estado;
                if(estado != Estado.ACERCA_DE && estado != Estado.EJEMPLO && !es_una_not) { 
                    estado = Estado.SIN_DETERMINAR;
                }  
            }

            // Añado la subquery a la global
            if(prevEstado != estado && prevEstado != Estado.SIN_DETERMINAR){
                if(prevEstado == Estado.ACERCA_DE){
                    addQueryAcercaDE(String.valueOf(subquery));
                }else{
                    addQuery(String.valueOf(subquery));
                }
                subquery = new StringBuilder("");
                es_una_not = false;
            }

            if (estado == Estado.IDIOMA) {
                subquery.append("language:").append(obtenerCodigoDeIdioma(tokens[i]));
            } else if (estado == Estado.TIPO_TRABAJO) {
                boolean out = false;
                //Estamos ante un type or subject.
                while (i < tokens.length && !out) {
                    if (esMaster(tokens[i])) {
                        subquery.append("type:TFM");
                        out=true;
                    } else if (esTFG(tokens[i])) {
                        subquery.append("type:TFG");
                        out=true;
                    } else if (esProyectoFin(tokens[i])) {
                        subquery.append("type:PFC");
                        out=true;
                    } else if (esTesis(tokens[i])) { //zaguan el id:1975
                        subquery.append("type:TESIS");
                        out=true;
                    }else if (Objects.equals(tags[i], "NC")) { // Si es un Noun Common
                        if (!Objects.equals(tokens[i], "fin")) { // Si es distinto de fin, ya que fin es irrelevante porque no aporta info
                            subquery.append("subject:").append(tokens[i]);
                            out = true;
                        }
                    }
                    if(!out){
                        i++;
                    }
                }
            }else if (estado == Estado.PERSONA) {
                // Como hay un problema con la tilde, la eliminamos y cambiamos por Lopez, si no pone un interrogante donde la ó
                if(tokens[i].replaceAll("[^\\p{ASCII}]", "").equals("Lpez")){
                    tokens[i] = "Lopez";
                }
                subquery = new StringBuilder("creator:" + tokens[i] + " OR " + "contributor:" + tokens[i]);
            }else if (estado == Estado.DEPARTAMENTO) {
                while(!Objects.equals(tags[i], "NC") && i < tokens.length){
                    i++;
                }
                if(i<tokens.length){
                    subquery.append("publisher:").append(tokens[i]);
                }
            }else if (estado == Estado.SIGLO) { 
                if(es_una_not){
                    subquery.append(generarConsultaORDesdeNumeroRomano(tokens[i]));
                }else{
                    subquery.append("title:").append(tokens[i]);
                }

            }else if (estado == Estado.FECHA) {
                String tokenNormalizado = tokens[i].replaceAll("[^\\p{ASCII}]", "");

                if (tokenNormalizado.equals("ltimos") || tokenNormalizado.equals("ltimo")) { // los x últimos años
                    int agnoInicio = agnoActual - Integer.parseInt(tokens[i+1]);
                    subquery.append("date:[").append(Integer.toString(agnoInicio)).append(" TO ").append(Integer.toString(agnoActual)).append("]");
                    i++;
                } else { //Entonces estamos ante un año, o un rango pero especificando ambos años
                    boolean found = false;
                    i++;
                    int j = i + 1;
                    while (j < tokens.length) {
                        if (Objects.equals(tags[j], "Z")) { //Hemos encontrado otro año antes
                            subquery.append("date:[").append(tokens[i]).append(" TO ").append(tokens[j]).append("]");
                            found = true;
                            break;
                        } else if (Objects.equals(tags[j], "NC")) { // Ya aunque haya un año, no es sobre el rango
                            found = false;
                            break;
                        }
                        j++;
                    }
                    i = j;
                    if (!found && i < tokens.length) {
                        if (tags[i] == "Z") {
                            subquery.append("date:").append(tokens[i]);
                        }
                    }
                }
            } else if (estado == Estado.ACERCA_DE || estado == Estado.EJEMPLO) {
                // Si es un verbo lo ignoro
                if(!esUnVerboAcercaDelTrabajo(tokens[i])){
                    subquery.append(tokens[i] + " ");
                }

            }
            es_una_or = false;
        }
            if(!Objects.equals(String.valueOf(subquery), "")){ // Cuidado con el lenguage, ya que se considera palabra vacia la en
                if(estado == Estado.ACERCA_DE || estado == Estado.EJEMPLO){
                    addQueryAcercaDE(String.valueOf(subquery));
                }else{
                    addQuery(String.valueOf(subquery));
                }
            }
            nameFinder.clearAdaptiveData();
            return boolQuery.build();
    }


    public static void showResults(IndexSearcher searcher, Query query, BufferedWriter writer,String identifier_query) throws IOException {
        System.out.println("Searching for: " + query.toString());
        Sort sort = new Sort(new SortField(null, SortField.Type.SCORE, true));
        TopDocs results = searcher.search(query, INITIAL_HITS,sort);
        int numTotalHits = Math.toIntExact(results.totalHits.value);
        // Sort by score in descending order (higher scores first)

        if (numTotalHits > 0) {
            System.out.println("HAY HITS");
            ScoreDoc[] hits = searcher.search(query, numTotalHits).scoreDocs;
            StoredFields storedFields = searcher.storedFields();
            for (int i = 0; i < hits.length; i++) {
                org.apache.lucene.document.Document doc = storedFields.document(hits[i].doc);
                String identifier = doc.get("identifier");
                writer.write(identifier_query + "   " + identifier);
                writer.newLine();
            }
        }
    }


    public static void main(String[] args) throws Exception {
        String index = "index";
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("equipo014.txt"));

            File xmlFile = new File("necesidadesInformacion.xml");
            FileInputStream inputStream = new FileInputStream(xmlFile);
            InputStreamReader reader_file = new InputStreamReader(inputStream, StandardCharsets.UTF_8); // Especificar la codificación UTF-8

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(new InputSource(reader_file));

            doc.getDocumentElement().normalize();
            NodeList informationNeedList = doc.getElementsByTagName("informationNeed");

           for (int i = 0; i < informationNeedList.getLength(); i++) {
               Node node = informationNeedList.item(i);
               if (node.getNodeType() == Node.ELEMENT_NODE) {
                   Element element = (Element) node;
                   String identifier = element.getElementsByTagName("identifier").item(0).getTextContent();
                   String text = element.getElementsByTagName("text").item(0).getTextContent();
                   Query q = procesarNecesidadInformacion(text,identifier);
                   showResults(searcher,q,writer,identifier);
               }
           }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        
}