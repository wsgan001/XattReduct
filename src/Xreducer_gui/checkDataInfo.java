package Xreducer_gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import weka.core.Instances;

public class checkDataInfo {
	public static Vector getDataInfo(int index,File file) throws Exception, IOException{
		Vector info = new Vector();
		info.add(0, index);
		info.add(1, file);
		String fne = file.getName();
		int eindex =fne.lastIndexOf('.');
		String fn = fne.substring(0,eindex).toLowerCase();
		info.add(2, fn);
		Instances dataset = new Instances(new FileReader(file));
		dataset.setClassIndex(dataset.numAttributes()-1); 
		info.add(3, dataset.numInstances());
		int Nominalcnt = 0;
		for(int i=0;i<dataset.numAttributes()-1;++i){
			if(dataset.attribute(i).isNominal())
				Nominalcnt++;
		}
		info.add(4, Nominalcnt);
		info.add(5, dataset.numAttributes()-Nominalcnt-1);
		info.add(6, dataset.numClasses());
		return info;
	}
}
