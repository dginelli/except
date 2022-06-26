package it.unimib.validator;

import it.unimib.model.RepairTarget;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.diff.*;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.support.JavaOutputProcessor;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PatchInfoWriter {

    private final String outputDirectory;

    public PatchInfoWriter(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void writePatchDetails(RepairTarget repairTarget, String failingTestClass, String failingTestMethod) {
        try {
            FileWriter patchDetailsFileWriter = new FileWriter(outputDirectory + File.separator + "patch-details.txt", true);
            String patchDetails = "################################################################" +
                    System.lineSeparator() + System.lineSeparator() +
                    "Repair Target used for generating the patch related to the failing test:" +
                    System.lineSeparator() + System.lineSeparator() +
                    failingTestClass + " " + failingTestMethod +
                    System.lineSeparator() + System.lineSeparator() +
                    repairTarget +
                    System.lineSeparator() + System.lineSeparator();
            patchDetailsFileWriter.write(patchDetails);
            patchDetailsFileWriter.flush();
            patchDetailsFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writePatchedJavaFile(CtClass<?> ctClass, Launcher launcher) {
        JavaOutputProcessor javaOutputProcessor = new JavaOutputProcessor(launcher.createPrettyPrinter());
        javaOutputProcessor.setFactory(launcher.getFactory());
        javaOutputProcessor.getEnvironment().setSourceOutputDirectory(
                new File(outputDirectory + File.separator + "patch"));
        javaOutputProcessor.createJavaFile(ctClass);
    }

    public void writePatchDiff(File originalFile, File patchedFile) {

        OutputStream out = new ByteArrayOutputStream();
        try {
            RawText rawText1 = new RawText(originalFile);
            RawText rawText2 = new RawText(patchedFile);
            EditList diffList = new EditList();
            diffList.addAll(new HistogramDiff().diff(RawTextComparator.DEFAULT, rawText1, rawText2));
            new DiffFormatter(out).format(diffList, rawText1, rawText2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File file = new File(outputDirectory + File.separator +
                    "patch-diff" + File.separator);

            FileUtils.forceMkdir(file);
            FileWriter diffFileWriter =
                    new FileWriter( file + File.separator + originalFile.getName(), true);
            diffFileWriter.write(out.toString());
            diffFileWriter.flush();
            diffFileWriter.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}
