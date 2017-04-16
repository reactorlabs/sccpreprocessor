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
        Verifier.help();
        Grouping.help();
    }

    public static void main(String [] args) {
        //args = new String[] {"clean", "100", "0", "46", "/data/js_github", ".clean46"};
        //args = new String[] {"rewrite", "100", "99", "/home/peta/top1k"};
        //args = new String[] {"sort", "test"};
        //args = new String[] { "mergesort", "/data/blocks1.file", "/data/blocks2.file", "/data/blocks.file" };
        //args = new String[] { "join", "/data/js_github_cleaned", "/data/top1k", "/data/with_top1k"};
        //args = new String[] { "stats", "/data/with_top1k" };
        //args = new String[] { "verify", "/data/js_github" };
        //args = new String[] { "verify", "/data/tx2/output", "_0" };
        //args = new String[] { "group", "/data/js_github_npm" };


        // these have been cleaned
        //args = new String[] { "nm", "/data/ecoop17/datasets/js" };
        //args = new String[] { "nm", "/home/peta/delete" };
        //args = new String[] { "h2i", "/data/ecoop17/datasets/js/" };
        //args= new String[] { "commits", "/data/ecoop17/datasets/js", "/data/ghtorrent/mysql-2016-11-01/project_commits.csv" };

        //args = new String[] { "originals", "/data/ecoop17/datasets/java" };

        try {
            Long start = System.currentTimeMillis();
            if (args.length < 2)
                throw new RuntimeException("Invalid number of arguments");
            if (args[0].equals("clean"))
                ChunkCleaner.clean(args);
            else if (args[0].equals("rewrite"))
                ChunkRewriter.rewrite(args);
            else if (args[0].equals("sort"))
                TokenizedFilesSorter.sort(args);
            else if (args[0].equals("mergesort"))
                MergeSort.merge(args);
            else if (args[0].equals("join"))
                DatasetJoin.join(args);
            else if (args[0].equals("stats"))
                Stats.print(args);
            else if (args[0].equals("verify"))
                Verifier.verify(args);
            else if (args[0].equals("group"))
                Grouping.group(args);
            else if (args[0].equals("nm"))
                NodeModules.calculate(args);
            else if (args[0].equals("h2i"))
                HashesToInts.convert(args);
            else if (args[0].equals("commits"))
                Commits.calculate(args);
            else if (args[0].equals("originals"))
                ProjectOriginals.analyze(args);
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
