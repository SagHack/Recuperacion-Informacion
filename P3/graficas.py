import matplotlib.pyplot as plt
import numpy as np

# Definir diccionarios para almacenar los datos
data = {}
current_info_need = None

def leerDatos(output_file):
    # Abre el archivo y lee los datos
    with open(output_file, 'r') as file:
        for line in file:
            line = line.strip()
            if line.startswith('INFORMATION_NEED'):
                info_need = int(line.split()[-1])
                current_info_need = info_need
                data[info_need] = {}
            
            elif line.startswith('TOTAL'):
                current_info_need = line
                data[line] = {}
                
            elif line.startswith('interpolated_recall_precision'):
                data[current_info_need]['interpolated_recall_precision'] = []
                for _ in range(11):
                    line = next(file).strip()
                    x, y = map(float, line.split())
                    data[current_info_need]['interpolated_recall_precision'].append((x, y))

    # Crear una gráfica para cada INFORMATION_NEED
    for info_need, values in data.items():
        recall_precision = values['interpolated_recall_precision']
        x, y = zip(*recall_precision)
        
        if info_need != 'TOTAL':
            label = 'informacion need {}'.format(info_need)
        
        else:
            label = 'Total'
        
        plt.plot(x, y, label=label)

  

def generarGrafica(output_file):
    # Leer los datos
    leerDatos(output_file)
    # Configurar la gráfica
    plt.xlabel('exhaustividad (recall)')
    plt.ylabel('precision')
    plt.title('Curvas Recall-Precision')
    plt.legend()
    plt.grid(True)
    plt.show()
