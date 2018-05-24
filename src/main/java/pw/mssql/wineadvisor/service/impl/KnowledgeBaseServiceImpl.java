package pw.mssql.wineadvisor.service.impl;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import pw.mssql.wineadvisor.model.Wine;
import pw.mssql.wineadvisor.service.KnowledgeBaseService;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.*;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;
import java.io.IOException;

public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    private static String[] strains = {
            "GRACIANO", "PROSECCO", "BLAUBURGER", "LOUREIRO", "GLERA",
            "RONDINELLA", "SANGIOVESE", "MOSCATEL", "MALVASIA", "MALBEC",
            "CASTELAO", "VERMENTINO", "CABERNET", "MACABEO", "PINOT",
            "MONTEPULCIANO", "GEWURZTRAMINER", "MERLOT", "RIESLING", "NEGROAMARO",
            "MARZEMINO", "MOSCATO", "AGLIANICO", "BOMBINO", "PRIMITIVO",
            "MUSCAT", "TEMPRANILLO", "RABOSO", "FURMINT", "CHARDONNAY",
            "FETEASCA", "GRENACHE", "SAPERAVI", "PEDRO", "NERO"
    };

    private static String[] kinds = {"BIALE","WINO-MUSUJACE","ROZOWE","CZERWONE"};

    private static String[] drynesses = {"WYTRAWNE","SLODKIE","POLSLODKIE","POLWYTRAWNE"};

    private static String[] origins = {
            "CHILE","SLOWACJA","GRUZJA","SZWECJA","MOLDAWIA",
            "NOWA-ZELANDIA","FRANCJA","NIEMCY","WLOCHY","WEGRY",
            "PORTUGALIA","HISZPANIA"
    };

    private static String[] classes = {
            "OWOCE-MORZA","RYBY","DESERY","SERY","JAGNIECINA",
            "MAKARONY","MIESA-CZERWONE","MIESA-BIALE","APERITIF"
    };

    private NaiveBayes nb;

    public KnowledgeBaseServiceImpl() throws Exception {

        trainClassifier();
    }

    @Override
    public void trainClassifier() throws Exception {

        Mongo mongo = new Mongo("localhost", 27017);
        DB db = mongo.getDB("wineadv");

        GridFS gfsArff = new GridFS(db, "arff");

        GridFSDBFile arff = gfsArff.findOne("latest.arff");
        arff.writeTo("latest.arff");

        DataSource source = new DataSource("latest.arff");
        Instances dataset = source.getDataSet();
        dataset.setClassIndex(dataset.numAttributes()-1);
        this.nb = new NaiveBayes();
        nb.buildClassifier(dataset);
        Evaluation eval = new Evaluation(dataset);
        eval.evaluateModel(nb, dataset);
    }

    @Override
    public void moveLatestFileToMongoDB() throws IOException {

        Mongo mongo = new Mongo("localhost", 27017);
        DB db = mongo.getDB("wineadv");

        String newFileName = "latest.arff";
        File arffFile = new File("latest.arff");

        GridFS gfsArff = new GridFS(db, "arff");
        GridFSInputFile gfsFile = gfsArff.createFile(arffFile);
        gfsFile.setFilename(newFileName);
        gfsFile.save();

        DBCursor cursor = gfsArff.getFileList();
        while (cursor.hasNext()) {
            System.out.println(cursor.next());
        }
    }

    @Override
    public String classifyWine(Wine wine) throws Exception {

        //Initializing attributes

        Attribute alcohol = new Attribute("alcohol");

        FastVector strainsVals = new FastVector();
        for (int i = 0; i < strains.length; i++) {

            strainsVals.addElement(strains[i]);
        }
        Attribute strain = new Attribute("strain", strainsVals);

        FastVector kindsVals = new FastVector();
        for (int i = 0; i < kinds.length; i++) {

            kindsVals.addElement(kinds[i]);
        }
        Attribute kind = new Attribute("kind", kindsVals);

        FastVector drynessesVals = new FastVector();
        for (int i = 0; i < drynesses.length; i++) {

            drynessesVals.addElement(drynesses[i]);
        }
        Attribute dryness = new Attribute("dryness", drynessesVals);

        FastVector originsVals = new FastVector();
        for (int i = 0; i < origins.length; i++) {

            originsVals.addElement(origins[i]);
        }
        Attribute origin = new Attribute("origin", originsVals);

        //Creating dataset

        FastVector fvAttrVal = new FastVector();
        fvAttrVal.addElement(alcohol);
        fvAttrVal.addElement(strain);
        fvAttrVal.addElement(kind);
        fvAttrVal.addElement(dryness);
        fvAttrVal.addElement(origin);
        Instances dataset = new Instances("whatever", fvAttrVal, 0);

        //Creating instance for classification
        double[] attrValues = new double[dataset.numAttributes()];
        attrValues[0] = (double) wine.getAlcoholPercentage();
        attrValues[1] = dataset.attribute("strain").indexOfValue(wine.getGrapeVariety());
        attrValues[2] = dataset.attribute("kind").indexOfValue(wine.getWineType());
        attrValues[3] = dataset.attribute("dryness").indexOfValue(wine.getWineDryness());
        attrValues[4] = dataset.attribute("origin").indexOfValue(wine.getWineOrigin());
        Instance i1 = new DenseInstance(1.0, attrValues);
        dataset.add(i1);

        dataset.setClassIndex(dataset.numAttributes()-1);

        return classes[(int) nb.classifyInstance(dataset.firstInstance())];
    }
}
