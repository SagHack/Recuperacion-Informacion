import sys
import pandas as pd
from collections import Counter


def main():
    if len(sys.argv) != 7:
        print("Uso: python script.py -qrels <qrelsFileName> -results <resultsFileName> -output <outputFileName>")
        sys.exit(1)

    qrelsFileName, resultsFileName, outputFileName = parse_arguments(sys.argv)
    df_qrels, df_results = load_data(qrelsFileName, resultsFileName)

    results_informationNeed = df_results[0]
    results_documentId = df_results[1]

    with open(outputFileName, "w") as archivo:
        process_data(results_informationNeed, results_documentId, df_qrels, archivo)



def parse_arguments(argv):
    qrelsFileName, resultsFileName, outputFileName = None, None, None
    i = 1
    while i < len(argv):
        if argv[i] == '-qrels':
            qrelsFileName = argv[i + 1]
        elif argv[i] == '-results':
            resultsFileName = argv[i + 1]
        elif argv[i] == '-output':
            outputFileName = argv[i + 1]
        i += 2

    if None in (qrelsFileName, resultsFileName, outputFileName):
        print("Faltan argumentos. Uso: python script.py -qrels <qrelsFileName> -results <resultsFileName> -output <outputFileName>")
        sys.exit(1)

    return qrelsFileName, resultsFileName, outputFileName

def load_data(qrelsFileName, resultsFileName):
    df_qrels = pd.read_csv(qrelsFileName, sep='\t', header=None)
    df_results = pd.read_csv(resultsFileName, sep='\t', header=None)
    return df_qrels, df_results


def true_positives():
    count = 0
    df_qrels, df_results = load_data()
    


def buscar_indice_numero(iNeed,):
    for i, valor in enumerate(df_qrels[1]):
        if valor == numero:
            return i
    return -1


def buscar_documento(informationNeed, id_documento, qrels_informationNeed, qrels_documentId):
    for i in range(len(qrels_informationNeed)):
        if id_documento == qrels_documentId[i] and informationNeed == qrels_informationNeed[i]:
            return i
    return -1

def calcular_precision(total_relevantes, total_no_relevantes):
    return "{:.3f}".format(total_relevantes / (total_relevantes + total_no_relevantes))

def calcular_recall(total_relevantes, no_detectados):
    return "{:.3f}".format(total_relevantes / (total_relevantes + no_detectados))

def calcular_f1(precision, recall):
    return "{:.3f}".format(2 * ((precision * recall) / (precision + recall)))

def calcular_prec10(total_relevantes_a_10):
    return "{:.3f}".format(total_relevantes_a_10 / 10)

def calcular_average_precision(total, n):
    return "{:.3f}".format(total / n)

def process_data(results_informationNeed, results_documentId, df_qrels, archivo):
    qrels_informationNeed = df_qrels[0]
    qrels_documentId = df_qrels[1]
    total_relevantes = 0
    total_no_relevantes = 0
    total_relevantes_a_10 = 0
    cont = 0
    sumatorio_precision = 0
    previous_informationNeed = None

    for i in range(len(results_documentId)):
        if results_informationNeed[i] != previous_informationNeed:
            if previous_informationNeed is not None:
                imprimir_informacion(total_relevantes, total_no_relevantes, len(results_documentId), previous_informationNeed, sumatorio_precision)
                total_no_relevantes = 0
                total_relevantes = 0
                total_relevantes_a_10 = 0
                cont = 0
                sumatorio_precision = 0
            previous_informationNeed = results_informationNeed[i]

        index = buscar_documento(results_informationNeed[i], results_documentId[i], qrels_informationNeed, qrels_documentId)
        if df_qrels[2][index] == 0:
            total_no_relevantes += 1
        else:
            total_relevantes += 1
            if cont < 10:
                total_relevantes_a_10 += 1
            precision = calcular_precision(total_relevantes, total_no_relevantes)
            sumatorio_precision += float(precision)
            recall = calcular_recall(total_relevantes, docs_no_detectados(qrels_informationNeed, results_informationNeed))
            archivo.write(str(precision) + "\n")
        cont += 1
    if previous_informationNeed is not None:
        imprimir_informacion(total_relevantes, total_no_relevantes, len(results_documentId), previous_informationNeed, sumatorio_precision)

def docs_no_detectados(qrels_informationNeed, results_informationNeed):
    vector_ficheros_no_detectados = {}
    resultados_qrels_informationNeed = contar_elementos(qrels_informationNeed)
    resultados_results_informationNeed = contar_elementos(results_informationNeed)
    for clave, valor1 in resultados_qrels_informationNeed.items():
        valor2 = resultados_results_informationNeed.get(clave, 0)
        vector_ficheros_no_detectados[clave] = valor1 - valor2
    return vector_ficheros_no_detectados

def contar_elementos(vector):
    contador = Counter(vector)
    return dict(contador)

def imprimir_informacion(t_rel, t_no_rel, tam, id_docum, sumatorio_precision):
    vector_no_detectados = docs_no_detectados(qrels_informationNeed, results_informationNeed)
    archivo.write("INFORMATION_NEED " + str(id_docum) + "\n")
    precision = calcular_precision(t_rel, t_no_rel)
    recall = calcular_recall(t_rel, vector_no_detectados[id_docum])
    f1 = calcular_f1(float(precision), float(recall))
    precision10 = calcular_prec10(total_relevantes_a_10)
    average_precision = calcular_average_precision(sumatorio_precision, total_relevantes)
    archivo.write("precision " + str(precision) + "\n")
    archivo.write("recall " + str(recall) + "\n")
    archivo.write("F1 " + str(f1) + "\n")
    archivo.write("prec@10 " + str(precision10) + "\n")
    archivo.write("average_precision " + str(average_precision) + "\n")
    archivo.write("average_recall\n\n")

if __name__ == "__main__":
    main()
