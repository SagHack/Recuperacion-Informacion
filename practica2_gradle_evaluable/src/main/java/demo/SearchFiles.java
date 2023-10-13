import demo.SpanishAnalyzer2;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;  
import opennlp.tools.postag.POSTaggerME;

import opennlp.tools.util.Span;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
  
import java.io.File;

import java.text.Normalizer;

public class SearchFiles {
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
        idiomasConCodigo.put("español", "spanish");
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
            List<String> listaIdiomasNormalizada = List.of("español", "ingles", "chino", "italiano","frances", "aleman", "portugues", "japones", "coreano");
            return listaIdiomasNormalizada.contains(idioma);
        }

        public static String obtenerCodigoDeIdioma(String idioma) {
            // Normalizar los caracteres y convertir a minúsculas antes de buscar el código.
            return idiomasConCodigo.get(idioma);
        }


        public static boolean esUnVerboAcercaDelTrabajo(String verbo) { //Se podría ampliar la lista de verbos simplemente modificando esta lista, y funcionaria para todos los demás

            List<String> listaIdiomasNormalizada = List.of("traten", "sobre", "relacionados", "ser");

            // Normalizamos los caracteres y los convertimos a minúsculas antes de comparar.
            verbo = Normalizer.normalize(verbo, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
            return listaIdiomasNormalizada.contains(verbo);
        }

        public static boolean esUnaPalabraFecha(String palabra) {
            List<String> listaPalabras = List.of("publicados", "entre","ultimos");
            return listaPalabras.contains(palabra);
        }

        public static boolean esUnaPersona(String palabra, String[] names) {
            for (String name : names) {
                if (name.equals(palabra)) {
                    return true; // Found a match
                }
            }
            return false; // Word not found in the names array
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
            return word.equalsIgnoreCase("máster") || word.equalsIgnoreCase("master") || word.equalsIgnoreCase("tfm");
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




        public static void procesarNecesidadInformacion(String input,String identifier) throws IOException, ParseException {
        Estado prevEstado = Estado.SIN_DETERMINAR;
        Estado estado = Estado.SIN_DETERMINAR;

        // Load the pretrained model  
        POSModel model = new POSModelLoader().load(new File("opennlp-es-pos-perceptron-pos-es.model"));  
  
        // Initialize the POS tagger  
        POSTaggerME tagger = new POSTaggerME(model);  

        // Load your custom Name Finder model
        TokenNameFinderModel nameFinderModel = new TokenNameFinderModel(new File("es-ner-person.bin"));

        // Initialize the Name Finder
        NameFinderME nameFinder = new NameFinderME(nameFinderModel);
        input = input.replaceAll("[^a-zA-Z0-9\\sáéíóúÁÉÍÓÚñÑ]", "");




        
        // Input text  
        String[] tokens = input.split(" ");
        // Perform POS tagging  
        String[] tags = tagger.tag(tokens);  

        // Extraemos los nombres y los guardamos en el array names
        Span[] nameSpans = nameFinder.find(tokens);
        String[] names = new String[nameSpans.length];

        for (int i = 0; i < nameSpans.length; i++) {
            int start = nameSpans[i].getStart();
            int end = nameSpans[i].getEnd();
            names[i] = String.join(" ", Arrays.copyOfRange(tokens, start, end));
        }

        String query = "";
        StringBuilder subquery = new StringBuilder();
        String token = "";

        boolean es_una_or = false; 
        boolean es_una_not = false;
        // Recorremos todos los tokens
        for(int i = 0;i<tokens.length;i++) {
            if (Objects.equals(tags[i], "CC") && estado != Estado.SIN_DETERMINAR) { //Es una OR
                if (Objects.equals(tokens[i], "o")) {
                    subquery.append(" OR ");
                    es_una_or = true;
                    //System.out.println("OOOOOR");
                    i++;
                }
            }
            
            if (Objects.equals(tags[i], "RN")) { //Es una NOT){
                es_una_not = true;
                prevEstado = estado;
                // Añado la subquery a la global
                if(prevEstado == Estado.ACERCA_DE){addQueryAcercaDE(String.valueOf(subquery));
                }else{addQuery(String.valueOf(subquery));}
                subquery = new StringBuilder("");
                estado = Estado.SIN_DETERMINAR;
            }

            //System.out.println("ITERACION: " + estado + " -> " + tokens[i] + "OR:" +  es_una_or);

            if (esUnIdioma(tokens[i]) || (es_una_or && estado == Estado.IDIOMA)) { // Lo hemos hecho más global, ya que no tiene por que estar siempre precedida por lenguaje
                prevEstado = estado;
                estado = Estado.IDIOMA;
            } else if (esUnaPalabraFecha(tokens[i]) || Objects.equals(tags[i], "Z") || (es_una_or && estado == Estado.FECHA)) { //Si es un numero
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
                //System.out.println("SOBREEEEEEEEEEEE");
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
                //Estamos ante un type or subject. Tenemos que encontrar el proximo NC para asegurarnos de cual de los dos es
                while (i < tokens.length && !out) {
                    if (esMaster(tokens[i])) {
                        subquery.append("type:TFM");
                        out=true; // salgo del bucle
                    } else if (esTFG(tokens[i])) {
                        subquery.append("type:TFG");
                        out=true; // salgo del bucle
                    } else if (esProyectoFin(tokens[i])) {
                        subquery.append("type:PFC");
                        out=true; // salgo del bucle
                    } else if (esTesis(tokens[i])) { //zaguan el id:1975
                        subquery.append("type:TESIS");
                        out=true; // salgo del bucle
                    }else if (Objects.equals(tags[i], "NC")) { // Si es un Noun Common
                        if (!Objects.equals(tokens[i], "fin")) { // Si es distinto de fin, ya que fin es irrelevante porque no aporta info
                            subquery.append("subject:").append(tokens[i]);
                            out = true; // salgo del bucle
                        }
                    }
                    //System.out.println("BUCLE:" + tokens[i]);
                    if(!out){
                        i++;
                    }
                }
            }else if (estado == Estado.PERSONA) {
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
                if (Objects.equals(tokens[i - 1], "último") || Objects.equals(tokens[i - 1], "últimos")) { // los x últimos años
                    int agnoInicio = agnoActual - Integer.parseInt(tokens[i]);
                    subquery.append("date:[").append(Integer.toString(agnoInicio)).append(" TO ").append(Integer.toString(agnoActual)).append("]");
                } else { //Entonces estamos ante un año, o un rango pero especificando ambos años
                    boolean found = false;
                    i++;
                    int j = i + 1;
                    while (j < tokens.length) {
                        if (Objects.equals(tags[j], "Z")) { //Hemos encontrado otro año antes
                            subquery.append("date:[").append(tokens[i]).append(" TO ").append(tokens[j]).append("]");
                            found = true;
                            break;
                        } else if (Objects.equals(tags[j], "NC")) { //Ya aunque haya un año, no es sobre el rango
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

            // Cuando salimos del bucle añadimos las query que nos hemos dejado por añadir
            //addQuery(subquery);
            // Cleanup
            nameFinder.clearAdaptiveData();
            System.out.println("Generated Boolean Query: " + boolQuery.build().toString());

    }
    







    public static void main(String[] args) throws Exception {
        try {
            File xmlFile = new File("necesidadesInformacion.xml");
            FileInputStream inputStream = new FileInputStream(xmlFile);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            // Specify the character encoding when parsing the file
            Document doc = dBuilder.parse(inputStream, StandardCharsets.UTF_8.name());

            doc.getDocumentElement().normalize();
            NodeList informationNeedList = doc.getElementsByTagName("informationNeed");

            for (int i = 0; i < 1; i++) { // informationNeedList.getLength()
                Node node = informationNeedList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String identifier = element.getElementsByTagName("identifier").item(0).getTextContent();
                    String text = element.getElementsByTagName("text").item(0).getTextContent();

                    //procesarNecesidadInformacion(text,identifier);
                    //procesarNecesidadInformacion("¿Hay trabajos de fin de grado o trabajos de fin de master que traten sobre la animación o visión por computador publicados en los últimos 20 años, en lenguaje español?",identifier);
                   //procesarNecesidadInformacion("Quiero buscar trabajos de veterinaria relacionados con especies felinas (gatos) o especies caninas (perros) entre 2012 y 2018.",identifier);
                    //procesarNecesidadInformacion("Trabajos de fin de grado o master realizados o dirigidos por algún miembro de la familia López en el departamento de Informática.", identifier);
                  //procesarNecesidadInformacion("¿Qué trabajos hay sobre arte? Tienen que ser de arte aragonés, y que no traten temas posteriores al siglo XIX.",identifier);
                   procesarNecesidadInformacion("traten cadenas de proteinas o hallar pacientes similares basándose en sus síntomas.",identifier);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        
}