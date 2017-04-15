
/* Calculates project originals, i.e. for

 */

import java.util.ArrayList;
import java.util.HashMap;

public class ProjectOriginals {
    public static void help() {
        System.out.println("originals FOLDER");
        System.out.println("    Does project originals calculation.");
        System.out.println("    FOLDER - folder where to look for the CSV files");
        System.out.println("");
    }


    public static void analyze(String [] args) {
        if (args.length < 2)
            throw new RuntimeException("Invalid number of arguments");
        String folder = args[1];
        ProjectOriginals po = new ProjectOriginals(folder);
        po.loadProjects();
        po.analyzeProjectClones(0);




    }

    private ProjectOriginals(String folder) {
        folder_ = folder;
    }


    /* For each project, the following is remembered:
     */
    private class Record {
        // when the file was created
        int created;
        // Id of the original project
        int originalId;
        // similarity (i.e. how much of the original project is in the host project?)
        double similarity;

        Record(int created) {
            this.created = created;
            originalId = -1;
            similarity = 0;
        }
    }




    int errors = 0;
    int lastId = 0;

    /* Loads all projects.
     */
    private void loadProjects() {
        commits_ = new HashMap<>();
        String filename = folder_ + "/" + Config.PROJECTS_EXTRA + ".txt";
        System.out.println("Loading projects...");
        int total = CSVReader.file(filename, (ArrayList<String> row) -> {
            try {
                int id = Integer.parseInt(row.get(0));
                int created = Integer.parseInt(row.get(1));
                commits_.put(id, new Record(created));
            } catch (Exception e) {
                errors += 1;
            }
        });
        System.out.println("    total projects:          " + total);
        System.out.println("    errors:                  " + errors);
    }

    /* When we have the projects, analyze the project clone records to find the originals.
     */
    private void analyzeProjectClones(int index) {
        String filename = folder_ + "/" + Config.PROJECT_CLONES + "." + String.valueOf(index) + ".csv";
        System.out.println("Analyzing project clones: " + filename);
        int total = CSVReader.file(filename, (ArrayList<String> row) -> {
            int cloneId = Integer.parseInt(row.get(0));
            int hostId = Integer.parseInt(row.get(4));
            double similarity = Double.parseDouble(row.get(7));
        });
        System.out.println("    total records:           " + total);
    }


/*


            // cloneId 0
            // cloneClonedFiles 1
            // cloneTotalFiles 2
            // cloneCloningPercent 3
            // hostId 4
            // hostAffectedFiles 5
            // hostTotalFiles 6
            // hostAffectedPercent 7

 */


    private String folder_;
    private HashMap<Integer, Record> commits_;

}
