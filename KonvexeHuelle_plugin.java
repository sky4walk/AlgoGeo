//written by Andre Betz 2006 in Java 1.4.2
//http://www.andrebetz.de

import java.awt.Component;
import java.util.ArrayList;

public class KonvexeHuelle_plugin implements AlgoGeoMain.PluginInterface {
	private ArrayList m_pnts = null;
	private ArrayList m_lns = null;
	
	public void Init(Component parent,ArrayList pnts,ArrayList lines, ArrayList circles){
		m_pnts = pnts;
		m_lns = lines;
	}
	public boolean Start(){
		if(m_pnts.size()>1){
			m_lns.clear();
			
			myQuickSort qs = new myQuickSort(new Schnittpunkt_plugin.SortXY());
			Object[] ol = m_pnts.toArray();
			qs.Sort(ol);
			
			ArrayList FeldOben = new ArrayList();
			ArrayList FeldUnten = new ArrayList();
			
			FeldOben.add(ol[0]);
			FeldOben.add(ol[1]);
			FeldUnten.add(ol[ol.length-1]);
			FeldUnten.add(ol[ol.length-2]);
			
			// untersucht obere Hälfte
			for(int i=2;i<ol.length;i++){
				FeldOben.add(ol[i]);
	            int pos = FeldOben.size()-1;
	            
	            while((pos>=2) && (!isRightTurn((AlgoGeoMain.MyPoint)FeldOben.get(pos-2),(AlgoGeoMain.MyPoint)FeldOben.get(pos-1),(AlgoGeoMain.MyPoint)FeldOben.get(pos)))){
	                pos--;
	                FeldOben.remove(pos);
	            }            
	        }
			// untersucht untere Hälfte
			for(int i=ol.length-3;i>=0;i--){
				FeldUnten.add(ol[i]);
	            int pos = FeldUnten.size()-1;
	            
	            while((pos>=2) && (!isRightTurn((AlgoGeoMain.MyPoint)FeldUnten.get(pos-2),(AlgoGeoMain.MyPoint)FeldUnten.get(pos-1),(AlgoGeoMain.MyPoint)FeldUnten.get(pos)))){
	                pos--;
	                FeldUnten.remove(pos);
	            }            
	        }
			
			for(int i=1;i<FeldOben.size();i++){
				AlgoGeoMain.MyPoint pa = (AlgoGeoMain.MyPoint)FeldOben.get(i-1);
				AlgoGeoMain.MyPoint pb = (AlgoGeoMain.MyPoint)FeldOben.get(i);
		        AlgoGeoMain.MyLine ml = new AlgoGeoMain.MyLine(pa,pb);
				m_lns.add(ml);				
			}
			for(int i=1;i<FeldUnten.size();i++){
				AlgoGeoMain.MyPoint pa = (AlgoGeoMain.MyPoint)FeldUnten.get(i-1);
				AlgoGeoMain.MyPoint pb = (AlgoGeoMain.MyPoint)FeldUnten.get(i);
		        AlgoGeoMain.MyLine ml = new AlgoGeoMain.MyLine(pa,pb);
				m_lns.add(ml);				
				
			}
		}		
		return true;
	}
	
	public boolean isRightTurn(AlgoGeoMain.MyPoint p,AlgoGeoMain.MyPoint q,AlgoGeoMain.MyPoint r) {
		// Determinante der Dreiecksgleichung, liegt punkt links/rechts 
		double Det = (q.x-p.x)*(q.y+p.y) + (r.x-q.x)*(r.y+q.y) + (p.x-r.x)*(p.y+r.y);        
        if(Det>0){
        	return true;
        }else{
        	return false; 
        }
    }
}
