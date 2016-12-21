package eu.amidst.tutorial.usingAmidst.models;



import COM.hugin.HAPI.ExceptionHugin;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.BayesianNetworkWriter;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.dynamic.datastream.DynamicDataInstance;
import eu.amidst.dynamic.io.DynamicDataStreamLoader;
import eu.amidst.dynamic.models.DynamicBayesianNetwork;
import eu.amidst.latentvariablemodels.dynamicmodels.DynamicModel;
import eu.amidst.latentvariablemodels.dynamicmodels.KalmanFilter;
import eu.amidst.latentvariablemodels.staticmodels.CustomGaussianMixture;
import eu.amidst.latentvariablemodels.staticmodels.Model;

import java.io.IOException;

/**
 * Created by rcabanas on 23/05/16.
 */
public class DynModelLearning {
	public static void main(String[] args) throws ExceptionHugin, IOException {

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

		//Update the model with new information
		for(int i=1; i<12; i++) {
			data = DynamicDataStreamLoader.open(path+"data"+i+".arff");
			model.updateModel(data);
			System.out.println(model.getModel());
		}




	}

}
