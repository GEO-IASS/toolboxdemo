package eu.amidst.tutorial.usingAmidst.inference;



import COM.hugin.HAPI.ExceptionHugin;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.distribution.Distribution;
import eu.amidst.core.inference.ImportanceSampling;
import eu.amidst.core.variables.Assignment;
import eu.amidst.core.variables.HashMapAssignment;
import eu.amidst.core.variables.Variable;
import eu.amidst.dynamic.datastream.DynamicDataInstance;
import eu.amidst.dynamic.inference.FactoredFrontierForDBN;
import eu.amidst.dynamic.inference.InferenceAlgorithmForDBN;
import eu.amidst.dynamic.io.DynamicDataStreamLoader;
import eu.amidst.dynamic.models.DynamicBayesianNetwork;
import eu.amidst.dynamic.variables.DynamicAssignment;
import eu.amidst.dynamic.variables.HashMapDynamicAssignment;
import eu.amidst.latentvariablemodels.dynamicmodels.DynamicModel;
import eu.amidst.latentvariablemodels.dynamicmodels.KalmanFilter;

import java.io.IOException;
import java.util.Random;

/**
 * Created by rcabanas on 23/05/16.
 */
public class DynModelInference {
	public static void main(String[] args) throws ExceptionHugin, IOException {

		Random rand = new Random(0);

		String path = "datasets/dynamic/noclassdata/";


		DataStream<DynamicDataInstance> data = DynamicDataStreamLoader.open(path+"data0.arff");

		//Learn the model
		DynamicModel model =
				new KalmanFilter(data.getAttributes())
				.setNumHidden(2);

		//Learn the distributions
		model.updateModel(data);

		//Obtain the learned dynamic BN
		DynamicBayesianNetwork dbn = model.getModel();

		// Print the dynamic BN and save it
		System.out.println(dbn);


		//Select the inference algorithm
		InferenceAlgorithmForDBN infer = new FactoredFrontierForDBN(new ImportanceSampling()); // new ImportanceSampling(),  new VMP(),
		infer.setModel(dbn);

		// Set the Variables of interest
		Variable varTarget = dbn.getDynamicVariables().getVariableByName("gaussianHiddenVar1");

		for(int t=0; t<10; t++) {
			// Set the evidence
			HashMapDynamicAssignment assignment = new HashMapDynamicAssignment(2);
			assignment.setValue(dbn.getDynamicVariables().getVariableByName("GaussianVar9"), rand.nextDouble());
			assignment.setValue(dbn.getDynamicVariables().getVariableByName("GaussianVar8"), rand.nextDouble());
			assignment.setTimeID(t);

			// Run the inference
			infer.addDynamicEvidence(assignment);
			infer.runInference();

			// Get the posterior at current instant of time
			Distribution posterior_t = infer.getFilteredPosterior(varTarget);
			System.out.println("t="+t+" "+posterior_t);

			// Get the posterior in the future
			Distribution posterior_t_1 = infer.getPredictivePosterior(varTarget, 1);
			System.out.println("t="+t+"+1 "+posterior_t_1);

		}

	}

}
