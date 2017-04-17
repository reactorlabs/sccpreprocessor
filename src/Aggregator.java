import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by peta on 10.1.17.
 */
public class Aggregator {

    static final int MIN_DATE = 938972817;
    static final int MONTHS = 216;

    static void find(String[] args) {
        if (args.length < 2)
            throw new RuntimeException("Invalid number of arguments");
        String folder = args[1];
        Aggregator a = new Aggregator(folder);
        //cf.translateHash(4);
        //a.aggregateProjectsGithub();
        a.aggregateProjectsLanguage("Java");
        //a.loadSccClones();
        //a.loadUniqueTokenHashes();
        //a.aggregateAll();

        a.dummy();
    }

    Aggregator(String folder) {
        folder_ = folder;

    }

    Writer w;

    void dummy() {
        try (Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/data/js_clean/projects_npm.csv"), "utf-8"))) {
            String filename = "/data/packages_npm.csv";
            int total = LineReader.file(filename, (String line) -> {
                String id = line.split("json")[0];
                id = id.split("/")[2];
                id = id.substring(0, id.length() -1);
                w.write(id);
                w.write("\n");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }




    }


    static int monthIndex(long epochMs) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(epochMs));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        return (year - 1999) * 12 + month;
    }

    static int monthIndex(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        return (year - 1999) * 12 + month;
    }

/*    void loadSccClones() {
        sccClones_ = new HashSet<>();
        String filename = folder_ + "/" + Config.SCC_DUP + ".csv";
        int total = CSVReader.file(filename, (ArrayList<String> row) -> {
            sccClones_.add(Integer.parseInt(row.get(0)));
        });
        System.out.println("SCC Clones: " + total);

    } */

/*    void loadUniqueTokenHashes() {
        uniqueTokenHashes_ = new HashSet<>();
        String filename = folder_ + "/" + Config.UNIQUE_THASHES + ".csv";
        int total = CSVReader.file(filename, (ArrayList<String> row) -> {
            uniqueTokenHashes_.add(Integer.parseInt(row.get(0)));
        });
        System.out.println("Unique Token Hashes: " + total);
    } */

    void aggregateProjects() {
        aggregates_ = new int[1][MONTHS];
        String filename = folder_ + "/" + Config.PROJECTS + ".csv";
        int total = CSVReader.file(filename, (ArrayList<String> row) -> {
            long cdate = Long.parseLong(row.get(3));
            if (cdate >= MIN_DATE) {
                int month = monthIndex(cdate * 1000);
                if (month > MONTHS)
                    System.out.println(month);
                while (month < MONTHS)
                    aggregates_[0][month++] += 1;
            }
        });
        System.out.println("    projects:         " + total);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(folder_ + "/aggregated.projects.csv"), "utf-8"))) {
            for (int month = 0; month < MONTHS; ++month) {
                writer.write(String.valueOf(month));
                writer.write(" ");
                writer.write(String.valueOf(aggregates_[0][month]));
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void aggregateProjectsGithub() {

        int y = 1999;
        int m = 1;
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(folder_ + "/aggregated.projects.github.csv"), "utf-8"))) {
            for (int month = 0; month < MONTHS; ++month) {
                String mm = String.valueOf(m);
                if (mm.length() == 1)
                    mm = "0" + mm;
                String sURL = "https://api.github.com/search/repositories?q=language:JavaScript+created:<=" + y + "-" + mm + "-01";
                URL url = new URL(sURL);
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader((InputStream) request.getContent()));
                String line = br.readLine();
                line = line.substring(0, line.length() - 1);
                line = line.split(":")[1].split(",")[0];
                int projects = Integer.parseInt(line);
                writer.write(String.valueOf(month));
                writer.write(" ");
                writer.write(String.valueOf(projects));
                writer.write("\n");
                m = m + 1;
                if (m == 13) {
                    y = y + 1;
                    m = 1;
                }
                try {
                    TimeUnit.SECONDS.sleep(8);
                } catch (InterruptedException e) {
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void aggregateProjectsLanguage(String language) {
        aggregates_ = new int[1][MONTHS];
        language_ = 0;
        forked_ = 0;
        deleted_ = 0;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH);
        String filename = folder_ + "/projects.csv";
        int total = CSVReader.file(filename, (ArrayList<String> row) -> {
            // if the project is forked, or deleted, ignore it
            String l = row.get(5);
            if (language.equals(l)) {
                if (! "0".equals(row.get(8))) {
                    ++deleted_;
                    return;
                }
                if (! "\\N".equals(row.get(7))) {
                    ++forked_;
                    return;
                }
                try {
                    int month = monthIndex(format.parse(row.get(6)));
                    if (month > MONTHS)
                        System.out.println(month);
                    while (month < MONTHS)
                        aggregates_[0][month++] += 1;
                    if (++language_ % 10000 == 0)
                        System.out.print(".");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ++language_;
            }
        });
        System.out.println();
        System.out.println("    projects:          " + total);
        System.out.println("    language projects: " + language_);
        System.out.println("    deleted:           " + deleted_);
        System.out.println("    forked:            " + forked_);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(folder_ + "/aggregated.projects." + language + ".csv"), "utf-8"))) {
            for (int month = 0; month < MONTHS; ++month) {
                writer.write(String.valueOf(month));
                writer.write(" ");
                writer.write(String.valueOf(aggregates_[0][month]));
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    void aggregateProjectsAll() {
        aggregates_ = new int[1][MONTHS];
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH);
        String filename = folder_ + "/jsonly.csv";
        int total = CSVReader.file(filename, (ArrayList<String> row) -> {
            int month = 0;
            try {
                month = monthIndex(format.parse(row.get(2)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (month > MONTHS)
                System.out.println(month);
            while (month < MONTHS)
                aggregates_[0][month++] += 1;
        });
        System.out.println("    projects:         " + total);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(folder_ + "/aggregated.projects.all.csv"), "utf-8"))) {
            for (int month = 0; month < MONTHS; ++month) {
                writer.write(String.valueOf(month));
                writer.write(" ");
                writer.write(String.valueOf(aggregates_[0][month]));
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /* Aggregates file counts for months based on their creation date and whether they are:
           - min.js
           - NPM
           - Bower
           - test
     */
    void aggregateAll() {
        aggregates_ = new int[64][MONTHS];
        bower_ = 0;
        String filename = folder_ + "/" + Config.AGGREGATION + ".csv";
        int total = CSVReader.file(filename, (ArrayList<String> row) -> {
            // don't cre about file id
            long cdate = Long.parseLong(row.get(1));
            if (cdate >= MIN_DATE) {
                int fileId = Integer.parseInt(row.get(0));
                int npm = Integer.parseInt(row.get(2));
                int bower = Integer.parseInt(row.get(3)) * 2;
                if (bower > 0)
                    ++bower_;
                int test = Integer.parseInt(row.get(4)) * 4;
                int minjs= row.get(5).equals("min.js") ? 8 : 0;
                int thUnique =uniqueTokenHashes_.contains(Integer.parseInt(row.get(6))) ? 16 : 0;
                int sccUnique = sccClones_.contains(fileId) ? 0 : 32;
                int month = monthIndex(cdate * 1000);
                int counterIdx = npm + bower + test + minjs + sccUnique + thUnique;
                if (month > MONTHS)
                    System.out.println(month);
                while (month < MONTHS)
                    aggregates_[counterIdx][month++] += 1;
            }
        });
        System.out.println("    files:         " + total);
        System.out.println("    bower files:   " + bower_);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(folder_ + "/aggregated.files.csv"), "utf-8"))) {
            for (int month = 0; month < MONTHS; ++month) {
                writer.write(String.valueOf(month));
                writer.write(" ");
                for (int counter = 0; counter < 64; ++counter) {
                    writer.write(String.valueOf(aggregates_[counter][month]));
                    writer.write(" ");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    String folder_;
    int bower_;
    int language_;
    int deleted_;
    int forked_;

    int[][] aggregates_;

    Set<Integer> sccClones_;
    Set<Integer> uniqueTokenHashes_;









}
