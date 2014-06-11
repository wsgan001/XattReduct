package Xreducer_gui;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class NotepadFile {

	private Component parent;
	private File file;
	private JFileChooser filechooser= new JFileChooser(new File(System.getProperty("user.dir").replace("\\", "/")+"/data/"));
	public NotepadFile(final Component parent){
		this.parent = parent;
		filechooser.addChoosableFileFilter(new NotepadFileFilter("txt"));
		filechooser.addChoosableFileFilter(new NotepadFileFilter("arff"));
		
	}
	public File getFile(){
		return file;
	}
	public void openFile(File openfile){
		String str = null;
		if(openfile == null){
			if(getFile()==null){
				filechooser.setSelectedFile(getFile());
				
				if(JFileChooser.APPROVE_OPTION==filechooser.showOpenDialog(parent)){
					this.file=filechooser.getSelectedFile();
				}
				else
					this.file = null;
			}
		}
		else
			this.file = openfile;
	}
}
class NotepadFileFilter extends FileFilter{
	String str;
	public NotepadFileFilter(String str){
		this.str=str;
	}
	@Override
	public boolean accept(File file) {
		// TODO Auto-generated method stub
		if(file.isDirectory())
			return true;
		String fileName = file.getName();
		int index =fileName.lastIndexOf('.');
		if(index>0&&index<fileName.length()-1){
			String exetension = fileName.substring(index+1).toLowerCase();
			if(exetension.equals(str))
				return true;
		}
		return false;
	}
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		if(str.equals("arff")){
			return "arff文件(*.arff)";
		}
		if(str.equals("txt")){
			return "文本文件(*.txt)";
		}
		return "";
	}
}
