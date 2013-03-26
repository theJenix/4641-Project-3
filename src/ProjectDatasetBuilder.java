import java.io.IOException;
import java.util.ArrayList;

import func.KMeansClusterer;
import shared.DataSet;
import shared.DataSetWriter;
import shared.filt.IndependentComponentAnalysis;
import shared.filt.InsignificantComponentAnalysis;
import shared.filt.LabelSplitFilter;
import shared.filt.PrincipalComponentAnalysis;
import shared.filt.RandomizedProjectionFilter;
import shared.filt.ReversibleFilter;
import shared.reader.ArffDataSetReader;
import shared.reader.CSVDataSetReader;

public class ProjectDatasetBuilder {
    private String baseDir = "data/";

    /**
     * @param args
     */
    public static void main(String[] args) {
        DataSet iris = null;
        DataSet segmentation = null;
        try {
            iris = (new ArffDataSetReader("/Applications/weka-3-6-8/data/vote.arff")).read();
            segmentation      = (new ArffDataSetReader("/Applications/weka-3-6-8/data/segmentation-train.arff")).read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        (new LabelSplitFilter()).filter(iris);
        (new LabelSplitFilter()).filter(segmentation);

        // CLUSTER DATA
        // Clustering was done in Weka, so load the data sets here
//        DataSet a_ck = null; // abalone_clustered_kmeans
//        DataSet a_cem = null; // abalone_clustered_expectation_maximization
//
//        DataSet hd_ck = null; // heartdisease_clustered_kmeans
//        DataSet hd_cem = null; // heartdisease_clustered_expectation_maximization
//        try {
//            a_ck = (new ArffDataSetReader("data/clustered/abalone_kmeans.arff"))
//                    .read();
//            a_cem = (new ArffDataSetReader("data/clustered/abalone_emax.arff"))
//                    .read();
//            hd_ck = (new ArffDataSetReader("data/clustered/hd_kmeans.arff"))
//                    .read();
//            hd_cem = (new ArffDataSetReader("data/clustered/hd_emax.arff"))
//                    .read();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // Filter Data

        // REDUCE DATA
        /*
         * Thread abaThread = new Thread(new DataSetWorker(abalone, "abalone"));
         * Thread hdThread = new Thread(new DataSetWorker(hd, "hd"));
         * abaThread.start(); hdThread.start(); try { abaThread.wait();
         * hdThread.wait(); } catch (InterruptedException e) { // TODO
         * Auto-generated catch block e.printStackTrace(); }
         */
        DataSetWorker adw = new DataSetWorker(iris, "abalone");
        adw.run();
        DataSetWorker hdw = new DataSetWorker(segmentation, "hd");
        hdw.run();
    }

    private static class DataSetWorker implements Runnable {
        // static variables
        private static final String reducedDir = "data/reduced/";
        private static final String clustReducedDir = "data/creduced/";

        // the array of DataSets corresponding to the mountain of nnets we need
        // to train
        private DataSet clean;
        ArrayList<Tuple<ReversibleFilter, String>> filters;
        private String setName;
        private final int toKeep = 5;

        public DataSetWorker(DataSet d, String setName) {
            this.setName = setName;
            this.clean = d;
            filters = new ArrayList<Tuple<ReversibleFilter, String>>();
            init();
        }

        public void reduce() {

        }

        public void init() {
            filters.add(new Tuple<ReversibleFilter, String>(
                    new PrincipalComponentAnalysis(clean), "_pca.csv"));
            filters.add(new Tuple<ReversibleFilter, String>(
                    new IndependentComponentAnalysis(clean), "_ica.csv"));
            filters.add(new Tuple<ReversibleFilter, String>(
                    new RandomizedProjectionFilter(toKeep, clean.get(0).size()),
                    "_insig.csv"));
            filters.add(new Tuple<ReversibleFilter, String>(
                    new InsignificantComponentAnalysis(clean), "_rp.csv"));
        }

        public void filter() {
            init();
            for (Tuple<ReversibleFilter, String> tup : filters) {
                ReversibleFilter filter = tup.fst();
                String ext = tup.snd();

                filter.filter(clean);
                DataSetWriter wr = new DataSetWriter(clean, reducedDir
                        + setName + ext);
                try {
                    wr.write();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                filter.reverse(clean);
            }
        }

        @Override
        public void run() {
            filter();
        }

    }

    public static class Tuple<X, Y> {
        private final X fst;
        private final Y snd;

        public Tuple(X x, Y y) {
            this.fst = x;
            this.snd = y;
        }

        public X fst() {
            return this.fst;
        }

        public Y snd() {
            return this.snd;
        }
    }

}
