package eu.amidst.tutorial.usingAmidst.createdatasets;

import eu.amidst.core.datastream.*;
import eu.amidst.core.io.DataStreamWriter;
import eu.amidst.core.variables.StateSpaceType;
import eu.amidst.dynamic.datastream.DynamicDataInstance;
import eu.amidst.dynamic.utils.DataSetGenerator;
import eu.amidst.flinklink.core.data.DataFlink;
import eu.amidst.flinklink.core.io.DataFlinkLoader;
import eu.amidst.flinklink.core.io.DataFlinkWriter;
import org.apache.flink.api.java.ExecutionEnvironment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/**
 * Created by rcabanas on 20/05/16.
 */
public class CreateDynamicData {

    public static void main(String[] args) throws Exception{
		int nOfDisc;
		int nOfCont;
		DataStream<DynamicDataInstance> dataGaussians = null;



        String path = "datasets/dynamic/classdata/";


        for(int i=0; i<12; i++) {
            //generate(path, "data"+i+".arff",500, 1,3, names1, i);
			nOfCont = 10;
			nOfDisc = 1;
			dataGaussians = DataSetGenerator.generate(1,1000,nOfDisc,nOfCont);
			DataStreamWriter.writeDataToFile(dataGaussians, path+"data"+i+".arff");


			makeDistributed(path, "data"+i+".arff", "dataflink"+i+".arff");
        }

		path = "datasets/dynamic/noclassdata/";
		for(int i=0; i<12; i++) {
			//generate(path, "data"+i+".arff",500, 1,3, names1, i);
			nOfCont = 10;
			nOfDisc = 0;
			dataGaussians = DataSetGenerator.generate(1,1000,nOfDisc,nOfCont);
			DataStreamWriter.writeDataToFile(dataGaussians, path+"data"+i+".arff");


			makeDistributed(path, "data"+i+".arff", "dataflink"+i+".arff");
		}











    }



    public static void generate(String path, String filename, int nSamples, int nDiscr, int nCont, String names[], int month) throws IOException {
     //   int nContinuousAttributes=4;
     //   int nDiscreteAttributes=1;
     //   String names[] = {"SEQUENCE_ID", "TIME_ID", "Default", "Income","Expenses","Balance","TotalCredit"};
       // String path = "datasets/simulated/";
       // int nSamples=1000;


        //Generate random dynamic data
		int seed = 1234;
		System.out.println(seed);
        DataStream<DynamicDataInstance> data  = DataSetGenerator.generate(seed,nSamples,nDiscr,nCont);
        List<Attribute> list = new ArrayList<Attribute>();



        //Replace the names
        IntStream.range(0, data.getAttributes().getNumberOfAttributes())
                .forEach(i -> {
                    Attribute a = data.getAttributes().getFullListOfAttributes().get(i);
                    StateSpaceType s = a.getStateSpaceType();

                    System.out.println(s);

                    Attribute a2 = new Attribute(a.getIndex(), names[i],s);
                    list.add(a2);
                });


        //New list of attributes
        Attributes att2 = new Attributes(list);



        List<DataInstance> listData = data.stream().collect(Collectors.toList());


        //Datastream with the new attribute names
        DataStream<DataInstance> data2 =
                new DataOnMemoryListContainer<DataInstance>(att2,listData);



        //Write to a single file
        DataStreamWriter.writeDataToFile(data2, path + filename);



    }


    public static DataStream<DataInstance> concat (DataStream<DataInstance> d1, DataStream<DataInstance> d2) {

        Attributes att = d1.getAttributes();



        Stream<DataInstance> stream12 = Stream.concat(d1.stream(), d2.stream());
        List<DataInstance> listData = stream12.collect(Collectors.toList());

        DataStream<DataInstance> data =
                new DataOnMemoryListContainer<DataInstance>(att,listData);

        return data;

    }


    public static void makeDistributed(String path, String localfile, String distributedfile) throws Exception {
        //Write to a distributed folder
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataFlink<DataInstance> data2Flink = DataFlinkLoader.loadDataFromFile(env, path + localfile, false);
        DataFlinkWriter.writeDataToARFFFolder(data2Flink, path + distributedfile);


    }



	public static void generateNaiveBayes(Attributes att) {


	}


}
