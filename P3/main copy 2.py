import sys
import pandas as pd

# Verificar que se proporcionen suficientes argumentos
if len(sys.argv) != 7:
    print("Uso: python script.py -qrels <qrelsFileName> -results <resultsFileName> -output <outputFileName>")
    sys.exit(1)

# Inicializar variables 
qrelsFileName = None
resultsFileName = None
outputFileName = None

# Iterar sobre los argumentos
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


# Leer el archivo en un DataFrame sin nombres de columna
df_qrels = pd.read_csv(qrelsFileName, sep='\t', header=None)
df_results = pd.read_csv(resultsFileName, sep='\t', header=None)

# Acceder a columnas por índice numérico
qrels_inforamtionNeed = df_qrels[0]
qrels_documentId = df_qrels[1]
qrels_relevancy = df_qrels[2]

results_informationNeed = df_results[0]
results_documentId = df_results[1]

def calcular_true_positives(qrels_informationNeed, qrels_relevancy, results_informationNeed, results_documentId):
    true_positives = 0

    # Crear un diccionario para mapear DOCUMENT_ID a RELEVANCY en los resultados
    results_dict = {}
    for i in range(len(results_informationNeed)):
        if results_informationNeed[i] not in results_dict:
            results_dict[results_informationNeed[i]] = {}
        results_dict[results_informationNeed[i]][results_documentId[i]] = qrels_relevancy[i]

    # Iterar a través de INFORMATION_NEED en qrels
    for i in range(len(qrels_informationNeed)):
        info_need = qrels_informationNeed[i]
        if info_need in results_dict:
            # Comprobar si el DOCUMENT_ID está presente en los resultados y es relevante (RELEVANCY = 1)
            if qrels_documentId[i] in results_dict[info_need] and results_dict[info_need][qrels_documentId[i]] == 1:
                true_positives += 1

    return true_positives

def calcular_true_negatives(qrels_informationNeed, qrels_relevancy, results_informationNeed, results_documentId):
    true_negatives = 0

    # Crear un conjunto de pares (INFORMATION_NEED, DOCUMENT_ID) de qrels para una búsqueda rápida
    qrels_set = set(zip(qrels_informationNeed, qrels_documentId))

    # Iterar a través de los resultados en results
    for i in range(len(results_informationNeed)):
        info_need = results_informationNeed[i]
        document_id = results_documentId[i]
        if (info_need, document_id) not in qrels_set:
            # Si el documento no está en qrels, y la RELEVANCY es 0, es un True Negative
            true_negatives += 1

    return true_negatives

def buscar_documento(informationNeed,id_documento):
    tam_qrels = int(len(qrels_inforamtionNeed))
    for i in range(tam_qrels):
        if(id_documento == qrels_documentId[i] and informationNeed == qrels_inforamtionNeed[i]):
            return i
    return -1

#funcion auxiliar que calcula la precision con formato de 3 decimales
def calcular_precision(total_relevantes,total_no_relevantes):
    return "{:.3f}".format(total_relevantes / (total_relevantes + total_no_relevantes))

#funcion auxiliar que calcula el recall con formato de 3 decimales
def calcular_recall(total_relevantes, total_no_relevantes, tam):
    recall = total_relevantes / tam
    #recall = total_relevantes / cont
    return "{:.3f}".format(recall)

#funcion auxiliar que calcula el f1 con formato de 3 decimales
def calcular_f1(precision,recall):
    return "{:.3f}".format(2*((precision*recall)/(precision+recall)))

#funcion auxiliar que calcula el prec@10 con formato de 3 decimales
def calcular_prec10(total_relevantes_a_10):
    return "{:.3f}".format(total_relevantes_a_10/10)

#funcion auxiliar que calcula el average_precision
def calcular_average_precision(precision, recall):
    return 0


#funcion auxiliar que cuenta el número de elementos falsos negativos
def encontrar_falsos_negativos(qrels_relevancy, results_informationNeed, results_documentId):
    falsos_negativos = 0
    for i in range(len(results_documentId)):
        index = buscar_documento(results_informationNeed[i], results_documentId[i])
        if qrels_relevancy[index] == 1: 
            falsos_negativos += 1

    return falsos_negativos

def imprimir_informacion(t_rel, t_no_rel, tam):
    archivo.write("---------------------\n")
    
    true_positives = calcular_true_positives(qrels_inforamtionNeed, qrels_relevancy, results_informationNeed, results_documentId)
    archivo.write(f"True Positives: {true_positives}\n")
    true_negatives = calcular_true_negatives(qrels_inforamtionNeed, qrels_relevancy, results_informationNeed, results_documentId)
    archivo.write(f"True Negatives: {true_negatives}\n")
    
    archivo.write("---------------------\n")
    
    
    archivo.write("INFORMATION_NEED " + str(prev_value) + "\n")

    precision = calcular_precision(t_rel,t_no_rel)
    archivo.write("precision "+ str(precision) + "\n")

    #recall = calcular_recall(t_rel, t_no_rel, tam)
    #recall = 0.75
    recall = total_relevantes / tam
    archivo.write("recall "+ str(recall) + "\n")

    archivo.write("F1 "+ str(calcular_f1(float(precision),float(recall))) + "\n")

    precision10 = calcular_prec10(total_relevantes_a_10)
    archivo.write("prec@10 "+ str(precision10) + "\n")

    average_precision = calcular_average_precision(precision, recall)
    archivo.write("average_precision " + str(average_precision) + "\n")


    archivo.write("recall_precision\n")

    archivo.write("interpolated_recall_precision\n")
 
    archivo.write("\n")


# ejecutar funcion buscar_docuemnto
with open(outputFileName, "w") as archivo:
    tam = int(len(results_documentId))
    total_relevantes = 0
    total_no_relevantes = 0
    total_relevantes_a_10 = 0
    prev_value = None
    for i in range(tam):
        if results_informationNeed[i] != prev_value:
            if prev_value is not None:
                

                falsos_negativos = encontrar_falsos_negativos(qrels_relevancy, results_informationNeed, results_documentId)
                imprimir_informacion(total_relevantes, total_no_relevantes, tam)
                print("Total elementos iguales:", count)
                print("total relevantes: ", total_relevantes)
                print("total no relevantes: ", total_no_relevantes)
    
                
            print("informationNeed: ", results_informationNeed[i], " documentId: ", results_documentId[i], " relevancy: ", qrels_relevancy[i])
            prev_value = results_informationNeed[i]
            count = 1
            total_relevantes = 0
            total_no_relevantes = 0
            total_relevantes_a_10 = 0
        else:
            count += 1

        index = buscar_documento(results_informationNeed[i],results_documentId[i])
        print("informationNeed: ",results_informationNeed[i]," documentId: ",results_documentId[i]," relevancy: ",qrels_relevancy[index])

        
        
        if qrels_relevancy[index] ==0:
            #print("es no relevante")  #archivo.write("es no relevante\n")
            total_no_relevantes += 1
        else:
            #print("es relevante")     #archivo.write("es relevante\n")
            total_relevantes += 1
            if i < 10:
                total_relevantes_a_10 += 1
        
        if i == tam - 1:
            imprimir_informacion(total_relevantes, total_no_relevantes,falsos_negativos)
            print("total relevantes: ", total_relevantes)
            print("total no relevantes: ", total_no_relevantes)
            print("Total elementos iguales:", count)
        # si no coinciden o es el ultimo elemento de la lista
    
archivo.close()
# print("total recuperados: ",tam)
# print("total relevantes: ",total_relevantes)
# print("total no relevantes: ",total_no_relevantes)



# Construir y ejecutar el comando
# comando = f"java Evaluation -qrels {qrelsFileName} -results {resultsFileName} -output {outputFileName}"
# print(f"Ejecutando comando: {comando}")

# Aquí puedes usar subprocess para ejecutar el comando si es necesario