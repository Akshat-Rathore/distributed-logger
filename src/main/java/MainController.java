import java.io.*;
import java.util.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

public class MainController {
    private static final String CONFIG_FILE = ".config";

    public static void main(String[] args) {
        String username = getUsername();
        System.out.println("MainController started in background.");
        redirectSystemOut(".logs");
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaLogger kafkaLogger = new KafkaLogger(props, username);
        FileWatcher watcher = new FileWatcher(".dlogger");

        // Setup a scheduled task to check for new logs
        new Thread(() -> {
            // KafkaLogger kafkaLogger = new KafkaLogger(props, username);
            // FileWatcher watcher = new FileWatcher(".dlogger");

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Map<String, String> newLogs = watcher.getNewLogs();
                    kafkaLogger.logToKafka(newLogs);
                }
            }, 0, 5000); // Check every 5 seconds

            // Shutdown hook inside the thread
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                kafkaLogger.close();
                timer.cancel();
            }));

        }).start();


    }

    private static void redirectSystemOut(String logFileName) {
        try {
            PrintStream fileOut = new PrintStream(new FileOutputStream(logFileName, true));
            System.setOut(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String getUsername() {
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                return reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (Scanner scanner = new Scanner(System.in);
             BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            writer.write(username);
            return username;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Return null or handle accordingly if unable to read/write the username.
    }
}
