import java.awt.Component;
import java.util.ArrayList;

//written by Andre Betz 2006 in Java 1.4.2
//http://www.andrebetz.de

// berechnet den Schnittpunkt von Linien mit dem Plane-Sweep Algorithmus

public class Schnittpunkt_plugin implements AlgoGeoMain.PluginInterface {
	private ArrayList m_lines = null;
	private ArrayList m_circles = null;
	private AVLTree m_Schedule = null;
	private double m_delta = 0.00001;
	
	public static class SortXY extends AVLTree.Compare{
		public int DoCompare(Object x1,Object x2){
			AlgoGeoMain.MyPoint p1 = (AlgoGeoMain.MyPoint)x1;
			AlgoGeoMain.MyPoint p2 = (AlgoGeoMain.MyPoint)x2;
			if(p1.x > p2.x) {
				return 1;
			} else if (p1.x < p2.x) {
				return -1;
			} else {
				if (p1.y > p2.y)  {
					return 1;
				} else if (p1.y < p2.y) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	}
	public static class SortY extends AVLTree.Compare{
		public int DoCompare(Object x1,Object x2){
			AlgoGeoMain.MyPoint p1 = (AlgoGeoMain.MyPoint)x1;
			AlgoGeoMain.MyPoint p2 = (AlgoGeoMain.MyPoint)x2;
			if(p1.y > p2.y) {
				return 1;
			} else if (p1.y < p2.y) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	public static class LineAVLElm extends AVLTree.NodeElm {
		private LineAVLElm m_OtherSite = null;
		private boolean m_EndPunkt = false;
		private LineAVLElm m_CutLine1 = null;
		private LineAVLElm m_CutLine2 = null;
		private LineAVLElm m_StatusList = null;
		
		public LineAVLElm getM_StatusList() {
			return m_StatusList;
		}
		public void setM_StatusList(LineAVLElm statusList) {
			m_StatusList = statusList;
		}
		public LineAVLElm getM_CutLine1() {
			return m_CutLine1;
		}
		public void setM_CutLine1(LineAVLElm cutLine1) {
			m_CutLine1 = cutLine1;
		}
		public LineAVLElm getM_CutLine2() {
			return m_CutLine2;
		}
		public void setM_CutLine2(LineAVLElm cutLine2) {
			m_CutLine2 = cutLine2;
		}
		public boolean IsCutPoint(){
			return (m_OtherSite==null ? true : false);
		}
		public String ToString(Object o){
			AlgoGeoMain.MyPoint p1 = (AlgoGeoMain.MyPoint)o;
			return "(X:"+p1.x+" Y:"+p1.y+")";
		}
		public AlgoGeoMain.MyPoint GetThisPoint(){
			return (AlgoGeoMain.MyPoint)m_NodeObj;
		}
		public LineAVLElm GetOtherSite(){
			return m_OtherSite;
		}
		public void SetOtherSite(LineAVLElm OtherSite){
			m_OtherSite = OtherSite;
		}	
		public boolean IsEndPoint(){
			return m_EndPunkt;
		}
		public AlgoGeoMain.MyPoint GetStartPoint(){
			if(!m_EndPunkt){
				return GetThisPoint();
			}else{
				return GetOtherSite().GetThisPoint();
			}
		}
		public AlgoGeoMain.MyPoint GetEndPoint(){
			if(m_EndPunkt){
				return GetThisPoint();
			}else{
				return GetOtherSite().GetThisPoint();
			}
		}

		public boolean LineCut(LineAVLElm ln){
			if(ln!=null){
				AlgoGeoMain.MyPoint r1 = ln.GetStartPoint();
				AlgoGeoMain.MyPoint r2 = ln.GetEndPoint();
				AlgoGeoMain.MyPoint p = GetThisPoint();
				AlgoGeoMain.MyPoint q = GetOtherSite().GetThisPoint();
				
				AlgoGeoMain.MyLine ml1 = new AlgoGeoMain.MyLine(p,q); 
				AlgoGeoMain.MyLine ml2 = new AlgoGeoMain.MyLine(r1,r2); 

				return ml1.Cut(ml2);
			}
			return false;
		}
		public AlgoGeoMain.MyPoint GetCutPoint(LineAVLElm ln){
			double m1 = GetGradient();
			double m2 = ln.GetGradient();
			double t1 = GetOffset();
			double t2 = ln.GetOffset();
			double CutX = (t2 - t1) / (m1 - m2);
			double CutY = CutX * m1 + t1;
			return new AlgoGeoMain.MyPoint(CutX,CutY);
		}
		private double GetGradient(){
			AlgoGeoMain.MyPoint p1 = GetStartPoint();
			AlgoGeoMain.MyPoint p2 = GetEndPoint();
			if(p1.x==p2.x){
				return 0.0;
			}
			return (p1.y-p2.y)/(p1.x-p2.x);
		}
		private double GetOffset(){
			return GetStartPoint().y-GetStartPoint().x * GetGradient();
		}
		
		public String ToString(){
			AlgoGeoMain.MyPoint p1 = (AlgoGeoMain.MyPoint)m_NodeObj;
			double X = p1.x;
			double Y = p1.y;
			String str = "";
			str += "(X:"+X+" Y:"+Y+")";
			return str;
		}
		public LineAVLElm(AlgoGeoMain.MyPoint p1,AlgoGeoMain.MyPoint p2){
			super(p1);
			if(p1.x>p2.x){
				m_EndPunkt = true;
			}else{
				m_EndPunkt = false;				
			}
		}
		public LineAVLElm(AlgoGeoMain.MyPoint p,LineAVLElm connect){
			super(p);
			m_OtherSite = connect;
		}
		public LineAVLElm(AlgoGeoMain.MyPoint p,LineAVLElm last,LineAVLElm next){
			super(p);
			m_CutLine1 = last;
			m_CutLine2 = next;
		}
	}
	
	public void Init(Component parent,ArrayList pnts,ArrayList lines, ArrayList circles){
		m_lines = lines;
		m_circles = circles;
		m_Schedule = new AVLTree(new SortXY());
	}
	public boolean Start(){
		AVLTree Status = new AVLTree(new SortY());
		m_circles.clear();
		m_Schedule.clear();
		for(int i=0;i<m_lines.size();i++){
			AlgoGeoMain.MyLine ml = (AlgoGeoMain.MyLine)m_lines.get(i);
			LineAVLElm LineElm1 = new LineAVLElm(ml.getP1(),ml.getP2());
			LineAVLElm LineElm2 = new LineAVLElm(ml.getP2(),ml.getP1());
			LineElm1.SetOtherSite(LineElm2);
			LineElm2.SetOtherSite(LineElm1);
			m_Schedule.Insert(LineElm1);
			m_Schedule.Insert(LineElm2);
		}
		LineAVLElm iterator = (LineAVLElm)m_Schedule.GetFirst();
		
		while(iterator!=null){
			if(iterator.IsCutPoint()){
				// Startpunkte der Schnittlinien holen
				LineAVLElm lst = iterator.getM_CutLine1().GetOtherSite().getM_StatusList();
				LineAVLElm nxt = iterator.getM_CutLine2().GetOtherSite().getM_StatusList();				
				// Punkte darin an Schnittpunkt anpassen und Reihenfolge tauschen				
				LineAVLElm LineLst = new LineAVLElm(new AlgoGeoMain.MyPoint(iterator.GetStartPoint().x,iterator.GetStartPoint().y+m_delta),lst.GetOtherSite());
				LineAVLElm LineNxt = new LineAVLElm(new AlgoGeoMain.MyPoint(iterator.GetStartPoint().x,iterator.GetStartPoint().y-m_delta),nxt.GetOtherSite());
				// Neue Punktreihenfolge in Statusliste einfuegen
				lst.GetOtherSite().setM_StatusList(LineLst);
				nxt.GetOtherSite().setM_StatusList(LineNxt);
				Status.Insert(LineLst);
				Status.Insert(LineNxt);
				// Alte Startpunkte in Status löschen
				lst.Delete();
				nxt.Delete();
				// Nachfolger und Vorgaenger der Punkte besorgen
				LineAVLElm LineOverNxt = (LineAVLElm)Status.GetLast(LineNxt);
				LineAVLElm LineOverLst = (LineAVLElm)Status.GetNext(LineLst);
				// Beide auf Schnitt an neuen Positionen testen
				if(LineOverNxt!=null){
					if(LineOverNxt.GetOtherSite().LineCut(LineNxt.GetOtherSite())){
						// Schnittpunkt in Schedule einfuegen
						AlgoGeoMain.MyPoint lc = LineOverNxt.GetOtherSite().GetCutPoint(LineNxt.GetOtherSite());
						LineAVLElm CutElm = new LineAVLElm(lc,LineOverNxt,LineNxt);
						m_Schedule.Insert(CutElm);
					}
				}
				if(LineOverLst!=null){
					if(LineOverLst.GetOtherSite().LineCut(LineLst.GetOtherSite())){
						// Schnittpunkt in Schedule einfuegen
						AlgoGeoMain.MyPoint lc = LineOverLst.GetOtherSite().GetCutPoint(LineLst.GetOtherSite());
						LineAVLElm CutElm = new LineAVLElm(lc,LineLst,LineOverLst);
						m_Schedule.Insert(CutElm);
					}					
				}
				// Schneidpunkt in Markierung aufnehmen
				m_circles.add(new AlgoGeoMain.MyPoint(iterator.GetStartPoint()));
			}else{
				if(iterator.IsEndPoint()){
					// StartPunkt holen
					LineAVLElm StartPnt = iterator.GetOtherSite();
					// StartPunkt aus der StatusListe holen
					LineAVLElm StatusPnt = StartPnt.getM_StatusList();
					// Vorgaenger und Nachfolger holen
					LineAVLElm nxt = (LineAVLElm)Status.GetLast(StatusPnt);				
					LineAVLElm lst = (LineAVLElm)Status.GetNext(StatusPnt);
					// Testen auf Schnitt von Vorggaenger und Nachfolger
					if(nxt!=null && lst!=null){
						if(nxt.GetOtherSite().LineCut(lst.GetOtherSite())){
							// Schnittpunkt in Schedule einfuegen
							AlgoGeoMain.MyPoint lc = nxt.GetOtherSite().GetCutPoint(lst.GetOtherSite());
							LineAVLElm CutElm = new LineAVLElm(lc,lst,nxt);
							m_Schedule.Insert(CutElm);
						}
					}
					// Startpunkt aus der StatusListe löschen
					StatusPnt.Delete();
				}else{
					// neuen Punkt für Statusliste erzeugen
					LineAVLElm LinePnt = new LineAVLElm(new AlgoGeoMain.MyPoint(iterator.GetStartPoint()),iterator);
					// Schedule und Status verbinden
					iterator.setM_StatusList(LinePnt);
					// In StatusListe einfuegen
					Status.Insert(LinePnt);
					// Vorgaenger und Nachfolger des Startpunktes besorgen
					LineAVLElm nxt = (LineAVLElm)Status.GetNext(LinePnt);				
					LineAVLElm lst = (LineAVLElm)Status.GetLast(LinePnt);
					// Testen auf Schnitt
					if(nxt!=null){
						if(iterator.LineCut(nxt.GetOtherSite())){
							// Schnittpunkt in Schedule einfuegen
							AlgoGeoMain.MyPoint lc = iterator.GetCutPoint(nxt.GetOtherSite());
							LineAVLElm CutElm = new LineAVLElm(lc,iterator.getM_StatusList(),nxt);
							m_Schedule.Insert(CutElm);
						}
					}
					if(lst!=null){
						if(iterator.LineCut(lst.GetOtherSite())){
							// Schnittpunkt in Schedule einfuegen
							AlgoGeoMain.MyPoint lc = iterator.GetCutPoint(lst.GetOtherSite());
							LineAVLElm CutElm = new LineAVLElm(lc,lst,iterator.getM_StatusList());
							m_Schedule.Insert(CutElm);
						}					
					}
				}
			}
			iterator = (LineAVLElm)m_Schedule.GetNext(iterator);
		}
		return true;
	}
}
