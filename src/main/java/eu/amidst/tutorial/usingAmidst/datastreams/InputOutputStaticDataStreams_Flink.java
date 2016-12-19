package eu.amidst.tutorial.usingAmidst.datastreams;


import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.DataStreamWriter;
import eu.amidst.core.variables.StateSpaceTypeEnum;
import eu.amidst.dynamic.datastream.DynamicDataInstance;
import eu.amidst.dynamic.io.DynamicDataStreamLoader;
import eu.amidst.flinklink.core.data.DataFlink;
import eu.amidst.flinklink.core.io.DataFlinkLoader;
import eu.amidst.flinklink.core.io.DataFlinkWriter;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.configuration.Configuration;

import java.io.IOException;
import java.util.List;

/**
 * Created by rcabanas on 24/11/16.
 */
public class InputOutputStaticDataStreams_Flink {
	public static void main(String[] args) throws Exception {



		String path = "datasets/static/classdata/";


		//Set-up Flink session.
		/*Configuration conf = new Configuration();
		conf.setInteger("taskmanager.network.numberOfBuffers", 12000);
		final ExecutionEnvironment env = ExecutionEnvironment.createLocalEnvironment(conf);
		env.getConfig().disableSysoutLogging();
		*/

		// Set-up Flink session
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		// Load the data stream
		DataFlink<DataInstance> data = DataFlinkLoader.open(env, path+"dataFlink0.arff", false);

		data.getAttributes().forEach(att -> {
			String name = att.getName();
			StateSpaceTypeEnum type = att.getStateSpaceType().getStateSpaceTypeEnum();
			System.out.println(name +" "+type.name());
		});


		data.getDataSet().collect().forEach(dataInstance -> System.out.println(dataInstance));

		DataFlinkWriter.writeDataToARFFFolder(data, path+"dataFlink0_copy.arff"); //Different

	}
}
