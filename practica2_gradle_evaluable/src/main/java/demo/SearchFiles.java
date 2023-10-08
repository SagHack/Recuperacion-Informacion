import opennlp.tools.cmdline.postag.POSModelLoader;  
import opennlp.tools.postag.POSModel;  
import opennlp.tools.postag.POSTaggerME;  
import opennlp.tools.util.Span;  
  
import java.io.File;  
  
public class SearchFiles {  
    public static void main(String[] args) throws Exception {  
        // Load the pretrained model  
        POSModel model = new POSModelLoader().load(new File("opennlp-es-pos-perceptron-pos-es.model"));  
  
        // Initialize the POS tagger  
        POSTaggerME tagger = new POSTaggerME(model);  
  
        // Input text  
        String[] tokens = "¿Hay trabajos de fin de grado o trabajos de fin de master que traten sobre la animación o visión por computador publicados en los últimos 20 años, en lenguaje español?".split(" ");
        
        // Perform POS tagging  
        String[] tags = tagger.tag(tokens);  
        String query = "";
        String token = "";
        int state = 0;

        for (int i = 0; i < tokens.length; i++) {
            System.out.println(tags[i] + ":"+tokens[i]);
        }


        // Print the tags  
        for (int i = 0; i < -1; i++) {
            token = tokens[i].toLowerCase();
            if(tags[i] == "NC" && token == "trabajos"){
                query += "type:";
                state = 0;
                break;
            }


            if(state == 0){ //Type or subject puede ser
                if(tags[i] == "VMN" || tags[i] == "VMI" || tags[i] == "NC"){
                    if(token == "grado"){
                        query +="tfg ";
                    }else if(token == "master"){
                        query += "master ";
                    }else{
                        query += token + " ";
                    }
                }
            }

            if(tags[i] == "CC"){ //OR
                query += "OR ";
            }
           
           
//
//           switch (tags[i]) {
//               case "NC":
//                   nombre
//                   break;
//               case "CC":
//                   query +=" OR ";
//                   break;
//               case "VMI":
//               case "VMN":
//                   verbo
//                   break;


            System.out.println(tags[i] + ":"+tokens[i]);
        }  
    }  
}