package it.unimib.util;

import com.opencsv.CSVWriter;
import fr.spoonlabs.flacoco.api.result.FlacocoResult;
import fr.spoonlabs.flacoco.api.result.Location;
import fr.spoonlabs.flacoco.api.result.Suspiciousness;
import it.unimib.model.FailureInfo;
import it.unimib.model.RepairTarget;
import it.unimib.model.SuspiciousLocation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static it.unimib.util.Constants.EXCEPT_WORKING_DIR;

public class SBFLRankingMergingUtil {

    private static final String OUTPUT_FILENAME = "except_ranking.csv";

    public SBFLRankingMergingUtil() {}

    public static void mergeRepairTargetsWithFlacocoResults(String programName, FailureInfo failureInfo, List<RepairTarget> repairTargetList, FlacocoResult flacocoResult) {

        repairTargetList.sort(Comparator.comparingDouble(RepairTarget::getSuspiciousnessScore).reversed());
        Map<Location, Suspiciousness> sbflRanking = flacocoResult.getDefaultSuspiciousnessMap();

        for (int i = 0; i < repairTargetList.size(); i++) {
            int finalI = i;

            sbflRanking.entrySet().removeIf(e -> repairTargetList.get(finalI).getSuspiciousLocation().getClassName().equals(e.getKey().getClassName()) &&
                    repairTargetList.get(finalI).getSuspiciousLocation().getLineNumber() == e.getKey().getLineNumber());
        }

        String output = EXCEPT_WORKING_DIR + File.separator + programName + File.separator +
                failureInfo.getFailingTestClass().replace(".", "-") + "#" + failureInfo.getFailingTestMethod();

        writeResults(repairTargetList, sbflRanking, output);
    }

    public static List<RepairTarget> getRepairTargetsWithFlacocoResults(
            Map<FailureInfo, List<RepairTarget>> repairTargetForFailureMap, FlacocoResult flacocoResult) {

        List<RepairTarget> repairTargetList = new ArrayList<>();

        for (Map.Entry<FailureInfo, List<RepairTarget>> set : repairTargetForFailureMap.entrySet()) {
            repairTargetList.addAll(set.getValue());
            repairTargetList.sort(Comparator.comparingDouble(RepairTarget::getSuspiciousnessScore).reversed());
        }

        List<RepairTarget> filteredRepairTargetList = new ArrayList<>();

        // TODO Check implementation of equals() method of RepairTarget and SuspiciousLocation classes
        // You should use repairTargetList.stream().distinct().collect(Collectors.toList());

        for (int i = 0; i < repairTargetList.size(); i++) {
            boolean found = false;
            if (filteredRepairTargetList.isEmpty()) {
                filteredRepairTargetList.add(repairTargetList.get(i));
            } else {
                for (int j = 0; j < filteredRepairTargetList.size() && !found; j++) {
                    if (repairTargetList.get(i).getSuspiciousLocation().getClassName().
                            equals(filteredRepairTargetList.get(j).getSuspiciousLocation().getClassName()) &&
                            repairTargetList.get(i).getSuspiciousLocation().getLineNumber() ==
                                    filteredRepairTargetList.get(j).getSuspiciousLocation().getLineNumber()) {
                        found = true;
                    }
                }
                if (!found) {
                    filteredRepairTargetList.add(repairTargetList.get(i));
                }
            }
        }

        Map<Location, Suspiciousness> sbflRanking = flacocoResult.getDefaultSuspiciousnessMap();

        for (int i = 0; i < filteredRepairTargetList.size(); i++) {
            int finalI = i;

            sbflRanking.entrySet().removeIf(e ->
                    filteredRepairTargetList.get(finalI).getSuspiciousLocation().getClassName().equals(e.getKey().getClassName()) &&
                            filteredRepairTargetList.get(finalI).getSuspiciousLocation().getLineNumber() == e.getKey().getLineNumber());
        }

        List<RepairTarget> flacocoResults = new ArrayList<>();

        sbflRanking.forEach((k, v) -> {
            flacocoResults.add(new RepairTarget(
                    new SuspiciousLocation(k.getClassName(),
                            null, k.getLineNumber(),
                            null, null), null, v.getScore()));
        });

        filteredRepairTargetList.addAll(flacocoResults);

        return filteredRepairTargetList;
    }

    public static void writeResults(List<RepairTarget> repairTargetList,
                                    Map<Location, Suspiciousness> sbflRanking, String output) {

        if (repairTargetList != null) {

            File directory = new File(output);
            if (! directory.exists()){
                directory.mkdirs();
            }

            CSVWriter writer = null;
            try {
                writer = new CSVWriter(new FileWriter(output + File.separator + OUTPUT_FILENAME, false),
                        ';',
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.RFC4180_LINE_END);
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<String[]> finalOutput = new ArrayList<String[]>();
            finalOutput.add(new String[] {"name", "suspiciousness_value"});

            for (int i = 0; i < repairTargetList.size(); i++) {
                finalOutput.add(new String[]{repairTargetList.get(i).toStringWithoutSuspiciousnessScore(), String.valueOf(repairTargetList.get(i).getSuspiciousnessScore())});
            }

            sbflRanking.forEach((k, v) -> {
                finalOutput.add(new String[] {k.toString(), String.valueOf(v.getScore())});
            });

            writer.writeAll(finalOutput);
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            File directory = new File(output);
            if (! directory.exists()){
                directory.mkdirs();
            }

            CSVWriter writer = null;
            try {
                writer = new CSVWriter(new FileWriter(output + File.separator + OUTPUT_FILENAME, false),
                        ';',
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.RFC4180_LINE_END);
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<String[]> finalOutput = new ArrayList<String[]>();
            finalOutput.add(new String[] {"name", "suspiciousness_value"});

            sbflRanking.forEach((k, v) -> {
                finalOutput.add(new String[] {k.toString(), String.valueOf(v.getScore())});
            });

            writer.writeAll(finalOutput);
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
