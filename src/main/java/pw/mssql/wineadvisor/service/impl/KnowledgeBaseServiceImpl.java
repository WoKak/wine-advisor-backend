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

        //TODO : Change for wine attributes
        Attribute alcohol = new Attribute("alcohol", (FastVector) null);
        Attribute strain = new Attribute("strain", (FastVector) null);
        Attribute kind = new Attribute("kind", (FastVector) null);
        Attribute dryness = new Attribute("dryness", (FastVector) null);
        Attribute origin = new Attribute("origin", (FastVector) null);

        FastVector fvClassVal = new FastVector(9);
        fvClassVal.addElement("OWOCE MORZA");
        fvClassVal.addElement("RYBY");
        fvClassVal.addElement("DESERY");
        fvClassVal.addElement("SERY");
        fvClassVal.addElement("JAGNIĘCINA");
        fvClassVal.addElement("MAKARONY");
        fvClassVal.addElement("MIĘSA CZERWONE");
        fvClassVal.addElement("MIĘSA BIAŁE");
        fvClassVal.addElement("APERITIF");
        Attribute theClass = new Attribute("theClass", fvClassVal);

        FastVector fvAttrVal = new FastVector(6);
        fvAttrVal.addElement(alcohol);
        fvAttrVal.addElement(strain);
        fvAttrVal.addElement(kind);
        fvAttrVal.addElement(dryness);
        fvAttrVal.addElement(origin);
        fvAttrVal.addElement(theClass);

        Instances dataset = new Instances("whatever", fvAttrVal, 0);

        double[] attrValues = new double[dataset.numAttributes()];
        attrValues[0] = 13.0;
        attrValues[1] = dataset.attribute("strain").addStringValue("PINOT");
        attrValues[2] = dataset.attribute("kind").addStringValue("CZERWONE");
        attrValues[3] = dataset.attribute("dryness").addStringValue("WYTRAWNE");
        attrValues[4] = dataset.attribute("origin").addStringValue("NOWA ZELANDIA");

        Instance i1 = new DenseInstance(1.0, attrValues);
        dataset.add(i1);
        dataset.setClassIndex(dataset.numAttributes()-1);
//        i1.setDataset(dataset);
        String result;

        switch ((int) nb.classifyInstance(dataset.instance(0))) {

            case 0:
                result = "OWOCE MORZA";
                break;
            case 1:
                result = "RYBY";
                break;
            case 2:
                result = "DESERY";
                break;
            case 3:
                result = "SERY";
                break;
            case 4:
                result = "JAGNIĘCINA";
                break;
            case 5:
                result = "MAKARONY";
                break;
            case 6:
                result = "MIĘSA CZERWONE";
                break;
            case 7:
                result = "MIĘSA BIAŁE";
                break;
            case 8:
                result = "APERITIF";
                break;
            default:
                result = "TYM RAZEM SIĘ NIE UDAŁO";
                break;
        }

        result = result.toLowerCase();

        System.out.println(result);

        return result;
    }
}
