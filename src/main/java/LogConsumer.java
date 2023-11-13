
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.time.Duration;
import java.util.*;
import java.io.*;

public class LogConsumer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter classname: ");
        String classname = scanner.nextLine();
        System.out.print("Enter the number of messages to consume: ");
        int numMessages = scanner.nextInt();
        scanner.nextLine();  // Consume the newline

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "log-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        String topicName = username + "_" + classname;

        consumer.subscribe(Collections.singletonList(topicName));

        try {
            File logFile = new File("dl_" + username + "_" + classname + ".log");
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));

            int messageCount = 0;
            while (messageCount < numMessages) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    writer.write(record.value());
                    writer.newLine();
                    if (++messageCount >= numMessages) {
                        break;
                    }
                }
            }
            writer.close();

            System.out.print("Do you want to continue? (y/n): ");
            String answer = scanner.nextLine();
            if (!"y".equalsIgnoreCase(answer)) {
                consumer.close();
                scanner.close();
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }
}
