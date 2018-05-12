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
        Attribute sepallength = new Attribute("sepallength");
        Attribute sepalwidth = new Attribute("sepalwidth");
        Attribute petallength = new Attribute("petallength");
        Attribute petalwidth = new Attribute("petalwidth");

        FastVector fvClassVal = new FastVector(3);
        fvClassVal.addElement("Iris-setosa");
        fvClassVal.addElement("Iris-versicolor");
        fvClassVal.addElement("Iris-virginica");
        Attribute Class = new Attribute("theClass", fvClassVal);

        FastVector fvAttrVal = new FastVector(5);
        fvAttrVal.addElement(sepallength);
        fvAttrVal.addElement(sepalwidth);
        fvAttrVal.addElement(petallength);
        fvAttrVal.addElement(petalwidth);
        fvAttrVal.addElement(Class);

        Instances dataset = new Instances("whatever", fvAttrVal, 0);

        double[] attrValues = new double[dataset.numAttributes()];
        attrValues[0] = 7.0;
        attrValues[1] = 3.3;
        attrValues[2] = 4.8;
        attrValues[3] = 1.4;

        Instance i1 = new DenseInstance(1.0, attrValues);
        dataset.add(i1);
        dataset.setClassIndex(dataset.numAttributes()-1);

        //TODO: Double => class name transformation
        System.out.println(nb.classifyInstance(dataset.instance(0)));

        return "Hello!";
    }
}
