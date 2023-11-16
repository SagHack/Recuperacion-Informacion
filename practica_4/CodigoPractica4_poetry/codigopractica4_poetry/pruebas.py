import os
import sys

def listar_nombres_archivos_en_directorio(ruta_relativa):
    # Obtener la ruta completa del script en ejecución
    ruta_script = os.path.abspath(__file__)
    
    # Obtener el directorio del script
    directorio_script = os.path.dirname(ruta_script)

    # Combinar la ruta relativa con la ruta del script para obtener la ruta completa del directorio
    ruta_completa = os.path.join(directorio_script, ruta_relativa)

    # Verificar si la ruta existe y es un directorio
    if not os.path.exists(ruta_completa):
        print(f"La ruta '{ruta_completa}' no existe.")
        return

    if not os.path.isdir(ruta_completa):
        print(f"La ruta '{ruta_completa}' no es un directorio.")
        return

    # Obtener la lista de archivos en el directorio
    archivos_en_directorio = os.listdir(ruta_completa)

    # Imprimir los nombres de los archivos
    print(f"Nombres de archivos en el directorio '{ruta_completa}':")
    for archivo in archivos_en_directorio:
        print(f"- {archivo}")

if __name__ == "__main__":
    # Obtener la ruta del directorio como argumento de línea de comandos
    if len(sys.argv) != 2:
        print("Uso: python pruebas.py <ruta_directorio>")
    else:
        ruta_directorio = sys.argv[1]
        listar_nombres_archivos_en_directorio(ruta_directorio)
