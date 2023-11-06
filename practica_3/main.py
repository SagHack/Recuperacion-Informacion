import sys
import pandas as pd

# Función que verifica que los parámetros de entrada son correctos
def comprobar_antes_ejecutar():
    if len(sys.argv) != 7:
        print("Uso: python main.py -qrels <qrelsFileName> -results <resultsFileName> -output <outputFileName>")
        sys.exit(1)

    qrelsFileName = None
    resultsFileName = None
    outputFileName = None

    i = 1
    while i < len(sys.argv):
        if sys.argv[i] == '-qrels':
            qrelsFileName = sys.argv[i + 1]
        elif sys.argv[i] == '-results':
            resultsFileName = sys.argv[i + 1]
        elif sys.argv[i] == '-output':
            outputFileName = sys.argv[i + 1]
        i += 2

    # Verificar que se proporcionen todos los argumentos necesarios
    if None in (qrelsFileName, resultsFileName, outputFileName):
        print("Faltan argumentos. Uso: python script.py -qrels <qrelsFileName> -results <resultsFileName> -output <outputFileName>")
        sys.exit(1)
    
    return qrelsFileName, resultsFileName, outputFileName


# Clase que almacena los datos que se van a utilizar para calcular las métricas, además de las funciones necesarias 
# para realizar lo que se pide en la práctica
class RelevanceData:
    def __init__(self, qrels_file, results_file, output_file):
        self.df_qrels = pd.read_csv(qrels_file, sep='\t', header=None, names=["information_need", "document_id", "relevancy"])  # Almacena los valores de los jueces
        self.df_results = pd.read_csv(results_file, sep='\t', header=None, names=["information_need", "document_id"])   # Almacena todos los resultados del fichero espeficado
        self.df_results_50 = []                 # Almacena los primeros 50 resultados de una necesidad de información concreta
        self.output_file = output_file
        
        self.total_iNeed = 0                    # Variable que almacena el número total de necesidades de información
        self.total_precision = 0                # Variable que almacena el total de la precision
        self.total_recall = 0                   # Variable que almacena el total del recall
        self.total_f1 = 0                       # Variable que almacena la suma de todas las f1
        self.total_prec_at_10 = 0               # Variable que almacena la suma de todos los prec_at_10
        self.total_MAP = 0                      # Variable que almacena la suma de todos los average_precision
        self.total_interpolated_recall = []     
        self.total_interpolated_precision = []

    # Función que devuelve un vector con los resultados
    def vector_results(self):
        return self.df_results

    # Devuelve True si el documento con doc_id de la necesidad de información p_iNeed es relevante. 
    # En caso contrario False.
    def is_relevant(self, p_iNeed, p_doc_id):
        for i in range(len(self.df_qrels)):
            t_iNeed = self.df_qrels["information_need"][i]
            t_doc_id = self.df_qrels["document_id"][i]
            relevancy = self.df_qrels["relevancy"][i] == 1

            # Si coincide la necesidad de información y el doc_id, entonces devolvemos True (si relevancy=1), False (si relevancy=0)
            if p_iNeed == t_iNeed and p_doc_id == t_doc_id:
                return relevancy
        return False
            
    # Devuelve el total de documentos que hay en la necesidad de información iNeed
    def total_docs_real(self, iNeed):
        total = len(self.df_qrels[self.df_qrels["information_need"] == iNeed])
        return total
    
    # Devuelve el total de documentos recuperados de una necesidad de información concreta(está espeficado en df_results_50, ya que 
    # este almacena los 50 primeros de la necesidad de información que se esta calculando), se limita a los 50 primeros.
    def total_docs_recuperados(self, iNeed):
        total = len(self.df_results_50)
        return total
    
    # Devuelve el total de documentos relevantes
    def total_docs_relevantes(self,iNeed):
        relevant_docs = self.df_qrels[(self.df_qrels["information_need"] == iNeed) & (self.df_qrels["relevancy"] == 1)]
        total = len(relevant_docs)
        return total

    # Función que devuelve un contador con los true positives que existen
    def true_positives(self, iNeed):
        count = 0
        recuperados = self.total_docs_recuperados(iNeed)
        for i in range(recuperados):
            p_doc_id = self.df_results_50["document_id"].iloc[i]
            if self.is_relevant(iNeed, p_doc_id):
                count += 1
        return count

    # Función que devuelve un contador con los false positives que existen
    def false_positives(self, iNeed):
        count = 0
        recuperados = self.total_docs_recuperados(iNeed)
        for i in range(recuperados):
            p_doc_id = self.df_results_50["document_id"].iloc[i]
            if not self.is_relevant(iNeed, p_doc_id):
                count += 1
        return count
    
    # Función que devuelve un contador con los false negatives que existen    
    def false_negatives(self,iNeed):
        n_relevantes = self.total_docs_relevantes(iNeed)
        
        return n_relevantes - self.true_positives(iNeed)
    
    # Función que devuelve un contador con los true negatives que existen
    def true_negatives(self,iNeed):
        n_no_relevantes = self.total_docs_real(iNeed) - self.total_docs_relevantes(iNeed)
        tp = self.true_positives(iNeed)
        return n_no_relevantes - tp
    
    # Función que dado los true positives y false positives calcula la precision de 
    def precision(self, iNeed, tp, fp):
        if (tp + fp) == 0:
            return 0.0
        return tp / (tp + fp)

    # Función que dado los true positives y false negatives calcula el recall 
    def recall(self, iNeed, tp, fn):
        if (tp + fn) == 0:
            return 0.0
        return tp / (tp + fn)
    
    # Función que dado la precision y el recall calcula la f1
    def f1(self,iNeed, precision, recall):
        if (precision + recall) == 0:
            return 0.0
        return (2*precision*recall) / (precision + recall)
    
    # Función que calcula el prec_at_10
    def prec_at_10(self, iNeed):
        count_tp = 0
        for i in range(10): 
            p_doc_id = self.df_results_50["document_id"].iloc[i]
            if self.is_relevant(iNeed, p_doc_id):
                count_tp += 1

        return count_tp / 10 if count_tp > 0 else 0.0
    
    # Función que busca el documento en el que se encuentra el id_documento y devuelve su posición
    def buscar_documento(self,iNeed,id_documento):
        tam_qrels = int(len(self.df_qrels))
        for i in range(tam_qrels):
            if(id_documento == self.df_qrels["document_id"][i] and iNeed == self.df_qrels["information_need"][i]):
                return i
        return -1
    
    # Función que caclula el average precision
    def average_precision(self, iNeed):
        precision_acumulada = 0
        tot_rel = 0
        tot_no_rel = 0
        precision = 0
        n_total = self.total_docs_recuperados(iNeed)

        for i in range(n_total):
            if self.is_relevant(iNeed,self.df_results_50["document_id"][i]):
                tot_rel += 1
                precision =(tot_rel / (tot_rel + tot_no_rel))
                precision_acumulada += precision
            else:
                tot_no_rel += 1
        if(tot_rel == 0):
            return 0.0
        return precision_acumulada / tot_rel

    # Función que calcula el recall_precision
    def recall_precision(self, iNeed):
        tot_rel = 0
        tot_rel_real = self.total_docs_relevantes(iNeed)
        tot_no_rel = 0
        precision = []
        recall = []
        
        n_total = self.total_docs_recuperados(iNeed)
        for i in range(n_total):
            if self.is_relevant(iNeed,self.df_results_50["document_id"][i]):
                tot_rel += 1
                fn = tot_rel_real - tot_rel 
                r = (tot_rel / (tot_rel + fn))
                p = (tot_rel / (tot_rel + tot_no_rel))
                precision.append(p)
                recall.append(r)
            else:
                tot_no_rel += 1
        return recall ,precision

    # Función que calcula el interpolated_recall_precision
    def interpolated_recall_precision(self, iNeed):
        recall, precision = self.recall_precision(iNeed)
        interpolated_recall = [i / 10 for i in range(11)]  # 11 evenly spaced values from 0 to 1
        interpolated_precision = []

        max_precision = 0
        for r_target in interpolated_recall:
            while recall and recall[0] < r_target:
                max_precision = max(max_precision, precision[0])
                recall.pop(0)
                precision.pop(0)
            interpolated_precision.append(max_precision)

        rec, prec = self.recall_precision(iNeed)
        k = 0
        j = 0.0
        for i in range(11):
            max_precision = 0
            for j in range(len(rec)):
                if rec[j] >= i/10 and prec[j] > max_precision:
                    max_precision = max(max_precision, prec[j])
            interpolated_precision[i] = max_precision
                       
        return interpolated_recall, interpolated_precision

    # Función que devuelve toda la información de una necesidad de información concreta
    def imprimirInfo(self,iNeed):

        tp = self.true_positives(iNeed)     # Se obtienen los true positives
        fp = self.false_positives(iNeed)    # Se obtienen los false positives 
        fn = self.false_negatives(iNeed)    # Se obtienen los false negatives

        self.total_iNeed +=1
        precision = self.precision(iNeed, tp, fp)
        recall = self.recall(iNeed, tp, fn)
        f1 = self.f1(iNeed, precision, recall)
        prec_10 = self.prec_at_10(iNeed)
        # devuelve array de average_precision, recall_precision y interpolated_recall_precision
        average_precision = self.average_precision(iNeed)
        r_p_1,r_p_2 = self.recall_precision(iNeed) 
        i_r_p_1,i_r_p_2 = self.interpolated_recall_precision(iNeed)
        
        resultado = "INFORMATION_NEED " + str(iNeed) + "\n"
        resultado += "precision {:.3f}\n".format(precision)
        self.total_precision += precision

        resultado += "recall {:.3f}\n".format(recall)
        self.total_recall += recall

        resultado += "F1 {:.3f}\n".format(f1)
        self.total_f1 += f1

        resultado += "prec@10 {:.3f}\n".format(prec_10)
        self.total_prec_at_10 += prec_10

        resultado += "average_precision {:.3f}\n".format(average_precision)
        self.total_MAP += average_precision

        resultado += "recall_precision\n"
        for r, p in zip(r_p_1, r_p_2):
            resultado += f"{r:.3f} {p:.3f}\n"
            
        resultado += "interpolated_recall_precision\n"
        i = 0
        for r, p in zip(i_r_p_1, i_r_p_2):
            resultado += f"{r:.3f} {p:.3f}\n"
            if self.total_iNeed == 1:
                self.total_interpolated_recall.append(r)
                self.total_interpolated_precision.append(p)
            else:
                self.total_interpolated_recall[i] += r
                self.total_interpolated_precision[i] += p
            i += 1

        resultado += "\n"
        return resultado  

    # Función que devuelve toda la información de la ejecución
    def imprimir_total(self):
        resultado = "TOTAL\n"
        resultado += "precision {:.3f}\n".format(self.total_precision / self.total_iNeed)
        resultado += "recall {:.3f}\n".format(self.total_recall / self.total_iNeed)
        resultado += "F1 {:.3f}\n".format(self.total_f1 / self.total_iNeed)
        resultado += "prec@10 {:.3f}\n".format(self.total_prec_at_10 / self.total_iNeed)
        resultado += "MAP {:.3f}\n".format(self.total_MAP / self.total_iNeed)
        resultado += "interpolated_recall_precision\n"
        
        for r, p in zip(self.total_interpolated_recall, self.total_interpolated_precision):
            r /= self.total_iNeed
            p /= self.total_iNeed
            resultado += f"{r:.3f} {p:.3f}\n"            
        return resultado

# Función principal
def main():
    qrels_file, results_file, output_file = comprobar_antes_ejecutar()

    relevance_data = RelevanceData(qrels_file, results_file, output_file)
    vector_resultados = relevance_data.vector_results()
    all_information_needs = vector_resultados["information_need"].unique()  
    resultado = ""
    for iNeed in all_information_needs:
        print("Calculando métricas para la necesidad de información ", iNeed)
        relevance_data.df_results_50 = relevance_data.df_results[relevance_data.df_results["information_need"] == iNeed].head(50).reset_index(drop=True)
        resultado +=relevance_data.imprimirInfo(iNeed)
    with open(relevance_data.output_file, "w") as archivo:
        archivo.write(resultado)
        archivo.write(relevance_data.imprimir_total())
    archivo.close()

main()
