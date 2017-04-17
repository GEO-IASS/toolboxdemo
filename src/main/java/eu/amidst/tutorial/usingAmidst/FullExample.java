package eu.amidst.tutorial.usingAmidst;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.distribution.Distribution;
import eu.amidst.core.inference.ImportanceSampling;
import eu.amidst.core.inference.InferenceAlgorithm;
import eu.amidst.core.inference.messagepassing.VMP;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.variables.Assignment;
import eu.amidst.core.variables.HashMapAssignment;
import eu.amidst.core.variables.StateSpaceTypeEnum;
import eu.amidst.core.variables.Variable;
import eu.amidst.dynamic.inference.FactoredFrontierForDBN;
import eu.amidst.dynamic.inference.InferenceAlgorithmForDBN;
import eu.amidst.dynamic.io.DynamicDataStreamLoader;
import eu.amidst.dynamic.models.DynamicBayesianNetwork;
import eu.amidst.dynamic.variables.HashMapDynamicAssignment;
import eu.amidst.flinklink.core.data.DataFlink;
import eu.amidst.flinklink.core.io.DataFlinkLoader;
import eu.amidst.latentvariablemodels.dynamicmodels.DynamicModel;
import eu.amidst.latentvariablemodels.dynamicmodels.HiddenMarkovModel;
import eu.amidst.latentvariablemodels.dynamicmodels.InputOutputHMM;
import eu.amidst.latentvariablemodels.dynamicmodels.KalmanFilter;
import eu.amidst.latentvariablemodels.staticmodels.FactorAnalysis;
import eu.amidst.latentvariablemodels.staticmodels.GaussianMixture;
import eu.amidst.latentvariablemodels.staticmodels.Model;
import org.apache.flink.api.java.ExecutionEnvironment;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rcabanas on 19/12/16.
 */
public class FullExample {
	public static void main(String[] args) throws Exception {


		/**
		 * This large code-example covers the following points:

			 A) learning from static data (multicore)
			 B) learning from dynamic data (multicore)
			 C) learning from static data (Flink)
			 D) inference in static models (multicore)
			 E) inference in dynamic models (multicore)

		 When making the demo, you should emphasize in the following
		 ideas:

		 	- Simplicity and flexibility of the code: by simply changing
		      a few lines, you can learn either from static or dynamic data,
		      in a multicore or distributed system.
		      In fact, codes A,B and C are quite similar.

		 	- You can also change the predefined model easily: all the classes
		  	  implementing a particular static model inherit the class Model.
		  	  Similarly, all the classes implementing a particular dynamic model
		      inherit the class DynamicModel. Thus, for changing the learnt model
		 	  you simply have to change the constructor.


		 	- The loading methods are 'lazy', in the sense that data is actually
		      read when needed. Invoking the open methods is not very time consuming


		 In this example we focus on learning the set of predefined models. Yet, you could
		 also explain how to create your own model. For that, go to example

		 	eu.amidst.tutorial.usingAmidst.models.CustomModel

		 You could also explain anything about Mavem, which makes quite easy
		 the installation of the toolbox and the dependencies of third party
		 developers. (see pom.xml file)

		 *
		 */




		String path;
		List<DataStream> dataStatic = new ArrayList<>();
		List<DataStream> dataDynamic = new ArrayList<>();
		List<DataFlink> dataFlink = new ArrayList<>();

		BayesianNetwork bn;
		DynamicBayesianNetwork dbn;

		Random rand = new Random(1234);



		/////////////////////////////////////////////////
		//// A) learning from static data (multicore) ///
		/////////////////////////////////////////////////

		path = "datasets/static/noclassdata/";
		for(int i=0; i<12; i++) {
			dataStatic.add(
					DataStreamLoader.open(path + "data" + i + ".arff")
			);
		}


		// Print the attributes names and types
		dataStatic.get(0).getAttributes().forEach(att -> {
			String name = att.getName();
			StateSpaceTypeEnum type = att.getStateSpaceType().getStateSpaceTypeEnum();
			System.out.println(name +" "+type.name());
		});

		// Print the instances from the first dataset (Now the data is loaded)
		dataStatic.get(0).stream().forEach(d -> System.out.println(d));


		// Build a model (with empty distributions)
		// The learnt model can be easily changed by using another constructor
		Model modelStatic =
				new GaussianMixture(dataStatic.get(0).getAttributes())
				.setDiagonal(false);

		//		new FactorAnalysis(dataStatic.get(0).getAttributes())
		//		.setNumberOfLatentVariables(3);


		// Update the model with data
		for(int i=0; i<12; i++) {
			modelStatic.updateModel(dataStatic.get(0));
			System.out.println(modelStatic);
		}




		//////////////////////////////////////////////////////////
		/////// B) learning from dynamic data (multicore) ///////
		/////////////////////////////////////////////////////////

		path = "datasets/dynamic/noclassdata/";
		for(int i=0; i<12; i++) {
			dataDynamic.add(
					DynamicDataStreamLoader.open(path + "data" + i + ".arff")
			);
		}



		// Print the attributes names and types
		dataDynamic.get(0).getAttributes().forEach(att -> {
			String name = att.getName();
			StateSpaceTypeEnum type = att.getStateSpaceType().getStateSpaceTypeEnum();
			System.out.println(name +" "+type.name());
		});

		// Print the instances from the first dataset (Now the data is loaded)
		dataDynamic.get(0).stream().forEach(d -> System.out.println(d));


		// Build a model (with empty distributions)
		DynamicModel modelDynamic =
				new KalmanFilter(dataDynamic.get(0).getAttributes())
				.setNumHidden(2);
		//		new HiddenMarkovModel(dataDynamic.get(0).getAttributes())
		//		.setNumStatesHiddenVar(4);

		// Update the model with data
		for(int i=0; i<12; i++) {
			modelDynamic.updateModel(dataDynamic.get(0));
			System.out.println(modelDynamic);
		}


		////////////////////////////////////////////////////////
		//////// C) learning from static data (Flink) ///////////
		////////////////////////////////////////////////////////

		// Set-up Flink session (simple configuration)
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();


		path = "datasets/static/noclassdata/";
		for(int i=0; i<12; i++) {
			dataFlink.add(
					DataFlinkLoader.open(env, path + "dataflink" + i + ".arff",false)
			); // This could be a hdfs path
		}


		// Print the attributes names and types
		dataFlink.get(0).getAttributes().forEach(att -> {
			String name = att.getName();
			StateSpaceTypeEnum type = att.getStateSpaceType().getStateSpaceTypeEnum();
			System.out.println(name +" "+type.name());
		});

		// Print the instances from the first dataset (Now the data is loaded)
		dataFlink.get(0).getDataSet().collect().forEach(d -> System.out.println(d));

		// Build a model (with empty distributions)
		// The learnt model can be easily changed by using another constructor
		Model modelStaticFlink =
				new GaussianMixture(dataFlink.get(0).getAttributes())
						.setDiagonal(false);



		////////////////////////////////////////////////////////
		////////// D) inference in static models ///////////////
		////////////////////////////////////////////////////////


		// Extract the bn
		bn = modelStatic.getModel();

		// Set the Variables of interest
		Variable varTarget = bn.getVariables().getVariableByName("HiddenVar");

		//we set the evidence
		Assignment assignment = new HashMapAssignment();
		assignment.setValue(bn.getVariables().getVariableByName("GaussianVar8"), 8.0);
		assignment.setValue(bn.getVariables().getVariableByName("GaussianVar9"), -1.0);


		// Set the inference algorithm
		InferenceAlgorithm infer = new VMP(); //new HuginInference(); new ImportanceSampling();
		infer.setModel(bn);
		infer.setEvidence(assignment);

		// Run the inference
		infer.runInference();
		Distribution p = infer.getPosterior(varTarget);
		System.out.println("P(HiddenVar|GaussianVar8=8.0, GaussianVar9=-1.0) = "+p);



		////////////////////////////////////////////////////////
		////////// E) inference in dynamic models ///////////////
		////////////////////////////////////////////////////////


		dbn = modelDynamic.getModel();


		//Select the inference algorithm
		InferenceAlgorithmForDBN inferDyn = new FactoredFrontierForDBN(new ImportanceSampling()); // new ImportanceSampling(),  new VMP(),
		inferDyn.setModel(dbn);

		// Set the Variables of interest
		varTarget = dbn.getDynamicVariables().getVariableByName("gaussianHiddenVar1");

		for(int t=0; t<10; t++) {
			// Set the evidence
			HashMapDynamicAssignment assignmentDyn = new HashMapDynamicAssignment(2);
			assignmentDyn.setValue(dbn.getDynamicVariables().getVariableByName("GaussianVar9"), rand.nextDouble());
			assignmentDyn.setValue(dbn.getDynamicVariables().getVariableByName("GaussianVar8"), rand.nextDouble());
			assignmentDyn.setTimeID(t);

			// Run the inference
			inferDyn.addDynamicEvidence(assignmentDyn);
			inferDyn.runInference();

			// Get the posterior at current instant of time
			Distribution posterior_t = inferDyn.getFilteredPosterior(varTarget);
			System.out.println("t="+t+" "+posterior_t);

			// Get the posterior in the future
			Distribution posterior_t_1 = inferDyn.getPredictivePosterior(varTarget, 1);
			System.out.println("t="+t+"+1 "+posterior_t_1);

		}



	}
}
