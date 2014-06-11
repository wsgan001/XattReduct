package Xreducer_struct;

import java.io.File;
import java.util.Vector;

public class comFiles {
	public Vector<oneFile> onef = null;
	public int numFile = -1; 
	
	public comFiles(File[] f){
		this.onef = new Vector<oneFile>();
		//int cnt = 0;
		//for(int i=0;i<f.length;++i){
			 
				//oneFile temp = new oneFile(f[i]);
				//this.onef.add(temp);
				 
			 
		//}
		this.numFile = f.length;
		//this.numFile = cnt;
	}
}
