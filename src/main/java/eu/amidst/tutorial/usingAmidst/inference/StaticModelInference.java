package eu.amidst.tutorial.usingAmidst.inference;



import COM.hugin.HAPI.ExceptionHugin;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.distribution.Distribution;
import eu.amidst.core.inference.InferenceAlgorithm;
import eu.amidst.core.inference.messagepassing.VMP;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.variables.Assignment;
import eu.amidst.core.variables.HashMapAssignment;
import eu.amidst.core.variables.Variable;
import eu.amidst.latentvariablemodels.staticmodels.GaussianMixture;
import eu.amidst.latentvariablemodels.staticmodels.Model;

import java.io.IOException;

/**
 * Created by rcabanas on 23/05/16.
 */
public class StaticModelInference {
	public static void main(String[] args) throws ExceptionHugin, IOException {


		String path = "datasets/static/noclassdata/";


		DataStream<DataInstance> data = DataStreamLoader.open(path+"data0.arff");

		// Build the model
		Model model =
				new GaussianMixture(data.getAttributes())
		//		.setDiagonal(true)
				.setNumStatesHiddenVar(2);

		// Learn the distributions
		model.updateModel(data);

		// Obtain the learned BN
		BayesianNetwork bn = model.getModel();

		// Print the BN
		System.out.println(bn);

		//Update the model with new information
		for(int i=1; i<12; i++) {
			data = DataStreamLoader.open(path+"data"+i+".arff");
			model.updateModel(data);
			System.out.println(model.getModel());
		}


		///// Inference /////

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





	}

}
