//written by Andre Betz 2006 in Java 1.4.2
//http://www.andrebetz.de

import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import java.io.*;
import javax.swing.filechooser.FileFilter;


public class LoadPoints_plugin implements AlgoGeoMain.PluginInterface {
	private ArrayList m_pnts = null;
	private Component m_parent = null;
	public void Init(Component parent,ArrayList pnts,ArrayList lines, ArrayList circles){
		m_pnts = pnts;
		m_parent = parent;
	}
	public boolean Start(){
		File file = new File(AlgoGeoMain.GetClassPathName()+File.separator+"test.pts");
		if(file.exists() ){
		}else{
			JFileChooser choose = new JFileChooser(AlgoGeoMain.GetClassPathName());
			choose.setDialogTitle("Load AlgoGeo Points File");
			choose.setFileFilter(new FileFilter() {
		        public boolean accept(File f) {
		            return f.getName().toLowerCase().endsWith(".pts") || f.isDirectory();
		        }
		        public String getDescription() {
		            return "AlgoGeo Points(*.pts)";
		        }
		    });
				
		    int returnVal = choose.showOpenDialog(m_parent);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	String fn = choose.getSelectedFile().getAbsolutePath();
		    	file = new File(fn);
				if(!file.exists() ){
					return false;
				}
		    }
		}
		InputStream inputstreamResource = null;
		try {
			inputstreamResource = new FileInputStream(file);
		} catch(Exception ex) {
			return false;
		}

		BufferedReader brFileToBeLoaded = null;
		StringBuffer strPoints = new StringBuffer();
		try {
			int iFileCharacter;
			brFileToBeLoaded = new BufferedReader(new InputStreamReader(inputstreamResource));
			while(true) {
				iFileCharacter = brFileToBeLoaded.read();
				if(iFileCharacter==-1) break;
				strPoints.append((char)iFileCharacter);
			}
		} catch(Exception ex) {
			return false;
		} finally {
			try {
				if(brFileToBeLoaded!=null) brFileToBeLoaded.close();
			} catch(Exception ex) {
				return false;
			}
		}
		if(strPoints.length()>0){
			char[] chPts = strPoints.toString().toCharArray();
			char[] EndSym = {'\n','\t',' ','\r'};
			char[] digits ={'0','1','2','3','4','5','6','7','8','9'};
			int len = chPts.length;
			int actPos = 0;
			int state = 0;
			String X_cord = "";
			String Y_cord = "";			
			while(actPos<len){
				actPos = DelNoSigns(chPts,actPos,EndSym);
				switch(state){
				case 0:
					state = 1;
					break;
				case 1:
					if(chPts[actPos]=='('){
						state = 2;
						actPos++;
						X_cord = "";
					}else{
						state = 99;
					}
					break;
				case 2:
    				while(actPos<len&&DoesContain(chPts[actPos],digits)>=0){
    					X_cord += chPts[actPos];
    					actPos++;
    				}
    				state = 3;
					break;
				case 3:
					if(chPts[actPos]==','){
						state = 4;
						actPos++;
						Y_cord = "";
					}else{
						state = 99;
					}
					break;
				case 4:
    				while(actPos<len&&DoesContain(chPts[actPos],digits)>=0){
    					Y_cord += chPts[actPos];
    					actPos++;
    				}
    				state = 5;
					break;
				case 5:
					if(chPts[actPos]==')'){
						state = 0;
						actPos++;
						int xc = Integer.parseInt(X_cord);
						int yc = Integer.parseInt(Y_cord);
						m_pnts.add(new AlgoGeoMain.MyPoint(xc,yc));
					}else{
						state = 99;
					}
					break;
				default:
					return false;
				}
			}
		}
		return true;
	}
	private int DelNoSigns(char[] Input, int spos, char[] NoSigns) {
		int newpos = spos;
		if(NoSigns==null){
			return spos;
		}
		int slen = Input.length;
		if(newpos<slen) {
			char sign = Input[newpos];
			while((DoesContain(sign,NoSigns)>=0)&&(newpos<slen)) {
				newpos++;
				if(newpos<slen) {
					sign = Input[newpos];
				}
			}
		}
		return newpos;
	}
	private int DoesContain(char Sign, char[] SignList){
		int count = 0;
		while(count<SignList.length){
			if(Sign == SignList[count]){
				return count;	
			}
			count++;
		}
		return -1;
	}
}
