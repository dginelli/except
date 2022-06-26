package it.unimib.compiler;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompilerUtil {

    private static final Logger logger = Logger.getLogger(CompilerUtil.class);

    private CompilerUtil() {}

    public static void compile(String sourceCodePath, String outputFolderPath) {
        ProcessBuilder findJavaFilesProcessBuilder = new ProcessBuilder("find", sourceCodePath, "-name", "*.java");

        String javaFilesCommandOutput = null;
        List<String> stringCommand = new ArrayList<>();

        try {
            Process process = findJavaFilesProcessBuilder.start();
            javaFilesCommandOutput = new String(process.getInputStream().readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (javaFilesCommandOutput != null) {
            String[] javaFiles = javaFilesCommandOutput.split("\n");
            stringCommand.add("javac");
            stringCommand.add("-d");
            stringCommand.add(outputFolderPath);
            stringCommand.addAll(Arrays.asList(javaFiles));

            ProcessBuilder javacProcessBuilder = new ProcessBuilder(stringCommand);

            try {
                Process process = javacProcessBuilder.start();
                logger.debug(new String(process.getErrorStream().readAllBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.error("Cannot compile the program variant because the Java files were not found");
        }
    }
}
