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
qrels_informationNeed = df_qrels[0]
qrels_documentId = df_qrels[1]
qrels_relevancy = df_qrels[2]

results_informationNeed = df_results[0]
results_documentId = df_results[1]

def buscar_documento(informationNeed,id_documento):
    tam_qrels = int(len(qrels_informationNeed))
    for i in range(tam_qrels):
        if(id_documento == qrels_documentId[i] and informationNeed == qrels_informationNeed[i]):
            return i
    return -1

#funcion auxiliar que calcula la precision con formato de 3 decimales
def calcular_precision(total_relevantes, total_no_relevantes):
    return "{:.3f}".format(total_relevantes / (total_relevantes + total_no_relevantes))

#función auxiliar para calcular el recall
def calcular_recall(total_relevantes, no_detectados):
    return "{:.3f}".format(total_relevantes / (total_relevantes + no_detectados))
    return 0

#funcion auxiliar que calcula el f1 con formato de 3 decimales
def calcular_f1(precision,recall):
    return "{:.3f}".format(2*((precision*recall)/(precision+recall)))

#funcion auxiliar que calcula el prec@10 con formato de 3 decimales
def calcular_prec10(total_relevantes_a_10):
    return "{:.3f}".format(total_relevantes_a_10/10)

#funcion auxiliar que calcula el average precision
def calcular_average_precision(total, n):
    return "{:.3f}".format(total/n)

#función para calcular el average recall
def calcular_average_recall(total, n):
    return "{:.3f}".format(total/n)

from collections import Counter

#función para contar las ocurrencias de los elementos de un vector
def contar_elementos(vector):
    contador = Counter(vector)
    return dict(contador)

resultados_qrels_informationNeed = contar_elementos(qrels_informationNeed)
resultados_results_informationNeed = contar_elementos(results_informationNeed)

def escribir_resultados_en_archivo(outputFileName, results_documentId):
    for elemento, cantidad in resultados_qrels_informationNeed.items():
        #archivo.write(f"Elemento {elemento}: {cantidad} veces\n")
        pass

    for elemento, cantidad in resultados_results_informationNeed.items():
        #archivo.write(f"Elemento {elemento}: {cantidad} veces\n") 
        pass

    return resultados_qrels_informationNeed, resultados_results_informationNeed

def docs_no_detectados():
    resultados_qrels_informationNeed, resultados_results_informationNeed = escribir_resultados_en_archivo(outputFileName, results_documentId)
    vector_ficheros_no_detectados = {}
    for clave, valor1 in resultados_qrels_informationNeed.items():
        valor2 = resultados_results_informationNeed.get(clave, 0)
        vector_ficheros_no_detectados[clave] = valor1 - valor2
    return vector_ficheros_no_detectados


def imprimir_informacion(t_rel, t_no_rel, tam, id_docum, sumatorio_precision):
    vector_no_detectados = docs_no_detectados()
    archivo.write("INFORMATION_NEED "+ str(id_docum)+"\n")
    
    precision = calcular_precision(t_rel, t_no_rel)
    recall = calcular_recall(t_rel, vector_no_detectados[id_docum])
    f1 = calcular_f1(float(precision),float(recall))
    precision10 = calcular_prec10(total_relevantes_a_10)
    average_precision = calcular_average_precision(sumatorio_precision, total_relevantes)
    

    archivo.write("precision " + str(precision) + "\n")
    archivo.write("recall " + str(recall) + "\n")
    archivo.write("F1 "+ str(f1) + "\n")
    archivo.write("prec@10 "+ str(precision10) + "\n")
    archivo.write("average_precision " + str(average_precision) + "\n")
    archivo.write("average_recall\n")
    
    

    

    
    


# ejecutar funcion buscar_docuemnto
with open(outputFileName, "w") as archivo:
    tam = int(len(results_documentId))
    total_relevantes = 0
    total_no_relevantes = 0
    total_relevantes_a_10 = 0
    cont = 0
    sumatorio_precision = 0
    sumatorio_recall = 0
    vector_sumatorio_precision = {}
    vector_sumatorio_precision_NO = {}
    vector_sumatorio_recall = {}
    indice = 0
    indice_2 = 0
      
#     # archivo.write("--------\n")
#     # archivo.write(str(resultados_qrels_informationNeed) + "\n")
#     # archivo.write(str(resultados_results_informationNeed) + "\n")
#     # archivo.write("--------\n")

    

    previous_informationNeed = None  # Variable para realizar un seguimiento del informationNeed anterior
    for i in range(tam):
        if results_informationNeed[i] != previous_informationNeed:
            if previous_informationNeed is not None:
                imprimir_informacion(total_relevantes, total_no_relevantes, tam, previous_informationNeed, sumatorio_precision)
                vector_no_detectados = docs_no_detectados()
                for k in range(total_relevantes):
                    v = total_relevantes + vector_no_detectados[previous_informationNeed] - (k+1 )
                    recall = calcular_recall(k+1, v)
                    #archivo.write("--")
                    #calculo = (k+1) / (k+1 + v)
                    #archivo.write(str(calculo))
                    # archivo.write("--> " + str(k+1) + " " +str(v) +"\n")
                    #archivo.write("\t {:.3f} \n".format(vector_sumatorio_precision[k]))
                    archivo.write("{:.3f} {:.3f}".format(float(recall), float(vector_sumatorio_precision[k])) + "\n")

                    
                # archivo.write(str(total_relevantes + vector_no_detectados[previous_informationNeed]))
                archivo.write("interpolated_recall_precision\n")
                #for k in range(total_no_relevantes):
                    #v = total_relevantes + vector_no_detectados[previous_informationNeed] - (k+1 )
                    #recall = calcular_recall(k+1, v)
                    #archivo.write("--")
                    #calculo = (k+1) / (k+1 + v)
                    #archivo.write(str(calculo))
                    # archivo.write("--> " + str(k+1) + " " +str(v) +"\n")
                    #archivo.write("\t {:.3f} \n".format(vector_sumatorio_precision[k]))
                    #archivo.write("{:.3f} {:.3f}".format(float(recall), float(vector_sumatorio_precision[k])) + "\n")
                #    archivo.write(str(vector_sumatorio_precision_NO[k]) + "\n")
                    
                archivo.write("\n")

                total_no_relevantes = 0
                total_relevantes = 0
                total_relevantes_a_10 = 0
                cont = 0
                sumatorio_precision = 0
                indice = 0
                indice_2 = 0

                #archivo.write("---------------" + str(previous_informationNeed) + "\n")
            previous_informationNeed = results_informationNeed[i]

        index = buscar_documento(results_informationNeed[i], results_documentId[i])
        #archivo.write("informationNeed: " + str(results_informationNeed[i]) + " documentId: " + str(results_documentId[i]) + " relevancy: " + str(qrels_relevancy[index]) + "\n")
    
        if qrels_relevancy[index] ==0:
            #print("es no relevante")  #archivo.write("es no relevante\n")
            total_no_relevantes += 1
            
        else:
            #print("es relevante")     #archivo.write("es relevante\n")
            total_relevantes += 1
            
            if cont < 10:
                total_relevantes_a_10 += 1

            precision = calcular_precision(total_relevantes, total_no_relevantes)
            sumatorio_precision += float(precision)
            vector_sumatorio_precision[indice] = float(precision)
            #recall = calcular_recall(total_relevantes, )
            #vector_sumatorio_recall[indice] = float(recall)
            indice += 1


            vector_no_detectados = docs_no_detectados()
            print(total_relevantes, vector_no_detectados[previous_informationNeed], " -> ", total_relevantes + vector_no_detectados[previous_informationNeed])
            
            
            #div_recall = ((total_relevantes + vector_no_detectados[previous_informationNeed]) - cont)
            #recall = calcular_recall(total_relevantes, div_recall)#total_relevantes, vector_no_detectados[previous_informationNeed])
            #archivo.write(str(recall) + " " +str(precision) +"\n")
        
        
        cont += 1
    # Agregar una línea de separación al final si es necesario
    if previous_informationNeed is not None:
        imprimir_informacion(total_relevantes, total_no_relevantes, tam, previous_informationNeed, sumatorio_precision)
        vector_no_detectados = docs_no_detectados()
        for k in range(total_relevantes):
            v = total_relevantes + vector_no_detectados[previous_informationNeed] - (k+1 )
            recall = calcular_recall(k+1, v)
            #archivo.write("--")
            #calculo = (k+1) / (k+1 + v)
            #archivo.write(str(calculo))
            # archivo.write("--> " + str(k+1) + " " +str(v) +"\n")
            #archivo.write("\t {:.3f} \n".format(vector_sumatorio_precision[k]))
            archivo.write("{:.3f} {:.3f}".format(float(recall), float(vector_sumatorio_precision[k])) + "\n")

        #archivo.write("---------------" + str(previous_informationNeed) + "\n")
        archivo.write("interpolated_recall_precision\n")
        total_relevantes = 0
        total_no_relevantes = 0
        total_relevantes_a_10 = 0
        cont = 0
        indice = 0
        indice_2 = 0
  
archivo.close()
# # print("total recuperados: ",tam)
# # print("total relevantes: ",total_relevantes)
# # print("total no relevantes: ",total_no_relevantes)



# # Construir y ejecutar el comando
# # comando = f"java Evaluation -qrels {qrelsFileName} -results {resultsFileName} -output {outputFileName}"
# # print(f"Ejecutando comando: {comando}")

# # Aquí puedes usar subprocess para ejecutar el comando si es necesario