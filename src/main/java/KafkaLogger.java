import org.apache.kafka.clients.producer.*;

import java.util.*;

public class KafkaLogger {
    private final KafkaProducer<String, String> producer;
    private final String username;

    public KafkaLogger(Properties kafkaProps, String username) {
        this.producer = new KafkaProducer<>(kafkaProps);
        this.username = username;
    }

    public void logToKafka(Map<String, String> logs) {
        logs.forEach((filename, content) -> {
            String topicName = username + "_" + filename;
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, content);
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    exception.printStackTrace();
                } else {
                    System.out.println("Log sent to topic: " + metadata.topic());
                    System.out.println(content+"\n");
                }
            });
        });
    }

    public void close() {
        producer.close();
    }
}
