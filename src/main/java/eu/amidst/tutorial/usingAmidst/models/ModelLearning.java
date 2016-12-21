package eu.amidst.tutorial.usingAmidst.models;



import COM.hugin.HAPI.ExceptionHugin;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.variables.StateSpaceTypeEnum;
import eu.amidst.latentvariablemodels.staticmodels.GaussianMixture;
import eu.amidst.latentvariablemodels.staticmodels.Model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rcabanas on 23/05/16.
 */
public class ModelLearning {
	public static void main(String[] args) throws ExceptionHugin, IOException {


		String path = "datasets/static/noclassdata/";

		List<DataStream<DataInstance>> data = new ArrayList<>();


		data.add(DataStreamLoader.open(path+"data0.arff"));

		for(int i=1; i<12; i++) {
			data.add(DataStreamLoader.open(path + "data" + i + ".arff"));
		}

		data.get(0).getAttributes().forEach(att -> {
			String name = att.getName();
			StateSpaceTypeEnum type = att.getStateSpaceType().getStateSpaceTypeEnum();
			System.out.println(name +" "+type.name());
		});

		// Build the model
		Model model =
				new GaussianMixture(data.get(0).getAttributes())
				.setDiagonal(true);

		// Learn the distributions
		model.updateModel(data.get(0));

		// Obtain the learned BN
		BayesianNetwork bn = model.getModel();

		// Print the BN
		System.out.println(bn);

		//Update the model with new information
		for(int i=1; i<12; i++) {
			model.updateModel(data.get(i));
			System.out.println(model.getModel());
		}




	}

}
