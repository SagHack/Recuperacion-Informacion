import matplotlib.pyplot as plt
# Nombre del archivo de entrada
# archivo = "p1.txt"

# Variables para almacenar la información
informacion = {}
need_actual = None
seccion_actual = None

# # Abrir el archivo y leer línea por línea
# with open(archivo, 'r') as file:
#     for linea in file:
#         # Dividir la línea en palabras
#         palabras = linea.split()
#         if not palabras:
#             continue

#         # Comprobar si la línea contiene "INFORMATION_NEED"
#         if palabras[0] == "INFORMATION_NEED":
#             need_actual = int(palabras[1])
#             informacion[need_actual] = {}
#             seccion_actual = None
#         else:
#             # Comprobar si la línea contiene una sección válida
#             if palabras[0] in {"precision", "recall", "F1", "prec@10", "average_precision"}:
#                 seccion_actual = palabras[0]
#                 informacion[need_actual][seccion_actual] = float(palabras[1])
#             elif palabras[0] == "recall_precision":
#                 seccion_actual = "recall_precision"
#                 informacion[need_actual][seccion_actual] = []
#             elif palabras[0] == "interpolated_recall_precision":
#                 seccion_actual = "interpolated_recall_precision"
#                 informacion[need_actual][seccion_actual] = []
#             elif seccion_actual == "recall_precision":
#                 informacion[need_actual][seccion_actual].append((float(palabras[0]), float(palabras[1])))
#             elif seccion_actual == "interpolated_recall_precision":
#                 informacion[need_actual][seccion_actual].append((float(palabras[0]), float(palabras[1])))

# # Mostrar la información
# for need, data in informacion.items():
#     print("INFORMATION_NEED", need)
#     for seccion, valor in data.items():
#         if seccion == "recall_precision" or seccion == "interpolated_recall_precision":
#             print(seccion)
#             for par in valor:
#                 print(f"{par[0]:.3f} {par[1]:.3f}")
#         else:
#             print(f"{seccion} {valor:.3f}")

# # Calcular estadísticas totales
# total = {
#     "precision": 0,
#     "recall": 0,
#     "F1": 0,
#     "prec@10": 0,
#     "MAP": 0
# }

# total_recall_precision = []
# total_interpolated_recall_precision = []

# for need, data in informacion.items():
#     total["precision"] += data["precision"]
#     total["recall"] += data["recall"]
#     total["F1"] += data["F1"]
#     total["prec@10"] += data["prec@10"]
#     total["MAP"] += data["average_precision"]

#     total_recall_precision.extend(data["recall_precision"])
#     total_interpolated_recall_precision.extend(data["interpolated_recall_precision"])

# # Calcular promedios
# total["precision"] /= len(informacion)
# total["recall"] /= len(informacion)
# total["F1"] /= len(informacion)
# total["prec@10"] /= len(informacion)
# total["MAP"] /= len(informacion)

# # Mostrar estadísticas totales
# print("TOTAL")
# for seccion, valor in total.items():
#     if seccion == "recall_precision" or seccion == "interpolated_recall_precision":
#         continue
#     print(f"{seccion} {valor:.3f}")

# print("interpolated_recall_precision")
# for par in total_interpolated_recall_precision:
#     print(f"{par[0]:.3f} {par[1]:.3f}")
#---------------------------------------------------------------------------------------------------------------------------------------------------------
#---------------------------------------------------------------------------------------------------------------------------------------------------------
#---------------------------------------------------------------------------------------------------------------------------------------------------------
# # Datos de precisión y recall para INFORMATION_NEED 1
# precision1 = 0.300
# recall1 = 0.750

# # Datos de precisión y recall para INFORMATION_NEED 2
# precision2 = 0.400
# recall2 = 1.000

# # Datos de precisión y recall para TOTAL
# precision_total = 0.350
# recall_total = 0.875

# # Datos de recall y precisión interpolados para INFORMATION_NEED 1
# recall_precision1 = [(0.125, 1.000), (0.250, 1.000), (0.375, 0.333), (0.500, 0.364), (0.625, 0.333), (0.750, 0.300)]

# # Datos de recall y precisión interpolados para INFORMATION_NEED 2
# recall_precision2 = [(0.250, 1.000), (0.500, 0.667), (0.750, 0.333), (1.000, 0.400)]

# # Datos de recall y precisión interpolados para TOTAL
# recall_precision_total = [(0.000, 1.000), (0.100, 1.000), (0.200, 1.000), (0.300, 0.515), (0.400, 0.515), (0.500, 0.515), (0.600, 0.367), (0.700, 0.350), (0.800, 0.200), (0.900, 0.200), (1.000, 0.200)]


# # Plot de los datos de precisión y recall para INFORMATION_NEED 1
# plt.plot(recall1, precision1, marker='o', label='INFORMATION_NEED 1')

# # Plot de los datos de precisión y recall para INFORMATION_NEED 2
# plt.plot(recall2, precision2, marker='o', label='INFORMATION_NEED 2')

# # # Plot de los datos de precisión y recall para TOTAL
# # plt.plot(recall_total, precision_total, marker='o', label='TOTAL')

# # Mostrar los puntos interpolados para INFORMATION_NEED 1
# recall1_interp, precision1_interp = zip(*recall_precision1)
# plt.plot(recall1_interp, precision1_interp, linestyle='--', marker='x', label='Interpolado INFORMATION_NEED 1')

# # Mostrar los puntos interpolados para INFORMATION_NEED 2
# recall2_interp, precision2_interp = zip(*recall_precision2)
# plt.plot(recall2_interp, precision2_interp, linestyle='--', marker='x', label='Interpolado INFORMATION_NEED 2')

# # Mostrar los puntos interpolados para TOTAL
# recall_total_interp, precision_total_interp = zip(*recall_precision_total)
# plt.plot(recall_total_interp, precision_total_interp, linestyle='--', marker='x', label='Interpolado TOTAL')

# # Configurar la gráfica

# plt.title('Curva Precision-Recall')

# plt.grid(True)

# Función que abre el fichero de resultados para leer los datos
def datosGrafica(outfilename):
    precision = 0
    recall = 0
    F1 = 0
    prec10 = 0
    MAP = 0
    recall_precision = []
    interpolated_recall_precision = []
    # Abrir el archivo y leer línea por línea
    total = False
    with open(outfilename, 'r') as file:
        for linea in file:
            # Dividir la línea en palabras
            palabras = linea.split()
            if not palabras:
                continue

            # Comprobar si la línea contiene "INFORMATION_NEED"
            if palabras[0] == "INFORMATION_NEED":
                need_actual = int(palabras[1])
                informacion[need_actual] = {}
                seccion_actual = None
                print("INFORMATION_NEED", need_actual)
            
            elif palabras[0] == "TOTAL":
                total = True
                print("TOTAL")
            else:
                if palabras[0] == "precision":      # obtener precision
                    precision = float(palabras[1])
                    print("precision", precision)
                
                elif palabras[0] == "recall":       # obtener recall
                    recall = float(palabras[1])
                    print("recall", recall)
                # #Comprobar si la línea contiene una sección válida
                # if palabras[0] in {"precision", "recall", "F1", "prec@10", "average_precision"}:
                #     seccion_actual = palabras[0]
                #     informacion[need_actual][seccion_actual] = float(palabras[1])
                # print("palabras0", palabras[0])
    
                    
                # elif palabras[0] == "recall_precision":
                #     seccion_actual = "recall_precision"
                #     informacion[need_actual][seccion_actual] = []
                # elif palabras[0] == "interpolated_recall_precision":
                #     seccion_actual = "interpolated_recall_precision"
                #     informacion[need_actual][seccion_actual] = []
                # elif seccion_actual == "recall_precision":
                #     informacion[need_actual][seccion_actual].append((float(palabras[0]), float(palabras[1])))
                # elif seccion_actual == "interpolated_recall_precision":
                #     informacion[need_actual][seccion_actual].append((float(palabras[0]), float(palabras[1])))



def generarGrafica(outfilename):
    # plt.figure(figsize=(8, 6))
    plt.xlabel('Recall')
    plt.ylabel('Precision')
    plt.legend()

    #llama a la función que se encarga de leer el fichero de resultados
    datosGrafica(outfilename)

    # Mostrar la gráfica
   # plt.show()

