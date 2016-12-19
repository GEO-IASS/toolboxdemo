package eu.amidst.tutorial.usingAmidst.datastreams;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.io.DataStreamWriter;
import eu.amidst.core.variables.StateSpaceTypeEnum;
import eu.amidst.dynamic.datastream.DynamicDataInstance;
import eu.amidst.dynamic.io.DynamicDataStreamLoader;

import java.io.IOException;

/**
 * Created by rcabanas on 24/11/16.
 */
public class InputOutputDynamicDataStreams {
	public static void main(String[] args) throws IOException {

		String path = "datasets/dynamic/classdata/";

		DataStream<DynamicDataInstance> data = DynamicDataStreamLoader.loadFromFile(path+"data0.arff");

		data.getAttributes().forEach(att -> {
			String name = att.getName();
			StateSpaceTypeEnum type = att.getStateSpaceType().getStateSpaceTypeEnum();
			System.out.println(name +" "+type.name());
		});


		data.stream().forEach(dataInstance -> System.out.println(dataInstance));


		DataStreamWriter.writeDataToFile(data, path+"data0_copy.arff");
	}
}
