import java.util.ArrayList;

/**
 * Created by peta on 17.12.16.
 */
public class SccPreprocessor {

    public static void help() {
        System.out.println("Usage:");
        ChunkCleaner.help();
        ChunkRewriter.help();
        TokenizedFilesSorter.help();
        MergeSort.help();
        Stats.help();
    }

    public static void main(String [] args) {
        //args = new String[] {"clean", "100", "0", "46", "/data/js_github", ".clean46"};
        //args = new String[] {"rewrite", "100", "99", "/home/peta/top1k"};
        //args = new String[] {"sort", "test"};
        //args = new String[] { "mergesort", "/data/blocks1.file", "/data/blocks2.file", "/data/blocks.file" };
        //args = new String[] { "join", "/data/js_github_cleaned", "/data/top1k", "/data/with_top1k"};
        args = new String[] { "stats", "/data/with_top1k" };
        try {
            Long start = System.currentTimeMillis();
            if (args.length < 1)
                throw new RuntimeException("Invalid number of arguments");
            if (args[0] == "clean")
                ChunkCleaner.clean(args);
            else if (args[0] == "rewrite")
                ChunkRewriter.rewrite(args);
            else if (args[0] == "sort")
                TokenizedFilesSorter.sort(args);
            else if (args[0] == "mergesort")
                MergeSort.merge(args);
            else if (args[0] == "join")
                DatasetJoin.join(args);
            else if (args[0] == "stats")
                Stats.print(args);
            else
                throw new RuntimeException("Invalid action " + args[0]);
            Long end = System.currentTimeMillis();
            Long seconds = (end - start) / 1000;
            System.out.println("TOTAL TIME: " + seconds + " [s]");
            System.out.println("ALL DONE");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            help();
        }
    }
}
