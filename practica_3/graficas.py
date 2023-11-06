import matplotlib.pyplot as plt
import sys

# Lee y devuelve interpolated_recall e interpolated_precision del fichero especificado
def leerDatos(output_file):
    interpolated_recall = []
    interpolated_precision = []

    isTotal = False
    # Abre el archivo y lee los datos
    with open(output_file, 'r') as file:
        for line in file:
            line = line.strip()
            if line.startswith('TOTAL'):
                isTotal = True
        
            elif line.startswith('interpolated_recall_precision') and isTotal:
                for _ in range(11):
                    line = next(file).strip()
                    x, y = map(float, line.split())
                    interpolated_recall.append(x)
                    interpolated_precision.append(y)
    
    return interpolated_recall,interpolated_precision


# Añade a la gráfica la información de interpolated_recall e interpolated_precision del fichero especificado
def generarGrafica(output_file):
    interpolated_recall,interpolated_precision = leerDatos(output_file)
    plt.plot(interpolated_recall,interpolated_precision,label=output_file)
    plt.legend()


# Realiza la configuración inicial de la gráfica
def config_grafica():
    plt.figure(figsize=(12,5))
    plt.grid(True)
    plt.xlim(0, 1)  # Set the X-axis limits to start at 0
    plt.ylim(0, 1)  # Set the Y-axis limits to start at 0
    plt.xticks([i/10 for i in range(11)])  # X-axis from 0.0 to 1.0 in 0.1 increments
    plt.yticks([i/10 for i in range(11)])  # Y-axis from 0.0 to 1.0 in 0.1 increments
    plt.xlabel('exhaustividad (recall)')
    plt.ylabel('precision')
    plt.title('Curvas Recall-Precision')


def mainGraficas():
    #comprueba que el número de argumentos es correcto
    if len(sys.argv) < 2:
        print("Uso: python graficas.py <nombre de ficheros resultado de ejecutar el main.py>")
        sys.exit(1)

    config_grafica()
    
    i = 1
    # Obtenemos los nombres de los ficheros a leer y los ploteamos en la gráfica
    while i < len(sys.argv): 
        output_file = sys.argv[i]
        generarGrafica(output_file)
        i += 1
    plt.show()  # Muestra la gráfica
    
mainGraficas()