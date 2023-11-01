import sys
import pandas as pd
from collections import Counter
import graficas

class RelevanceData:
    def __init__(self, qrels_file, results_file, output_file):
        self.df_qrels = pd.read_csv(qrels_file, sep='\t', header=None, names=["information_need", "document_id", "relevancy"])
        self.df_results = pd.read_csv(results_file, sep='\t', header=None, names=["information_need", "document_id"])
        self.output_file = output_file
        
        self.total_iNeed = 0
        self.total_precision = 0
        self.total_recall = 0
        self.total_f1 = 0
        self.total_prec_at_10 = 0
        self.total_MAP = 0
        self.total_interpolated_recall = []
        self.total_interpolated_precision = []

    def vector_results(self):
        return self.df_results

    def is_relevant(self, p_iNeed, p_doc_id):
        for i in range(len(self.df_qrels)):
            t_iNeed = self.df_qrels["information_need"][i]
            t_doc_id = self.df_qrels["document_id"][i]
            relevancy = self.df_qrels["relevancy"][i] == 1

            # Si coincide la necesidad de información y el doc_id, entonces devolvemos True (si relevancy=1), False (si relevancy=0)
            if p_iNeed == t_iNeed and p_doc_id == t_doc_id:
                return relevancy
        return False
            
    def total_docs_real(self, iNeed):
        total = len(self.df_qrels[self.df_qrels["information_need"] == iNeed])
        return total
    
    def total_docs_recuperados(self, iNeed):
        total = len(self.df_results[self.df_results["information_need"] == iNeed])
        return total
    
    def total_docs_relevantes(self,iNeed):
        relevant_docs = self.df_qrels[(self.df_qrels["information_need"] == iNeed) & (self.df_qrels["relevancy"] == 1)]
        total = len(relevant_docs)
        return total

    def true_positives(self, iNeed):
        count = 0
        recuperados = self.total_docs_recuperados(iNeed)
        for i in range(recuperados):
            p_doc_id = self.df_results[self.df_results["information_need"] == iNeed]["document_id"].iloc[i]
            if self.is_relevant(iNeed, p_doc_id):
                count += 1
        return count

    def false_positives(self, iNeed):
        count = 0
        recuperados = self.total_docs_recuperados(iNeed)
        for i in range(recuperados):
            p_doc_id = self.df_results[self.df_results["information_need"] == iNeed]["document_id"].iloc[i]
            if not self.is_relevant(iNeed, p_doc_id):
                count += 1
        return count
    
    def false_negatives(self,iNeed):
        n_relevantes = self.total_docs_relevantes(iNeed)
        
        return n_relevantes - self.true_positives(iNeed)
    
    def true_negatives(self,iNeed):
        n_no_relevantes = self.total_docs_real(iNeed) - self.total_docs_relevantes(iNeed)
        tp = self.true_positives(iNeed)
        return n_no_relevantes - tp
    
    def precision(self, iNeed):
        tp = self.true_positives(iNeed)
        fp = self.false_positives(iNeed)
        if (tp + fp) == 0:
            return 0.0
        return tp / (tp + fp)

    def recall(self, iNeed):
        tp = self.true_positives(iNeed)
        fn = self.false_negatives(iNeed)
        if (tp + fn) == 0:
            return 0.0
        return tp / (tp + fn)
    
    def f1(self,iNeed):
        precision = self.precision(iNeed)
        recall = self.recall(iNeed)
        return (2*precision*recall) / (precision + recall)

    def prec_at_10(self, iNeed):
        tp = self.true_positives(iNeed)
        fp = self.false_positives(iNeed)
        docs_sorted = self.df_results[self.df_results["information_need"] == iNeed].sort_values(by="document_id")
        docs_sorted = docs_sorted.head(10)
        tp_at_10 = len(docs_sorted[docs_sorted["document_id"].isin(self.df_qrels[(self.df_qrels["information_need"] == iNeed) & (self.df_qrels["relevancy"] == 1)]["document_id"])])
        return tp_at_10 / 10 if tp_at_10 > 0 else 0.0
    
    

    def buscar_documento(self,iNeed,id_documento):
        tam_qrels = int(len(self.df_qrels))
        for i in range(tam_qrels):
            if(id_documento == self.df_qrels["document_id"][i] and iNeed == self.df_qrels["information_need"][i]):
                return i
        return -1
    

    def average_precision(self, iNeed):
        precision_acumulada = 0
        tot_rel = 0
        tot_no_rel = 0
        precision = 0
        n_total = self.total_docs_recuperados(iNeed)
        for i in range(n_total):
            if self.is_relevant(iNeed,self.df_results["document_id"][i]):
                tot_rel += 1
                precision =(tot_rel / (tot_rel + tot_no_rel))
                precision_acumulada += precision
            else:
                tot_no_rel += 1
        return precision_acumulada / tot_rel


    def recall_precision(self, iNeed):
        tot_rel = 0
        tot_rel_real = self.total_docs_relevantes(iNeed)
        tot_no_rel = 0
        precision = []
        recall = []
        
        n_total = self.total_docs_recuperados(iNeed)
        for i in range(n_total):
            if self.is_relevant(iNeed,self.df_results["document_id"][i]):
                tot_rel += 1
                fn = tot_rel_real - tot_rel 
                r = (tot_rel / (tot_rel + fn))
                p = (tot_rel / (tot_rel + tot_no_rel))
                precision.append(p)
                recall.append(r)
            else:
                tot_no_rel += 1
        return recall ,precision


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

        total_relevantes = 0
        total_no_relevantes = 0
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


    def imprimirInfo(self,iNeed):
        self.total_iNeed +=1
        precision = self.precision(iNeed)
        recall = self.recall(iNeed)
        f1 = self.f1(iNeed)
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


def main():
    qrels_file = "qrels.txt"  # Replace with your file name
    results_file = "results.txt"  # Replace with your file name
    output_file = "output_file.txt"  # Replace with your file name

    relevance_data = RelevanceData(qrels_file, results_file, output_file)
    vector_resultados = relevance_data.vector_results()
    all_information_needs = vector_resultados["information_need"].unique()  
    resultado = ""
    for iNeed in all_information_needs:
        resultado +=relevance_data.imprimirInfo(iNeed)
    with open(relevance_data.output_file, "w") as archivo:
        archivo.write(resultado)
        archivo.write(relevance_data.imprimir_total())
    graficas.generarGrafica(output_file)
    archivo.close()

main()
