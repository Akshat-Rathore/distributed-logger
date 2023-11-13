import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.ProtectionDomain;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

public class JavassistAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className,
                                    Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain,
                                    byte[] classfileBuffer) {
                return transformClass(classfileBuffer, className);
            }
        });
    }


private static byte[] transformClass(byte[] classBytes, String className) {
    ClassPool cp = ClassPool.getDefault();
    try {
        CtClass cc = cp.makeClass(new ByteArrayInputStream(classBytes));
        for (CtMethod method : cc.getDeclaredMethods()) {
            method.instrument(new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals("java.io.PrintStream") &&
                        (m.getMethodName().equals("println") || m.getMethodName().equals("print"))) {
                        if (m.getSignature().equals("()V")) {
                            return; // Ignore println() with no arguments.
                        }
                        int lineNumber = m.getLineNumber(); // Get the line number
                        m.replace("{" +
                                  "    String logMessage = \"Line " + lineNumber + ": \" + String.valueOf($1);" +
                                  "    JavassistAgent.logToFile(logMessage, \"" + className.replaceAll("/", ".") + "\");" +
                                  "    $_ = $proceed($$);" +
                                  "}");
                    }
                }
            });
        }
        return cc.toBytecode();
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}


    public static void logToFile(String message, String fileName) {
        try {
            Path logDir = Paths.get(".dlogger");
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }
            Path filePath = logDir.resolve(fileName);
            Files.write(filePath, (message + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
