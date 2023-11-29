#!/bin/bash
#script que compila y ejecuta automáticamente
echo "Se está compilando y ejecutando SematicGenerator.java"
javac SemanticGenerator.java
java SemanticGenerator.java -rdf ../../../../../resultado_pruebas -docs ../../../../../recordsdc
