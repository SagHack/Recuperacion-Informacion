#!/bin/bash
#script que compila y ejecuta automáticamente
#javac SemanticGenerator.java
echo "Se está compilando y ejecutando SematicGenerator.java"
java SemanticGenerator.java -rdf ../../../../../resultado_pruebas -docs ../../../../../recordsdc
