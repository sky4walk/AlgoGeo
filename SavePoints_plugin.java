//written by Andre Betz 2006 in Java 1.4.2
//http://www.andrebetz.de

import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.*;

import javax.swing.filechooser.FileFilter;

public class SavePoints_plugin implements AlgoGeoMain.PluginInterface {
	private ArrayList m_pnts = null;
	private Component m_parent = null;
	public void Init(Component parent,ArrayList pnts,ArrayList lines, ArrayList circles){
		m_pnts = pnts;
		m_parent = parent;
	}
	public boolean Start(){
		JFileChooser choose = new JFileChooser(AlgoGeoMain.GetClassPathName());
		choose.setDialogTitle("Save AlgoGeo Points File");
		choose.setFileFilter(new FileFilter() {
	        public boolean accept(File f) {
	            return f.getName().toLowerCase().endsWith(".pts") || f.isDirectory();
	        }
	        public String getDescription() {
	            return "AlgoGeo Points(*.pts)";
	        }
	    });
		int returnVal = choose.showSaveDialog(m_parent);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	String fn = choose.getSelectedFile().getAbsolutePath();
	    	
	    	
	    	File file = new File(fn);
	    	if(!file.getName().toLowerCase().endsWith(".pts")){
	    		fn += ".pts";
	    		file = new File(fn);
	    	}
	    	
	    	if(file.exists() ){
	    		int ret =JOptionPane.showConfirmDialog(m_parent,"File "+fn+" exists!\nOverwrite it ?", "File Exist Warning!", JOptionPane.YES_NO_OPTION);
	    		if(ret!=JOptionPane.YES_OPTION){
	    			return false;
	    		}
			}
	    	
	    	FileOutputStream outputstreamResource = null;
			try {
				outputstreamResource = new FileOutputStream(file);
			} catch(Exception ex) {
				return false;
			}
			
			BufferedWriter brFileToBeSaved = null;
			try {
				brFileToBeSaved = new BufferedWriter(new OutputStreamWriter(outputstreamResource));
				String strPts = Points2String();
				brFileToBeSaved.write(strPts);
			} catch(Exception ex) {
				return false;
			} finally {
				try {
					if(brFileToBeSaved!=null) brFileToBeSaved.close();
				} catch(Exception ex) {
					return false;
				}
			}
	    }
		return true;
	}
	
	private String Points2String(){
		StringBuffer strPts = new StringBuffer();
		if(m_pnts!=null){
			for(int i=0;i<m_pnts.size();i++){
				AlgoGeoMain.MyPoint mp = (AlgoGeoMain.MyPoint)m_pnts.get(i);
				strPts.append("("+(int)mp.x+","+(int)mp.y+")\r\n");
			}
		}
		return strPts.toString();
	}
}
