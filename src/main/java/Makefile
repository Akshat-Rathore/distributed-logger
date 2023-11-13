# Path to Javassist JAR
CLASS_PATH=/home/maokami/Documents/dl/logger/src/main/resources/jars/javassist-3.29.0-GA.jar:/home/maokami/Documents/dl/logger/src/main/resources/jars/spring-kafka-3.0.6.jar:/home/maokami/Documents/dl/logger/src/main/resources/jars/kafka_2.13-3.6.0.jar:/home/maokami/Documents/dl/logger/src/main/resources/jars/kafka-clients-3.6.0.jar

# Directory to store compiled class files
OUTPUT_DIR=bin

# Source files directory
SOURCE_DIR=.

# Targets
all: compile jar run

compile:
	@echo "Compiling JavassistAgent..."
	@mkdir -p $(OUTPUT_DIR)
	@javac -classpath $(CLASS_PATH) -d $(OUTPUT_DIR) $(SOURCE_DIR)/JavassistAgent.java

jar:
	@echo "Creating agent.jar..."
	@jar cvfm agent.jar MANIFEST.MF -C $(OUTPUT_DIR) .

run:
	@echo "Running Main.java with agent..."
	@java -javaagent:agent.jar -classpath $(CLASS_PATH) $(SOURCE_DIR)/Main.java

clean:
	@echo "Cleaning up..."
	@rm -rf $(OUTPUT_DIR) agent.jar

.PHONY: all compile jar run clean
