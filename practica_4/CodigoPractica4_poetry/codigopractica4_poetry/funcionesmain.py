import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.naive_bayes import MultinomialNB
from sklearn.metrics import accuracy_score, confusion_matrix, plot_confusion_matrix
import matplotlib.pyplot as plt

# Lee el archivo CSV
def read_csv(file_path):
    return pd.read_csv(file_path, delimiter=';')

# Entrena un clasificador Naive Bayes y devuelve la precisión y la matriz de confusión
def train_and_evaluate(data):
    X_train, X_test, y_train, y_test = train_test_split(data['descripcion'], data['subject'], test_size=0.2, random_state=42)

    vectorizer = CountVectorizer()
    X_train_vectorized = vectorizer.fit_transform(X_train)
    X_test_vectorized = vectorizer.transform(X_test)

    classifier = MultinomialNB()
    classifier.fit(X_train_vectorized, y_train)

    # Predice las etiquetas en el conjunto de prueba
    y_pred = classifier.predict(X_test_vectorized)

    # Calcula la precisión
    accuracy = accuracy_score(y_test, y_pred)
    print(f'Precisión: {accuracy}')

    # Calcula y muestra la matriz de confusión
    conf_matrix = confusion_matrix(y_test, y_pred)
    print('Matriz de Confusión:')
    print(conf_matrix)

    # Muestra la curva de confusión
    plot_confusion_matrix(classifier, X_test_vectorized, y_test, display_labels=classifier.classes_, cmap='Blues', values_format='d')
    plt.title('Matriz de Confusión')
    plt.show()

# Cambia 'tu_archivo.csv' al nombre real de tu archivo CSV
file_path = 'resumen.csv'
data = read_csv(file_path)

# Entrena y evalúa el clasificador para cada línea en el archivo CSV
for _, row in data.iterrows():
    print(f'\nProcesando línea: {row["titulo"]}')
    train_and_evaluate(pd.DataFrame([row]))

