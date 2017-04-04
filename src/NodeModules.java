import java.io.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by peta on 3.1.17.
 */
public class NodeModules {

    public static void help() {
        System.out.println("nm FOLDER");
        System.out.println("    Calculates nm module index for each file.");
        System.out.println("    FOLDER - folder where to look for the CSV files");
        System.out.println("");
    }

    public static void calculate(String [] args) {
        if (args.length < 2)
            throw new RuntimeException("Invalid number of arguments");
        String folder = args[1];
        NodeModules nm = new NodeModules(folder);
        nm.calculateNmIndex();
    }

    private NodeModules(String folder) {
        folder_ = folder;
    }

    private void calculateNmIndex() {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(folder_ + "/" + Config.FILES_NM + ".txt"), "utf-8"))) {
            maxDepth_ = 0;
            numNM_ = 0;
            String filename = folder_ + "/" + Config.FILES + ".txt";
            int total = CSVReader.file(filename, (ArrayList<String> row) -> {
                int id = Integer.valueOf(row.get(0));
                String url = row.get(2);
                String[] s = url.split("/");
                int depth = 0;
                String name = "";
                String blame = "";
                for (int i = 0; i < s.length; ++i) {
                    if (s[i].equals( "node\\_modules")) {
                        ++depth;
                        if (i+1 < s.length)
                            name = s[i+1];
                        if (depth == 1)
                            blame = name;
                    }
                }
                if (depth > 0) {
                    ++numNM_;
                    if (depth > maxDepth_)
                        maxDepth_ = depth;
                }
                writer.write(row.get(0));
                writer.write(",");
                writer.write(String.valueOf(depth));
                writer.write(",\"");
                writer.write(name);
                writer.write("\",\"");
                writer.write(blame);
                writer.write("\",\"");
                writer.write(s[s.length - 1]); // name of the file
                writer.write("\"\n");
            });
            System.out.println("    analyzed files:      " + total);
            System.out.println("    node module files:   " + numNM_);
            System.out.println("    max depth:           " + maxDepth_);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int maxDepth_;
    private int numNM_;
    private String folder_;

}
