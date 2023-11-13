import argparse
import sys
import os
import csv
import xml.etree.ElementTree as ET


# Función que verifica que los parámetros de entrada son correctos y devuelve el nombre de los 
# directorios donde se encuentran los ficheros de Zaguan y donde se guardarán los resultados
def parse_args():
    # tiene que cumplir el siguiente formato:
    # python clasificadorTexto.py -dir <zaguanDir> -output <resultsDir>
    if len(sys.argv) != 5:
        print("Uso: python clasificadorTexto.py -dir <zaguanDir> -output <resultsDir>")
        sys.exit(1)
    
    zaguanDir = None    # Directorio donde se encuentran los ficheros de Zaguan
    resultsDir = None   # Directorio donde se guardarán los resultados

    i = 1
    while i < len(sys.argv):
        if sys.argv[i] == '-dir':
            zaguanDir = sys.argv[i + 1]     # Guardamos el nombre del directorio donde se encuentran los ficheros de Zaguan
        elif sys.argv[i] == '-output':
            resultsDir = sys.argv[i + 1]    # Guardamos el nombre del directorio donde se guardarán los resultados
        i += 2

    # Verificar que se proporcionen todos los argumentos necesarios
    if None in (zaguanDir, resultsDir):
        print("Faltan argumentos. Uso: python clasificadorTexto.py -dir <zaguanDir> -output <resultsDir>")
        sys.exit(1)
    
    return zaguanDir, resultsDir


# Función que hace un ls y muestra por pantalla los ficheros dentro de la carpeta.
def listar_archivos_en_carpeta(carpeta):
    try:
        # Convertir la ruta relativa a absoluta si es necesario
        carpeta = os.path.abspath(carpeta)

        # Listar archivos en la carpeta
        archivos = os.listdir(carpeta)

        # Imprimir los nombres de los archivos
        for archivo in archivos:
            print(archivo)

    except OSError as e:
        print(f"Error al listar archivos en la carpeta {carpeta}: {e}")

# Función que dado el path de un directorio y un nombre de un fichero, genera un csv con dicho nombre
# con el titulo y descripcion de los ficheros del directorio pasado como argumento
def generar_csv(zaguanDir, resultsDir):
    # Construye la ruta completa del archivo CSV en el mismo directorio que el script
    ruta_csv = os.path.join(os.path.dirname(__file__), resultsDir)

    # Abre el archivo CSV en modo escritura
    with open(ruta_csv, mode="w", newline="", encoding="utf-8") as archivo_csv:
        escritor_csv = csv.writer(archivo_csv, delimiter=";")                       # escribe en el fichero csv con el delimitador ";"
        escritor_csv.writerow(["archivo_xml", "titulo", "descripcion", "subject"])  # escribe la cabecera del csv

        for archivo_xml in os.listdir(zaguanDir):   # Para cada fichero del directorio especificado
            
            if archivo_xml.endswith(".xml"):        # si el archivo es un xml
                ruta_xml = os.path.join(zaguanDir, archivo_xml)
                tree = ET.parse(ruta_xml)
                root = tree.getroot()
                ns = {'dc': 'http://purl.org/dc/elements/1.1/'}
                
                # Obtiene el título
                titulo_element = root.find(".//dc:title", namespaces=ns)
                titulo = titulo_element.text.replace("\n", " ") if titulo_element is not None and titulo_element.text is not None else ""

                # Obtiene la descripción
                descripcion_element = root.find(".//dc:description", namespaces=ns)
                descripcion = descripcion_element.text.replace("\n", " ") if descripcion_element is not None and descripcion_element.text is not None else ""
                
                # Obtiene los sujetos (o palabras clave) (puede haber varios)
                subjects = [subject.text.replace("\n", " ") for subject in root.findall(".//dc:subject", namespaces=ns)]

                # Escribe en el archivo CSV (cada palabra clave está separada entre ellas por ",")
                escritor_csv.writerow([archivo_xml, titulo, descripcion, ", ".join(subjects)])


# Función principal del main
def main():
    # Se comprueba que el número de argumentos sea correcto y obtenemos nombre de ficheros
    zaguanDir, resultsDir = parse_args()
    resultsDir = resultsDir + ".csv"
    generar_csv(zaguanDir, resultsDir)

# -------------------------------------------------------------------------------------- Programa principal
main()