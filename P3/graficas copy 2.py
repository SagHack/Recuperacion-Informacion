import matplotlib.pyplot as plt

# Variables para almacenar los datos
information_needs = []
precision_values = []
recall_values = []
f1_values = []
prec_at_10_values = []
avg_precision_values = []

def datosGrafica(outfilename):
    # Lee el archivo de entrada
    with open(outfilename, 'r') as file:

        information_need = None
        for line in file:
            palabras = line.split()
            if not palabras:
                continue

            if palabras[0] == "INFORMATION_NEED":
                information_need = int(palabras[1])
                information_needs.append(information_need)
            # parts = line.strip().split()
            # print(line)
            # if not parts:
            #     continue  # Saltar líneas vacías o sin datos
            # if parts[0] == 'INFORMATION_NEED':
            #     information_need = int(parts[1])
            #     information_needs.append(information_need)
            # elif parts[0] == 'precision':
            #     precision_values.append(float(parts[1]))
            # elif parts[0] == 'recall':
            #     recall_values.append(float(parts[1]))
            # elif parts[0] == 'F1':
            #     f1_values.append(float(parts[1]))
            # elif parts[0] == 'prec@10':
            #     prec_at_10_values.append(float(parts[1]))
            # elif parts[0] == 'average_precision':
            #     avg_precision_values.append(float(parts[1]))


def generarGrafica(outfilename):
    # Crear una figura y ejes
    datosGrafica(outfilename)
    fig, ax = plt.subplots()

    # Graficar los datos
    ax.plot(information_needs, precision_values, label='Precision')
    ax.plot(information_needs, recall_values, label='Recall')
    ax.plot(information_needs, f1_values, label='F1')
    ax.plot(information_needs, prec_at_10_values, label='Prec@10')
    ax.plot(information_needs, avg_precision_values, label='Average Precision')

    # Configurar la leyenda y etiquetas de ejes
    ax.legend()
    ax.set_xlabel('Information Need')
    ax.set_ylabel('Valor')
    ax.set_title('Gráfico de Métricas por Information Need')

    # Mostrar el gráfico
    #plt.show()


# def generarGrafica(outfilename):
#     # plt.figure(figsize=(8, 6))
#     plt.xlabel('Recall')
#     plt.ylabel('Precision')
#     plt.legend()

#     #llama a la función que se encarga de leer el fichero de resultados
#     datosGrafica(outfilename)