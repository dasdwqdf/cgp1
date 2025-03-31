# Nome do arquivo JAR gerado pelo Maven
JAR_FILE=target/cgp1-1.0-SNAPSHOT.jar

# Regra para compilar e empacotar o projeto Maven
build:
	mvn clean package

# Regra para rodar o JAR gerado
run: build
	java -jar $(JAR_FILE)

# Regra para limpar o projeto
clean:
	mvn clean
