//written by Andre Betz 2006 in Java 1.4.2
//http://www.andrebetz.de

public class myQuickSort {
	private AVLTree.Compare m_comp = null;
	private Object[] m_Values = null;
	
	public myQuickSort(AVLTree.Compare comp){
		m_comp = comp;
	}
	
	public void Sort(Object[] values){
		m_Values = values;
		qsort(0,m_Values.length-1);
	}
	
	private void qsort(int l,int r){
		if(l<r){
			int m = parts(l,r);
			if (m==r){
				m--;
			}
			qsort(l,m);
			qsort(m+1,r);
		}
	}

	private int parts(int l,int h){
		Object p = m_Values[l];
		while(true){
			while(m_comp.DoCompare(m_Values[h],p)>=0 && l<h){
				h--;
			}
			while(m_comp.DoCompare(m_Values[l],p)<0 && l<h){
				l++;
			}
			if(l<h){
				Object o = m_Values[l];
				m_Values[l] = m_Values[h];
				m_Values[h] = o;
			}else{
				return h;
			}
		}
	}
}
