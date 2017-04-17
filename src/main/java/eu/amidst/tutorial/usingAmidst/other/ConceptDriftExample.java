package eu.amidst.tutorial.usingAmidst.other;

import eu.amidst.core.conceptdrift.NaiveBayesVirtualConceptDriftDetector;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.latentvariablemodels.staticmodels.ConceptDriftDetector;
import eu.amidst.latentvariablemodels.staticmodels.Model;

/**
 * Created by rcabanas on 17/04/17.
 */
public class ConceptDriftExample {
	public static void main(String[] args) {


		int windowSize = 500;

		DataStream<DataInstance> data = DataStreamLoader.open("./datasets/DriftSets/sea.arff");

		System.out.println(data.getAttributes().toString());


		//Build the model
		Model model =
				new ConceptDriftDetector(data.getAttributes())
						.setWindowSize(windowSize)
						.setClassIndex(3)
						.setTransitionVariance(0.1);


		for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(windowSize)){
			model.updateModel(batch);
			System.out.println(model.getPosteriorDistribution("GlobalHidden_0").
					toString());


		}

		System.out.println(model.getDAG());



	}


}

