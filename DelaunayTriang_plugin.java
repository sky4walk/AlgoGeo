import java.awt.Component;
import java.util.ArrayList;
import java.util.Random;

//written by Andre Betz 2006 in Java 1.4.2
//http://www.andrebetz.de

public class DelaunayTriang_plugin implements AlgoGeoMain.PluginInterface {
	private ArrayList m_pnts = null;
	private ArrayList m_lns = null;

	public static class ConnectedLine extends AlgoGeoMain.MyLine {
		private DelaunayDAG m_Left = null;
		private DelaunayDAG m_Right = null;
		private boolean m_NotUsed = false;
		
		ConnectedLine(AlgoGeoMain.MyPoint p1,AlgoGeoMain.MyPoint p2){
			super(p1,p2);
		}
		public boolean IsConnected(ConnectedLine l){
			if(Equals(l)){
				return false;
			}else if(l.Equals(this.getP1())){
				return true;
			}else if(l.Equals(this.getP2())){
				return true;
			}else{
				return false;
			}
		}
		public DelaunayDAG getM_Left() {
			return m_Left;
		}

		public void setM_Left(DelaunayDAG left) {
			m_Left = left;
		}

		public DelaunayDAG getM_Right() {
			return m_Right;
		}

		public void setM_Right(DelaunayDAG right) {
			m_Right = right;
		}
		public boolean IsLeft(DelaunayDAG d){
			if(m_Left!=null){
				if(d.GetSimplex().Equals(m_Left.GetSimplex())){
					return true;
				}
			}
			return false;
		}
		public boolean IsUsed(){
			return !m_NotUsed;
		}
		public void UnUsed(){
			m_NotUsed = true;
		}
	}
	public static class DelaunayDAG{
		MySimplex m_ms = null;
		ArrayList m_Father = new ArrayList();
		ArrayList m_Child = new ArrayList();
		public DelaunayDAG GetFather(int i){
			if(i<m_Father.size()){
				return (DelaunayDAG)m_Father.get(i);
			}
			return null;
		}
		public void AddFather(DelaunayDAG d){
			m_Father.add(d);
		}
		public DelaunayDAG GetChild(int i) {
			if(i<m_Child.size()){
				return (DelaunayDAG)m_Child.get(i);
			}
			return null;
		}
		public int ChildSize(){
			return m_Child.size();
		}
		public int FatherSize(){
			return m_Father.size();
		}
		public MySimplex GetSimplex() {
			return m_ms;
		}
		public DelaunayDAG(MySimplex ms){
			m_ms = ms;
		}
		public void AddChilds(ArrayList n){
			for(int i=0;i<n.size();i++){
				DelaunayDAG d = (DelaunayDAG)n.get(i);
				d.AddFather(this);
				m_Child.add(d);
			}
		}
	}
	
	public static class MySimplex{
		AlgoGeoMain.MyPoint m_p1;
		AlgoGeoMain.MyPoint m_p2;
		AlgoGeoMain.MyPoint m_p3;
		ConnectedLine m_l1;
		ConnectedLine m_l2;
		ConnectedLine m_l3;

		MySimplex(AlgoGeoMain.MyPoint p1,AlgoGeoMain.MyPoint p2, AlgoGeoMain.MyPoint p3){
			m_p1 = p1;
			m_p2 = p2;
			m_p3 = p3;
			m_l1 = new ConnectedLine(m_p1,m_p2);
			m_l2 = new ConnectedLine(m_p2,m_p3);
			m_l3 = new ConnectedLine(m_p3,m_p1);
		}
		MySimplex(ConnectedLine l1,ConnectedLine l2, ConnectedLine l3){
			m_l1 = l1;
			m_l2 = l2;
			m_l3 = l3;
			m_p1 = m_l1.getP1();
			m_p2 = m_l1.getP2();
			if(m_l1.Equals(m_l2.getP1())){
				m_p3 = m_l2.getP2();				
			}else{
				m_p3 = m_l2.getP1();				
			}
		}
		public AlgoGeoMain.MyPoint GetP1(){
			return m_p1;
		}
		public AlgoGeoMain.MyPoint GetP2(){
			return m_p2;
		}
		public AlgoGeoMain.MyPoint GetP3(){
			return m_p3;
		}
		public ConnectedLine GetConnectedLine(AlgoGeoMain.MyPoint a,AlgoGeoMain.MyPoint b){
			AlgoGeoMain.MyLine l = new AlgoGeoMain.MyLine(a,b);
			if(l.Equals(m_l1)){
				return m_l1;
			}else if(l.Equals(m_l2)){
				return m_l2;				
			}else if(l.Equals(m_l3)){
				return m_l3;				
			}else{
				return null;
			}
		}
		public ConnectedLine getM_l1() {
			return m_l1;
		}
		public ConnectedLine getM_l2() {
			return m_l2;
		}
		public ConnectedLine getM_l3() {
			return m_l3;
		}
		public boolean Equals(AlgoGeoMain.MyPoint p){
			if(m_p1.x==p.x&&m_p1.y==p.y){
				return true;
			}else if(m_p2.x==p.x&&m_p2.y==p.y){
				return true;
			}else if(m_p3.x==p.x&&m_p3.y==p.y){
				return true;
			}else{
				return false;
			}
		}
		public AlgoGeoMain.MyPoint GetOppositePoint(ConnectedLine l){
			if(Equals(l)){
				if(l.Equals(m_p1)&&l.Equals(m_p2)){
					return m_p3;
				}else if(l.Equals(m_p1)&&l.Equals(m_p3)){
					return m_p2;
				}else if(l.Equals(m_p2)&&l.Equals(m_p3)){
					return m_p1;
				}
			}
			return null;			
		}
		public boolean Equals(ConnectedLine l){
			if(l.Equals(m_l1)){
				return true;
			}else if(l.Equals(m_l2)){
				return true;
			}else if(l.Equals(m_l3)){
				return true;
			}
			return false;
		}	
		
		public boolean Equals(MySimplex s){
			if(s.Equals(m_l1)&&s.Equals(m_l2)&&s.Equals(m_l3)){
				return true;
			}
			return false;
		}
		public ConnectedLine GetConnectedLine(MySimplex s){
			ConnectedLine cl = null;
			if(Equals(s.getM_l1())){
				cl = s.getM_l1();
			}
			else if(Equals(s.getM_l2())){
				cl = s.getM_l2();			
			}
			else if(Equals(s.getM_l3())){
				cl = s.getM_l3();
			}else{
			}
			return cl;
		}
		public boolean IsConnected(MySimplex s){
			if(!Equals(s)){
				if(s.Equals(m_l1)||s.Equals(m_l2)||s.Equals(m_l3)){
					return true;
				}
			}
			return false;
		}
		public boolean InsideTriangle(AlgoGeoMain.MyPoint p){
			double x1 = m_p1.x;
			double y1 = m_p1.y;
			double x2 = m_p2.x;
			double y2 = m_p2.y;
			double x3 = m_p3.x;
			double y3 = m_p3.y;
			double D,G;
		        
			D = (x1-x2)*(y3-p.y)-(y1-y2)*(x3-p.x);
			G = (x1-x3)*(y3-p.y)-(y1-y3)*(x3-p.x);
			if(D>0&&(G<=0||G>=D)) return false;
			if(D<0&&(G>=0||G<=D)) return false;
			if(D==0)              return false;
		        
			D = (x2-x3)*(y1-p.y)-(y2-y3)*(x1-p.x);
			G = (x2-x1)*(y1-p.y)-(y2-y1)*(x1-p.x);
			if(D>0&&(G<=0||G>=D)) return false;
			if(D<0&&(G>=0||G<=D)) return false;
			if(D==0)              return false;
		        
			return true;        
		}
		
		//		 berechnet den Umkreismittelpunkt eines Dreieckes
		private AlgoGeoMain.MyPoint GetUmkreismittelpunkt(){
			double dyPQ = m_p1.y - m_p2.y;
			double dxPQ = m_p2.x - m_p1.x;
			double dyQR = m_p2.y - m_p3.y;
			double dxQR = m_p3.x - m_p2.x;
			double syPQ = m_p1.y + m_p2.y;
			double sxPQ = m_p1.x + m_p2.x;
			double syQR = m_p2.y + m_p3.y;
			double sxQR = m_p2.x + m_p3.x;
			double c1 = 0.5 * (syPQ * dyPQ - sxPQ * dxPQ);
			double c2 = 0.5 * (syQR * dyQR - sxQR * dxQR);
			double x = (c1* dyQR - c2 * dyPQ) / (dyPQ * dxQR - dyQR * dxPQ); 
			double y = (c1 + dxPQ * x) / dyPQ;	
			return new AlgoGeoMain.MyPoint(x,y);
		}

		private double GetAbstand(AlgoGeoMain.MyPoint a,AlgoGeoMain.MyPoint b){
			return Math.sqrt((a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y));
		}
		
		public boolean IsPointDelaunayLegal(AlgoGeoMain.MyPoint p){
			AlgoGeoMain.MyPoint um = GetUmkreismittelpunkt();
			double r = GetAbstand(um,m_l1.getP1());
			double dp = GetAbstand(um,p);
			if(dp>r){
				return true;
			}else{
				return false;
			}
		}
	}
	
	public void Init(Component parent,ArrayList pnts,ArrayList lines, ArrayList circles){
		m_pnts = pnts;
		m_lns = lines;
	}
	
	public boolean Start(){
		m_lns.clear();
//		ArrayList pnts = RandomizePntsSeries();
		ArrayList pnts = m_pnts;
		DelaunayDAG root_dag = GetGreatSimplex();
		for(int i=0;i<pnts.size();i++){
			AlgoGeoMain.MyPoint p = (AlgoGeoMain.MyPoint)pnts.get(i);
			DelaunayDAG d = FindPointInSimplex(p,root_dag);
			ArrayList dagChilds = CreateNewSimplex(d,p);
			d.AddChilds(dagChilds);
			
			for(int j=0;j<dagChilds.size();j++){
				DelaunayDAG s = (DelaunayDAG)dagChilds.get(j);
				Legalize(s);
			}
			
		}
		DrawSimplex(root_dag,root_dag);
		return true;
	}
	private boolean IsLegal(ConnectedLine l){
		DelaunayDAG d1 = l.getM_Left();
		DelaunayDAG d2 = l.getM_Right();
		if(d1!=null && d2!=null){
			AlgoGeoMain.MyPoint op = d1.GetSimplex().GetOppositePoint(l);
			if(!d2.GetSimplex().IsPointDelaunayLegal(op)){
				return false;
			}
		}
		return true;
	}
	private void Legalize(DelaunayDAG s){
		ArrayList connected = GetConnected(s);
		for(int i=0;i<connected.size();i++){
			ConnectedLine l = (ConnectedLine)connected.get(i);
			if(!IsLegal(l)&&l.IsUsed()){
				DelaunayDAG d1 = l.getM_Left();
				DelaunayDAG d2 = l.getM_Right();				
				ArrayList flipped = FlipSimplex(d1,d2);
				d1.AddChilds(flipped);
				d2.AddChilds(flipped);
				for(int j=0;j<flipped.size();j++){
					DelaunayDAG d = (DelaunayDAG)flipped.get(j);
					Legalize(d);
				}
			}
		}
	}
	
	private ArrayList FlipSimplex(DelaunayDAG s1,DelaunayDAG s2){
		ArrayList sLst = new ArrayList();
		ConnectedLine cl = s1.GetSimplex().GetConnectedLine(s2.GetSimplex());
		if(cl!=null){
			AlgoGeoMain.MyPoint p1 = s1.GetSimplex().GetOppositePoint(cl);
			AlgoGeoMain.MyPoint p2 = s2.GetSimplex().GetOppositePoint(cl);
			ConnectedLine l = new ConnectedLine(p1,p2);
			cl.UnUsed();
			
			ConnectedLine e1 = s1.GetSimplex().GetConnectedLine(p1,cl.getP1());
			ConnectedLine e2 = s1.GetSimplex().GetConnectedLine(p1,cl.getP2());
			ConnectedLine e3 = s2.GetSimplex().GetConnectedLine(p2,cl.getP1());
			ConnectedLine e4 = s2.GetSimplex().GetConnectedLine(p2,cl.getP2());
			
			if(e1.IsConnected(e4)){
				ConnectedLine t = e4;
				e4 = e3;
				e3 = t;
			}
			
			DelaunayDAG d1 = new DelaunayDAG(new MySimplex(e1,e3,l));
			DelaunayDAG d2 = new DelaunayDAG(new MySimplex(e2,e4,l));
			
			l.setM_Left(d1);
			l.setM_Right(d2);
			if(e1.IsLeft(s1)){
				e1.setM_Left(d1);
			}else{
				e1.setM_Right(d1);
			}
			if(e2.IsLeft(s1)){
				e2.setM_Left(d2);
			}else{
				e2.setM_Right(d2);
			}
			if(e3.IsLeft(s2)){
				e3.setM_Left(d1);
			}else{
				e3.setM_Right(d1);
			}
			if(e4.IsLeft(s2)){
				e4.setM_Left(d2);
			}else{
				e4.setM_Right(d2);
			}
			
			sLst.add(d1);
			sLst.add(d2);
		}
		return sLst;
	}

	private ArrayList CreateNewSimplex(DelaunayDAG s, AlgoGeoMain.MyPoint p){
		ArrayList simplices = new ArrayList();
		if(s.GetSimplex().InsideTriangle(p)){
			ConnectedLine l1 = new ConnectedLine(s.GetSimplex().GetP1(),p);
			ConnectedLine l2 = new ConnectedLine(s.GetSimplex().GetP2(),p);
			ConnectedLine l3 = new ConnectedLine(s.GetSimplex().GetP3(),p);
			
			ConnectedLine e1 = s.GetSimplex().GetConnectedLine(s.GetSimplex().GetP1(),s.GetSimplex().GetP2());
			ConnectedLine e2 = s.GetSimplex().GetConnectedLine(s.GetSimplex().GetP2(),s.GetSimplex().GetP3());
			ConnectedLine e3 = s.GetSimplex().GetConnectedLine(s.GetSimplex().GetP3(),s.GetSimplex().GetP1());
			
			DelaunayDAG d1 = new DelaunayDAG(new MySimplex(l1,l2,e1));
			DelaunayDAG d2 = new DelaunayDAG(new MySimplex(l2,l3,e2));
			DelaunayDAG d3 = new DelaunayDAG(new MySimplex(l3,l1,e3));
			
			l1.setM_Left(d1);
			l1.setM_Right(d3);
			l2.setM_Left(d1);
			l2.setM_Right(d2);
			l3.setM_Left(d2);
			l3.setM_Right(d3);
			
			if(e1.IsLeft(s)){
				e1.setM_Left(d1);
			}else{
				e1.setM_Right(d1);				
			}
			if(e2.IsLeft(s)){
				e2.setM_Left(d2);
			}else{
				e2.setM_Right(d2);				
			}
			if(e3.IsLeft(s)){
				e3.setM_Left(d3);
			}else{
				e3.setM_Right(d3);				
			}
			
			simplices.add(d1);
			simplices.add(d2);
			simplices.add(d3);
		}
		return simplices;
	}
	
	private void DrawSimplex(DelaunayDAG dag,DelaunayDAG root){
		if(dag.ChildSize()>0){
			for(int i=0;i<dag.ChildSize();i++){
				DelaunayDAG child_dag = dag.GetChild(i);
				DrawSimplex(child_dag,root);
			}
		}else{
			MySimplex s = dag.GetSimplex();
			MySimplex rs = root.GetSimplex();
			if(rs.Equals(s.GetP1())||rs.Equals(s.GetP2())||rs.Equals(s.GetP3())){	
			}else{
				DrawSimplex(dag.GetSimplex());
			}
		}
	}
	
	private void DrawSimplex(MySimplex s){
		m_lns.add(new AlgoGeoMain.MyLine(s.m_p1,s.m_p2));
		m_lns.add(new AlgoGeoMain.MyLine(s.m_p2,s.m_p3));
		m_lns.add(new AlgoGeoMain.MyLine(s.m_p3,s.m_p1));
	}
	
	private int FindChildDag(AlgoGeoMain.MyPoint pnt,DelaunayDAG father_dag){
		for(int i=0;i<father_dag.ChildSize();i++){
			DelaunayDAG d = father_dag.GetChild(i);
			if(d.GetSimplex().InsideTriangle(pnt)){
				return i;
			}
		}
		return -1;
	}
	
	private DelaunayDAG FindPointInSimplex(AlgoGeoMain.MyPoint pnt,DelaunayDAG root_dag){
		DelaunayDAG dag = null;
		int pos = FindChildDag(pnt,root_dag);
		if(pos<0){
			dag = root_dag;
		}else{
			DelaunayDAG child_dag = root_dag.GetChild(pos);
			dag =  FindPointInSimplex(pnt,child_dag);
		}
		return dag;
	}
	
	private ArrayList GetConnected(DelaunayDAG s){
		ArrayList ngbrs = new ArrayList();
		ConnectedLine l1 = s.GetSimplex().getM_l1();
		ConnectedLine l2 = s.GetSimplex().getM_l2();
		ConnectedLine l3 = s.GetSimplex().getM_l3();
		if(l1.IsUsed()){
			ngbrs.add(l1);
		}
		if(l2.IsUsed()){
			ngbrs.add(l2);
		}
		if(l3.IsUsed()){
			ngbrs.add(l3);
		}		
		return ngbrs;
	}
	
	private ArrayList RandomizePntsSeries(){
		Random generator = new Random();
		ArrayList pnts = (ArrayList)m_pnts.clone();
		for(int x=0;x<pnts.size();x++){
			int rndIndex = generator.nextInt(pnts.size());
			Object o = pnts.get(x);
			pnts.set(x,pnts.get(rndIndex));
			pnts.set(rndIndex,o);
		}
		return pnts;
	}
	
	private DelaunayDAG GetGreatSimplex(){
		double greatestX = 0; 
		double greatestY = 0;	
		for(int x=0;x<m_pnts.size();x++){
			AlgoGeoMain.MyPoint p = (AlgoGeoMain.MyPoint)m_pnts.get(x);
			if(p.x>greatestX){
				greatestX = p.x;
			}
			if(p.y>greatestY){
				greatestY = p.y;
			}
		}
		double smallestX = greatestX;
		double smallestY = greatestY;
		for(int x=0;x<m_pnts.size();x++){
			AlgoGeoMain.MyPoint p = (AlgoGeoMain.MyPoint)m_pnts.get(x);
			if(p.x<smallestX){
				smallestX = p.x;
			}
			if(p.y<smallestY){
				smallestY = p.y;
			}
		}
		
		AlgoGeoMain.MyPoint mp = new AlgoGeoMain.MyPoint((greatestX+smallestX)/2,(greatestY+smallestY)/2);
		
		double dX = greatestX - smallestX;
		double dY = greatestY - smallestY;
		double m = 3*Math.max(dX,dY);
		
		MySimplex s = new MySimplex(new AlgoGeoMain.MyPoint(mp.x,mp.y+m),new AlgoGeoMain.MyPoint(mp.x+m,mp.y),new AlgoGeoMain.MyPoint(mp.x-m,mp.y-m));
		DelaunayDAG d = new DelaunayDAG(s);
		s.getM_l1().setM_Left(d);
		s.getM_l2().setM_Left(d);
		s.getM_l3().setM_Left(d);
		
		return d;
	}
}
