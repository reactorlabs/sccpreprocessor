import java.util.HashSet;
import java.util.Set;

/**
 * Created by peta on 20.12.16.
 */
public class Stats {

    public static void help() {
        System.out.println("stats FOLDER");
        System.out.println("    Prints statistics for tokenzed files in given folder");
        System.out.println("    FOLDER - folder where to look for the CSV files");
        System.out.println("");
    }

    public static void print(String[] args) {
        if (args.length < 2)
            throw new RuntimeException("Invalid number of arguments");
        String folder = args[1];
        Stats s = new Stats(folder);
        s.statsProjects();
        s.statsFiles();
        s.statsGeneric();
    }

    private Stats(String folder) {
        folder_ = folder;
    }

    private void statsProjects() {
        String filename = folder_ + "/" + Config.PROJECTS + ".csv";
        int total = LineReader.file(filename, (String line) -> { });
        System.out.println("Total projects:             " + total);
    }

    private void statsFiles() {
        String filename = folder_ + "/" + Config.FILES_EXTRA + ".csv";
        int total = LineReader.file(filename, (String line) -> { });
        System.out.println("Total files:                " + total);
    }

    private void statsGeneric() {
        uniqueTokenHashes_ = new HashSet<String>();
        String filename = folder_ + "/" + Config.GENERIC_STATS + ".csv";
        int total = LineReader.file(filename, (String line) -> {
            String tokenHash = line.split(",")[7];
            uniqueTokenHashes_.add(tokenHash);
        });
        System.out.println("Unique file hashes          " + total);
        System.out.println("Unique token hashes         " + uniqueTokenHashes_.size());
        uniqueTokenHashes_ = null;

    }

    private String folder_;

    private Set<String> uniqueTokenHashes_;
}
