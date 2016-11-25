//written by Andre Betz 2006 in Java 1.4.2
//http://www.andrebetz.de

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;

public class AlgoGeoMain {
    private JLabel label;
    private JFrame fenster = null;
    private ArrayList pnts = new ArrayList();
    private ArrayList lines = new ArrayList();
    private ArrayList circles = new ArrayList();
    private ArrayList plgLst = null;
    private CoordinateArea ca = null;
    
    public void updateCursorLocation(int x, int y) {
        if (x < 0 || y < 0) {
            return;
        }
        
        String lblText = "("+ x + ", " + y + ")";
        label.setText(lblText);       
    }
    
    private ArrayList CreatePlugIns(String[] ClassNames){
    	ArrayList objLst = new ArrayList();
    	if(ClassNames!=null){
    		for(int i=0;i<ClassNames.length;i++){
    			Object o = LoadClassObject(ClassNames[i]);
    			objLst.add(o);
    		}
    	}
    	return objLst;
    }
    
    private Object LoadClassObject(String ClassName){
    	Object o = null;
    	try{
    		int pos = ClassName.indexOf("class");
    		String ClassNameWOC = ClassName.substring(0,pos-1);
    		Class c = Class.forName(ClassNameWOC);
    		o = c.newInstance();
    	}catch (Exception e) {
    		return null;
    	}
    	return o;
    }
    
    public static String GetClassPathName(){
    	String classpath = System.getProperty("java.class.path");
    	return classpath;
    }
    
    private String GetPathName(Component parent,String clpt){   	
    	JFileChooser dirchoose = new JFileChooser(clpt);
    	dirchoose.setDialogTitle("Choose plugin directory");
    	dirchoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = dirchoose.showOpenDialog(parent);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	return dirchoose.getSelectedFile().getAbsolutePath();
        }
        return "";
    }
    
    private String[] GetPluginNames(String PluginPath){
       	File dir = new File (PluginPath) ;
       	String[] strFilesDirs = dir.list (new FilenameFilter() {
       		  public boolean accept( File d, String name ) {
       			    return name.toLowerCase().endsWith( "_plugin.class" );
       			  } } );
       	return strFilesDirs;
    }
    
    private void CreateMenuEntries(JMenu function,ArrayList plgLst){
    	if(plgLst!=null){
    		for(int i=0;i<plgLst.size();i++){
    			Object o = (Object)plgLst.get(i);
    			PluginInterface plg = (PluginInterface)o;
    			if(plg!=null){
    				plg.Init(fenster.getContentPane(),pnts,lines,circles);
    				String ClassName = o.getClass().getName();
    				JMenuItem item = new JMenuItem(ClassName);
    				item.addActionListener(new MenuEventCatcher(i+1,this));
    				function.add(item);
    			}
    		}
    	}
    }
    
    public AlgoGeoMain(){
    	JFrame.setDefaultLookAndFeelDecorated(true);
        fenster = new JFrame("AlgoGeo AndreBetz.de");
        fenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fenster.getContentPane().setLayout(new BoxLayout(fenster.getContentPane(),BoxLayout.PAGE_AXIS));
        
        JMenuBar menuBar = new JMenuBar();
        fenster.setJMenuBar(menuBar);

        JMenu function = new JMenu("Funktionen");
        menuBar.add(function);
        
        JMenuItem item = new JMenuItem("Reset");
        item.addActionListener(new MenuEventCatcher(0,this));
        function.add(item);

        String PlgPath = GetClassPathName();
        String[] plugins = GetPluginNames(PlgPath);
        if(plugins==null||plugins.length==0){
        	PlgPath = GetPathName(fenster.getContentPane(),PlgPath);
        	plugins = GetPluginNames(PlgPath);
        }
        plgLst = CreatePlugIns(plugins);
        CreateMenuEntries(function,plgLst);
        
        ca = new CoordinateArea(this,pnts,lines,circles);
        fenster.getContentPane().add(ca);
        
        label = new JLabel();
        fenster.getContentPane().add(label);
        
        ca.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        fenster.setLocation(50,50);
        fenster.setSize(200,200);
        fenster.pack();
        fenster.setVisible(true);
    }
    
    public void Init(){
    	pnts.clear();
    	lines.clear();
    	circles.clear();
    	ca.RePaint();
    }
    
    public void Repaint(){
    	ca.RePaint();    	
    }
    
    public static void main(String[] args) {
    	AlgoGeoMain agm = new AlgoGeoMain();
    	agm.Init();
    }

    public interface PluginInterface {
    	public void Init(Component fenster, ArrayList pnts,ArrayList lines,ArrayList circles);
    	public boolean Start();   	
    } 

    public static class MyPoint{
    	public double x;
    	public double y;
    	public MyPoint(double nx,double ny){
    		x = nx;
    		y = ny;
    	}
    	public MyPoint(MyPoint p){
    		x = p.x;
    		y = p.y;
    	}
    	public boolean Equals(MyPoint p){
    		if(p.x==this.x&&p.y==this.y){
    			return true;
    		}
    		return false;
    	}
    }
    
    public static class MyLine {
    	private MyPoint m_p1 = null;
    	private MyPoint m_p2 = null;
    	
    	public MyLine(MyPoint p1,MyPoint p2){
    		m_p1 = p1;
    		m_p2 = p2;
    	}
    	
    	public MyPoint getP1(){
    		return m_p1;
    	}
    	public MyPoint getP2(){
    		return m_p2;
    	}
    	public boolean Equals(MyPoint p){
    		if(p.x==m_p1.x&&p.y==m_p1.y){
    			return true;
    		}else if(p.x==m_p2.x&&p.x==m_p2.x){
    			return true;
    		}else{
    			return false;
    		}
    	}
    	
		public int PointPosition(MyPoint r){
			// -1: Punkt liegt links
			//  0: Punkt liegt auf der Linie
			//  1: Punkt liegt rechts
			double Det = (m_p1.x*m_p2.y + m_p1.y*r.x + m_p2.x*r.y  - r.x*m_p2.y - r.y*m_p1.x - m_p2.x*m_p1.y)*0.5;			
			return (int)(Det > 0.0 ? 1 : (Det < 0.0 ? -1 : 0));
		}

		public boolean Cut(MyLine l){
			int D1 = this.PointPosition(l.getP1());
			int D2 = this.PointPosition(l.getP2());
			int D3 = l.PointPosition(m_p1);
			int D4 = l.PointPosition(m_p2);
			return (D1!=D2 && D3!=D4 ? true : false);
		}

    	public boolean Equals(MyLine l){
    		if(this.m_p1.Equals(m_p2)){
    			if(l.getP1().Equals(l.getP2())){
    				if(this.m_p1.Equals(l.getP1())){
    					return true;
    				}
    			}
    		}else{
    			if(l.getP1().Equals(l.getP2())){
    	   			return false;
    			}else{
    				if(this.m_p1.Equals(l.getP1())&&this.m_p2.Equals(l.getP2())){
    					return true;
    				}else if(this.m_p1.Equals(l.getP2())&&this.m_p2.Equals(l.getP1())){
    					return true;
    				}else{
    		   			return false;
    				}
    			}
    		}
   			return false;
    	}
    }
    
    public class MenuEventCatcher implements ActionListener {
    	int m_actionNr = -1;
    	AlgoGeoMain m_agm = null;
    	public MenuEventCatcher(int actionNr,AlgoGeoMain agm){
    		m_actionNr = actionNr;
    		m_agm = agm;
    	}
        public void actionPerformed(ActionEvent e) {
        	if(m_actionNr==0){
            	m_agm.Init();        		
        	}else{
        		Object o = (Object)plgLst.get(m_actionNr-1);
    			PluginInterface plg = (PluginInterface)o;
    			plg.Start();
    			m_agm.Repaint();
        	}
        }
    }
    
    public class CoordinateArea  extends JComponent implements MouseInputListener{
		private static final long serialVersionUID = 1L;
		AlgoGeoMain agm = null;
        Dimension preferredSize = new Dimension(300,300);
        private ArrayList pntsLst = null; 
        private ArrayList linesLst = null; 
        private ArrayList circleLst = null;
    
        public CoordinateArea(AlgoGeoMain controller,ArrayList pnts,ArrayList lines, ArrayList circles) {
            this.agm = controller;           
            this.pntsLst = pnts;
            this.linesLst = lines;
            this.circleLst = circles;
            addMouseListener(this);
            addMouseMotionListener(this);
            setBackground(Color.WHITE);
            setOpaque(true);
        }
   
        public Dimension getPreferredSize() {
            return preferredSize;
        }
    
        protected void paintComponent(Graphics g) {
            if (isOpaque()) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            
            drawGrid(g, 20);
            drawPoints(g);
            drawLines(g);
            drawCircles(g);
        }
        
        private void drawCircles(Graphics g){
        	g.setColor(Color.BLUE);
        	for(int i=0;i<circleLst.size();i++){
        		MyPoint p = (MyPoint)circleLst.get(i);
        		g.drawOval((int)p.x-4,(int)p.y-4,8,8);
        	}
        }
        
        private void drawPoints(Graphics g){
        	g.setColor(Color.RED);
        	for(int i=0;i<pntsLst.size();i++){
        		MyPoint p = (MyPoint)pntsLst.get(i);
        		g.drawLine((int)p.x-3,(int)p.y-3,(int)p.x+3,(int)p.y+3);
        		g.drawLine((int)p.x-3,(int)p.y+3,(int)p.x+3,(int)p.y-3);
        	}
        }
        
        private void drawLines(Graphics g){
        	g.setColor(Color.GREEN);
        	for(int i=0;i<linesLst.size();i++){
        		MyLine oneln = (MyLine)linesLst.get(i);
        		MyPoint p1 = oneln.getP1();
        		MyPoint p2 = oneln.getP2();
        		g.drawLine((int)p1.x,(int)p1.y,(int)p2.x,(int)p2.y);
        	}
        }
        
        private void drawGrid(Graphics g, int gridSpace) {
        	g.setColor(Color.GRAY);
            Insets insets = getInsets();
            int firstX = insets.left;
            int firstY = insets.top;
            int lastX = getWidth() - insets.right;
            int lastY = getHeight() - insets.bottom;
            
            int x = firstX;
            while (x < lastX) {
                g.drawLine(x, firstY, x, lastY);
                x += gridSpace;
            }
            
            int y = firstY;
            while (y < lastY) {
                g.drawLine(firstX, y, lastX, y);
                y += gridSpace;
            }
        }
    
        public void mouseClicked(MouseEvent e) { 
        	pntsLst.add(new MyPoint(e.getX(), e.getY()));
            repaint();
        }

        public void RePaint(){
        	repaint();
        }
        
        public void mouseMoved(MouseEvent e) {
            agm.updateCursorLocation(e.getX(), e.getY());
        }

        public void mouseExited(MouseEvent e) { }
        public void mouseReleased(MouseEvent e) { }
        public void mouseEntered(MouseEvent e) { }
        public void mousePressed(MouseEvent e) { }
        public void mouseDragged(MouseEvent e) { }
    }
    
    public static class CreateLines_plugin implements AlgoGeoMain.PluginInterface {
    	private ArrayList m_pnts = null;
    	private ArrayList m_lines = null;
    	public void Init(Component parent, ArrayList pnts,ArrayList lines, ArrayList circles){
    		m_pnts = pnts;
    		m_lines = lines;
    	}
    	public boolean Start(){
    		m_lines.clear();
    		for(int i=1;i<m_pnts.size();i+=2){
    			MyPoint p1 = (MyPoint)m_pnts.get(i-1);
    			MyPoint p2 = (MyPoint)m_pnts.get(i);
    			AlgoGeoMain.MyLine ml = new AlgoGeoMain.MyLine(p1,p2);
    			m_lines.add(ml);
    		}
    		return true;
    	}
    }
    public static class CreatePolygon_plugin implements AlgoGeoMain.PluginInterface {
    	private ArrayList m_pnts = null;
    	private ArrayList m_lines = null;
    	public void Init(Component parent, ArrayList pnts,ArrayList lines, ArrayList circles){
    		m_pnts = pnts;
    		m_lines = lines;
    	}
    	public boolean Start(){
    		m_lines.clear();
    		for(int i=1;i<m_pnts.size();i++){
    			MyPoint p1 = (MyPoint)m_pnts.get(i-1);
    			MyPoint p2 = (MyPoint)m_pnts.get(i);
    			AlgoGeoMain.MyLine ml = new AlgoGeoMain.MyLine(p1,p2);
    			m_lines.add(ml);
    		}
    		if(m_pnts.size()>1){
    			MyPoint p1 = (MyPoint)m_pnts.get(0);
    			MyPoint p2 = (MyPoint)m_pnts.get(m_pnts.size()-1);
    			AlgoGeoMain.MyLine ml = new AlgoGeoMain.MyLine(p1,p2);
    			m_lines.add(ml);
    			
    		}
    		return true;
    	}
    }
}
