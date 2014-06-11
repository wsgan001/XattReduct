package Xreducer_core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

 

import Xreducer_struct.oneAlgRecord;
import Xreducer_struct.oneAlgorithm.xCategory;
import Xreducer_struct.oneAlgorithm.xStyle;
import Xreducer_struct.oneDataRecord;
import Xreducer_struct.oneFile.xNSstyle;


public class Utils_XMLoperator {
	
	public static void saveXml(String fileName, int style ,Vector<oneDataRecord> re) throws Exception{
		DocumentBuilderFactory factory = DocumentBuilderFactory .newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder(); 
		Document document = builder.newDocument(); 
		switch(style){
		case 0:{//保存文件信息记录
			Element root = document.createElement("保存数据文件信息记录"); 
			document.appendChild(root); 
			Element fileinfo = document.createElement("数据文件信息"); 
			for(int i=0;i<re.size();++i){
				Element filerecord = document.createElement("File"); 
				
				Element filepath = document.createElement("filepath"); 
				filepath.appendChild(document.createTextNode(re.get(i).filepath));
				filerecord.appendChild(filepath); 
				
				Element filename = document.createElement("filename"); 
				filename.appendChild(document.createTextNode(re.get(i).filename));
				filerecord.appendChild(filename);
				
				Element ins = document.createElement("ins"); 
				ins.appendChild(document.createTextNode(Integer.toString(re.get(i).ins)));
				filerecord.appendChild(ins);
				
				Element att = document.createElement("att"); 
				att.appendChild(document.createTextNode(Integer.toString(re.get(i).att)));
				filerecord.appendChild(att);
				
				Element cla = document.createElement("cla"); 
				cla.appendChild(document.createTextNode(Integer.toString(re.get(i).cla)));
				filerecord.appendChild(cla);
				
				Element progress = document.createElement("progress"); 
				progress.appendChild(document.createTextNode(Integer.toString(re.get(i).progress)));
				filerecord.appendChild(progress);
				
				Element state = document.createElement("state"); 
				state.appendChild(document.createTextNode(Integer.toString(re.get(i).state)));
				filerecord.appendChild(state);
				
				Element ischoose = document.createElement("ischoose"); 
				ischoose.appendChild(document.createTextNode(re.get(i).ischoose==true?"Yes":"No"));
				filerecord.appendChild(ischoose);
				
				
				/**********************oneFile**********************/
				
				Element baseindex = document.createElement("baseindex"); 
				baseindex.appendChild(document.createTextNode(Integer.toString(re.get(i).baseindex)));
				filerecord.appendChild(baseindex);
				
				Element signifcantlevel = document.createElement("signifcantlevel"); 
				signifcantlevel.appendChild(document.createTextNode(Double.toString(re.get(i).signifcantlevel)));
				filerecord.appendChild(signifcantlevel);
				
				Element numReduce = document.createElement("numReduce"); 
				numReduce.appendChild(document.createTextNode(Integer.toString(re.get(i).numReduce)));
				filerecord.appendChild(numReduce);
				Element numRun = document.createElement("numRun"); 
				numRun.appendChild(document.createTextNode(Integer.toString(re.get(i).numRun)));
				filerecord.appendChild(numRun);
				Element numFold = document.createElement("numFold"); 
				numFold.appendChild(document.createTextNode(Integer.toString(re.get(i).numFold)));
				filerecord.appendChild(numFold);
				Element numcomAlg = document.createElement("numcomAlg"); 
				numcomAlg.appendChild(document.createTextNode(Integer.toString(re.get(i).numcomAlg)));
				filerecord.appendChild(numcomAlg);
				
				Element trainclname = document.createElement("trainclname"); 
				trainclname.appendChild(document.createTextNode(re.get(i).trainclname));
				filerecord.appendChild(trainclname);
				
				Element NSindex = document.createElement("NSindex"); 
				NSindex.appendChild(document.createTextNode(Integer.toString(re.get(i).NSindex)));
				filerecord.appendChild(NSindex);
				Element NSstyle = document.createElement("NSstyle"); 
				NSstyle.appendChild(document.createTextNode(re.get(i).NSstyle.getValue()));
				filerecord.appendChild(NSstyle);
				Element NSmaxIndex = document.createElement("NSmaxIndex"); 
				NSmaxIndex.appendChild(document.createTextNode(Integer.toString(re.get(i).NSmaxIndex)));
				filerecord.appendChild(NSmaxIndex);
				Element NSclname = document.createElement("NSclname"); 
				NSclname.appendChild(document.createTextNode(re.get(i).NSclname));
				filerecord.appendChild(NSclname);
				Element randomI = document.createElement("randomI"); 
				randomI.appendChild(document.createTextNode(Integer.toString(re.get(i).randomI)));
				filerecord.appendChild(randomI);

				
				/**********************algs**********************/
				Element Algsinfo = document.createElement("AlgorithmsInfo"); 
				int J = re.get(i).numcomAlg;
				for(int j=0;j<J;++j){
					Element oneAlgorithm = document.createElement("oneAlgorithm");
					
					Element alg_category = document.createElement("alg_category"); 
					alg_category.appendChild(document.createTextNode(re.get(i).algs.get(j).alg_category.getValue()));
					oneAlgorithm.appendChild(alg_category);
					Element alg_style = document.createElement("alg_style"); 
					
					alg_style.appendChild(document.createTextNode(re.get(i).algs.get(j).alg_style.getValue()));
					oneAlgorithm.appendChild(alg_style);
					
					
					Element alg_ID = document.createElement("alg_ID"); 
					alg_ID.appendChild(document.createTextNode(Integer.toString(re.get(i).algs.get(j).alg_ID)));
					oneAlgorithm.appendChild(alg_ID);
					
					Element alg_algname = document.createElement("alg_algname"); 
					alg_algname.appendChild(document.createTextNode(re.get(i).algs.get(j).alg_algname));
					oneAlgorithm.appendChild(alg_algname);
					
					Element alg_alpha = document.createElement("alg_alpha"); 
					alg_alpha.appendChild(document.createTextNode(Double.toString(re.get(i).algs.get(j).alg_alpha)));
					oneAlgorithm.appendChild(alg_alpha);
					
					
					Element alg_eval = document.createElement("alg_eval"); 
					alg_eval.appendChild(document.createTextNode(re.get(i).algs.get(j).alg_eval==null?"None":re.get(i).algs.get(j).alg_eval));
					oneAlgorithm.appendChild(alg_eval);
					Element alg_search = document.createElement("alg_search"); 
					alg_search.appendChild(document.createTextNode(re.get(i).algs.get(j).alg_search==null?"None":re.get(i).algs.get(j).alg_search));
					oneAlgorithm.appendChild(alg_search);
					Element alg_flag = document.createElement("alg_flag"); 
					alg_flag.appendChild(document.createTextNode(re.get(i).algs.get(j).alg_flag==true?"Yes":"No"));
					oneAlgorithm.appendChild(alg_flag);
					
					Element alg_numReduce = document.createElement("alg_numReduce"); 
					alg_numReduce.appendChild(document.createTextNode(Integer.toString(re.get(i).algs.get(j).alg_numReduce)));
					oneAlgorithm.appendChild(alg_numReduce);
					Element alg_numRun = document.createElement("alg_numRun"); 
					alg_numRun.appendChild(document.createTextNode(Integer.toString(re.get(i).algs.get(j).alg_numRun)));
					oneAlgorithm.appendChild(alg_numRun);
					
					Element alg_clname = document.createElement("alg_clname"); 
					alg_clname.appendChild(document.createTextNode(re.get(i).algs.get(j).alg_clname));
					oneAlgorithm.appendChild(alg_clname);
					
					Element alg_numFold = document.createElement("alg_numFold"); 
					alg_numFold.appendChild(document.createTextNode(Integer.toString(re.get(i).algs.get(j).alg_numFold)));
					oneAlgorithm.appendChild(alg_numFold);
					
					Algsinfo.appendChild(oneAlgorithm);
				}
				filerecord.appendChild(Algsinfo);
				
				
				fileinfo.appendChild(filerecord);
				/*File f = new File(re.get(i).filepath);
				if(f.exists()){
					Element state = document.createElement("state"); 
					state.appendChild(document.createTextNode(Integer.toString(re.get(i).state)));
					Element state = document.createElement("state");
				}
				else{
					Element state = document.createElement("state"); 
					state.appendChild(document.createTextNode("3"));
				}*/
				
				
				/***************************************************/
				
			}
			root.appendChild(fileinfo);
			break;
		}
		default:break;
		}
		TransformerFactory tf = TransformerFactory.newInstance(); 
		try { 
			//先删除记录文件
			File temp = new File(fileName);
			temp.delete();
			
			Transformer transformer = tf.newTransformer(); 
			DOMSource source = new DOMSource(document); 
			transformer.setOutputProperty(OutputKeys.ENCODING, "gb2312"); 
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
			PrintWriter pw = new PrintWriter(new FileOutputStream(fileName)); 
			StreamResult result = new StreamResult(pw); 
			 

			transformer.transform(source, result); 
			//System.out.println("生成XML文件成功!"); 
			} 
		catch (TransformerConfigurationException e) { 
			System.out.println(e.getMessage()); } 
		catch (IllegalArgumentException e) { 
			System.out.println(e.getMessage()); } 
		catch (FileNotFoundException e) { 
			System.out.println(e.getMessage()); } 
		catch (TransformerException e) { 
			System.out.println(e.getMessage()); } 
	}
	
	public static Vector<oneDataRecord>  parserXml(String fileName , int style) {
		Vector<oneDataRecord>  res = new Vector<oneDataRecord> ();
		switch(style){
			case 0:{//保存文件信息记录
				try { 
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
					DocumentBuilder db = dbf.newDocumentBuilder(); 
					Document document = db.parse (new org.xml.sax.InputSource(new  java.io.FileInputStream(fileName)));
					Element root = document.getDocumentElement();
					
					NodeList fs = root.getElementsByTagName("File");
					for (int i = 0; i < fs.getLength(); i++) {
						oneDataRecord filerecordtemp = new oneDataRecord();
						//依次取每个”file”元素
						Element file = (Element) fs.item(i);
						NodeList filepath = file.getElementsByTagName("filepath");
						//System.out.println(filepath.item(0).getTextContent());
						filerecordtemp.filepath = filepath.item(0).getTextContent();
						
						NodeList filename = file.getElementsByTagName("filename");
						//System.out.println(filename.item(0).getTextContent());
						filerecordtemp.filename = filename.item(0).getTextContent();
						
						NodeList ins = file.getElementsByTagName("ins");
						filerecordtemp.ins = Integer.parseInt(ins.item(0).getTextContent());
						NodeList att = file.getElementsByTagName("att");
						filerecordtemp.att = Integer.parseInt(att.item(0).getTextContent());
						NodeList cla = file.getElementsByTagName("cla");
						filerecordtemp.cla = Integer.parseInt(cla.item(0).getTextContent());
						NodeList progress = file.getElementsByTagName("progress");
						//filerecordtemp.progress = Integer.parseInt(progress.item(0).getTextContent());
						filerecordtemp.progress = 0 ;
						//判断文件是否存在 
						NodeList state = file.getElementsByTagName("state");
						NodeList ischoose = file.getElementsByTagName("ischoose");
						File f = new File(filerecordtemp.filepath);
						if(!f.exists()){
							filerecordtemp.state = 3;
							filerecordtemp.ischoose = false;
						}
						else {
							//filerecordtemp.state = Integer.parseInt(state.item(0).getTextContent());
							filerecordtemp.state = 0 ;
							filerecordtemp.ischoose = ischoose.item(0).getTextContent().equals("Yes")?true:false;
						}
						
						/**********************oneFile**********************/
						
						NodeList baseindex = file.getElementsByTagName("baseindex");
						filerecordtemp.baseindex = Integer.parseInt(baseindex.item(0).getTextContent());
						
						NodeList signifcantlevel = file.getElementsByTagName("signifcantlevel");
						filerecordtemp.signifcantlevel = Double.parseDouble(signifcantlevel.item(0).getTextContent());
						
						NodeList numReduce = file.getElementsByTagName("numReduce");
						filerecordtemp.numReduce = Integer.parseInt(numReduce.item(0).getTextContent());
						NodeList numRun = file.getElementsByTagName("numRun");
						filerecordtemp.numRun = Integer.parseInt(numRun.item(0).getTextContent());
						NodeList numFold = file.getElementsByTagName("numFold");
						filerecordtemp.numFold = Integer.parseInt(numFold.item(0).getTextContent());
						NodeList numcomAlg = file.getElementsByTagName("numcomAlg");
						filerecordtemp.numcomAlg = Integer.parseInt(numcomAlg.item(0).getTextContent());
						int J = filerecordtemp.numcomAlg;
						
						NodeList trainclname = file.getElementsByTagName("trainclname");
						filerecordtemp.trainclname = trainclname.item(0).getTextContent();
						
						NodeList NSindex = file.getElementsByTagName("NSindex");
						filerecordtemp.NSindex = Integer.parseInt(NSindex.item(0).getTextContent());
						
						NodeList NSstyle = file.getElementsByTagName("NSstyle");
						filerecordtemp.NSstyle = xNSstyle.getxNSstyle(NSstyle.item(0).getTextContent());
						NodeList NSmaxIndex = file.getElementsByTagName("NSmaxIndex");
						filerecordtemp.NSmaxIndex = Integer.parseInt(NSmaxIndex.item(0).getTextContent());
						NodeList NSclname = file.getElementsByTagName("NSclname");
						filerecordtemp.NSclname = NSclname.item(0).getTextContent();
						
						NodeList randomI = file.getElementsByTagName("randomI");
						filerecordtemp.randomI = Integer.parseInt(randomI.item(0).getTextContent());
						
						Vector<oneAlgRecord> oneAs = new Vector<oneAlgRecord>();
						NodeList oneAls = root.getElementsByTagName("oneAlgorithm");
						for (int j = 0; j < oneAls.getLength(); j++) {
							
							oneAlgRecord oneAtemp = new oneAlgRecord();
							Element oneAlg = (Element) oneAls.item(j);
							
							NodeList alg_category = oneAlg.getElementsByTagName("alg_category");
							oneAtemp.alg_category = xCategory.getxCategory(alg_category.item(0).getTextContent());
							NodeList alg_style = oneAlg.getElementsByTagName("alg_style");
						 
							oneAtemp.alg_style = xStyle.getxStyle(alg_style.item(0).getTextContent());
							
							NodeList alg_ID = oneAlg.getElementsByTagName("alg_ID");
							oneAtemp.alg_ID = Integer.parseInt(alg_ID.item(0).getTextContent());
							NodeList alg_algname = oneAlg.getElementsByTagName("alg_algname");
							oneAtemp.alg_algname = alg_algname.item(0).getTextContent();
							NodeList alg_alpha = oneAlg.getElementsByTagName("alg_alpha");
							oneAtemp.alg_alpha = Double.parseDouble(alg_alpha.item(0).getTextContent());
							NodeList alg_eval = oneAlg.getElementsByTagName("alg_eval");
							oneAtemp.alg_eval = alg_eval.item(0).getTextContent();
							NodeList alg_search = oneAlg.getElementsByTagName("alg_search");
							oneAtemp.alg_search = alg_search.item(0).getTextContent();
							NodeList alg_flag = oneAlg.getElementsByTagName("alg_flag");
							oneAtemp.alg_flag = alg_flag.item(0).getTextContent().equals("Yes")?true:false;
							NodeList alg_numReduce = oneAlg.getElementsByTagName("alg_numReduce");
							oneAtemp.alg_numReduce = Integer.parseInt(alg_numReduce.item(0).getTextContent());
							NodeList alg_numRun = oneAlg.getElementsByTagName("alg_numRun");
							oneAtemp.alg_numRun = Integer.parseInt(alg_numRun.item(0).getTextContent());
							NodeList alg_clname = oneAlg.getElementsByTagName("alg_clname");
							oneAtemp.alg_clname = alg_clname.item(0).getTextContent();
							NodeList alg_numFold = oneAlg.getElementsByTagName("alg_numFold");
							oneAtemp.alg_numFold = Integer.parseInt(alg_numFold.item(0).getTextContent());	

							oneAs.add(oneAtemp);
						}
						
						filerecordtemp.algs = oneAs;
						/***************************************************/
						res.add(filerecordtemp);
					}
					
					//oneRecord filerecordtemp = new oneRecord();
					/*NodeList infol = document.getChildNodes(); 
					Node info = infol.item(0);
					NodeList files = info.getChildNodes(); 
					for (int i = 0; i < files.getLength(); i++) { 
						oneRecord filerecordtemp = new oneRecord();
						//依次取每个”file”元素
						Node file =  files.item(i); 
						NodeList fileRecordInfo = file.getChildNodes(); 
						System.out.println(fileRecordInfo.getLength());
						//for (int j = 0; j < fileRecordInfo.getLength(); j++) {
							//System.out.println(fileRecordInfo.item(j).getNodeName() + ":" + fileRecordInfo.item(j).getTextContent()+"  k="+j); 
						//}
						//for (int j = 0; j < fileRecordInfo.getLength(); j++) {
							//Element node = (Element)fileRecordInfo.item(j); 
							//System.out.println(node.getAttribute(fileName));
							
						
					 

							/*NodeList z = node.getChildNodes();
							Element student = (Element) z.item(i);

							 for (int k = 1; k < 20; k=k+2) { 
								 System.out.println(z.item(k).getNodeName() + ":" + z.item(k).getTextContent()+"  k="+k); 
								 //System.out.println(z.item(1).getTextContent(););
							 } 
							System.out.println(z.item(0).getTextContent());
							System.out.println(z.item(1).getTextContent());
							System.out.println(z.item(2).getTextContent());
							System.out.println(z.item(3).getTextContent());
							System.out.println(z.item(4).getTextContent());
							System.out.println(z.item(5).getTextContent());*/
							
							/*filerecordtemp.filepath = z.item(1).getTextContent();
							filerecordtemp.filename = z.item(3).getTextContent();
							filerecordtemp.ins = Integer.parseInt(z.item(5).getTextContent());
							filerecordtemp.att = Integer.parseInt(z.item(7).getTextContent());
							filerecordtemp.cla = Integer.parseInt(z.item(9).getTextContent());
							filerecordtemp.progress = Integer.parseInt(z.item(11).getTextContent());						
							//判断文件是否存在 
							File f = new File(filerecordtemp.filepath);
							if(f==null)
								filerecordtemp.state = 3;
							else filerecordtemp.state = Integer.parseInt(z.item(13).getTextContent());
							filerecordtemp.ischoose = z.item(15).getTextContent()=="Yes"?true:false;*/
					
					//System.out.println("解析完毕");
					} 
				catch (FileNotFoundException e) { 
					System.out.println(e.getMessage()); } 
				catch (ParserConfigurationException e) { 
					System.out.println(e.getMessage()); } 
				catch (SAXException e) { System.out.println(e.getMessage()); } 
				catch (IOException e) { System.out.println(e.getMessage()); 
				} 	
			break;
		    }
			default:break;
		}
		return res;
		
		
	
	
}}
