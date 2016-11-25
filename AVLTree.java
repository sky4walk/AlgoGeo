//written by Andre Betz 2006 in Java 1.4.2
//http://www.andrebetz.de

public class AVLTree {
	public abstract static class Compare{
		abstract public int DoCompare(Object o1,Object o2); 	
	}
	public static class NodeElm {
		protected Object m_NodeObj = null;
		private NodeElm m_left = null;
		private NodeElm m_right = null;
		private NodeElm m_top = null;
		private int m_DiffHight = 0;
		private boolean m_isRight = false;
		private boolean m_isDeleted = false;
		
		public boolean isDeleted(){
			return m_isDeleted;
		}
		public void UnDelete(){
			m_isDeleted = false;
		}
		public void Delete(){
			m_isDeleted = true;
		}
		public boolean IsRight(){
			return m_isRight;
		}
		public boolean IsTop(){
			return (m_top==null ? true : false);
		}
		public boolean IsLeft(){
			return !m_isRight;
		}
		public void SetRight(){
			m_isRight = true;
		}
		public void SetLeft(){
			m_isRight = false;
		}
		public Object getM_ni() {
			return m_NodeObj;
		}		

		public int getM_DiffHight(){
			return m_DiffHight;
		}

		public void setM_DiffHight(int depth){
			m_DiffHight = depth;
		}
		
		public NodeElm getM_left() {
			return m_left;
		}

		public void setM_left(NodeElm left) {
			this.m_left = left;
			if(left!=null){
				this.m_left.SetLeft();
				this.m_left.setM_top(this);
			}
		}

		public NodeElm getM_right() {
			return m_right;
		}
		public NodeElm getTopRight(){
			NodeElm ne = null;
			if(!this.IsTop()){
				if(this.IsLeft()){
					ne = this.m_top;
				}
			}
			return ne;
		}
		public NodeElm getTopLeft(){
			NodeElm ne = null;
			if(!this.IsTop()){
				if(this.IsRight()){
					ne = this.m_top;
				}
			}
			return ne;
		}
		public void setM_right(NodeElm right) {
			this.m_right = right;
			if(right!=null){
				this.m_right.SetRight();
				this.m_right.setM_top(this);
			}
		}

		public NodeElm getM_top() {
			return m_top;
		}

		public void setM_top(NodeElm m_top) {
			this.m_top = m_top;
		}
		public NodeElm(Object obj){
			m_NodeObj = obj;
		}
		public String ToString(){
			return "";
		}
	}
	private NodeElm m_Root = null;
	private int m_ActiveCount = 0;
	private Compare m_Comp = null;
	
	public AVLTree(Compare comp){
		m_Comp = comp;
	}
	
	public void clear(){
		m_Root = null;
	}

	public int Size(){
		return m_ActiveCount;
	}
	public NodeElm Find(NodeElm ne){
		return Find(ne,m_Root);
    }

	private NodeElm Find(NodeElm ne,NodeElm ne_tree){
        while(ne_tree!=null){
            if(m_Comp.DoCompare(ne.getM_ni(),ne_tree.getM_ni())<0){
            	ne_tree = ne_tree.getM_left();
            }else if(m_Comp.DoCompare(ne.getM_ni(),ne_tree.getM_ni())>0){
            	ne_tree = ne_tree.getM_right();
            }else{
                return ne_tree;
            }
        }
        return null;
    }

	public void Insert(NodeElm ne){
		if(ne!=null){
			String insName = ne.getM_ni().getClass().getName();
			if(m_Root==null){				
				m_Root = Insert(ne,m_Root);
				m_Root.setM_top(null);
			}else{
				String rtName = m_Root.getM_ni().getClass().getName();
				if(rtName.equals(insName)){
					m_Root = Insert(ne,m_Root);					
					m_Root.setM_top(null);
				}
			}
		}
	}
	
	public void Delete(NodeElm ne){
		if(!ne.isDeleted()){
			ne.Delete();
			m_ActiveCount--;
		}
	}
	
	private NodeElm Insert(NodeElm obj, NodeElm ne_Tree){
        if(ne_Tree == null ){
        	ne_Tree = obj;
            m_ActiveCount++;
        }
        else if(m_Comp.DoCompare(obj.getM_ni(),ne_Tree.getM_ni())>=0){
        	ne_Tree.setM_right(Insert(obj,ne_Tree.getM_right()));          
            if(height(ne_Tree.getM_right())-height( ne_Tree.getM_left())==2){
                if(m_Comp.DoCompare(obj.getM_ni(),ne_Tree.getM_right().getM_ni())>=0){
                	ne_Tree = rotateWithRightChild(ne_Tree);
                }else{
                	ne_Tree = doubleWithRightChild(ne_Tree);
                }
            }
        }else if(m_Comp.DoCompare(obj.getM_ni(),ne_Tree.getM_ni())<0){
        	ne_Tree.setM_left(Insert(obj,ne_Tree.getM_left()));
            if(height(ne_Tree.getM_left())-height(ne_Tree.getM_right())==2){
                if(m_Comp.DoCompare(obj.getM_ni(),ne_Tree.getM_left().getM_ni())<0){
                	ne_Tree = rotateWithLeftChild(ne_Tree);
                }else{
                	ne_Tree = doubleWithLeftChild(ne_Tree);
                }
            }
        }
        else{
        	ne_Tree.UnDelete();
        }
        ne_Tree.setM_DiffHight(max( height( ne_Tree.getM_left() ), height( ne_Tree.getM_right() ) ) + 1);
        return ne_Tree;
    }
	
    private NodeElm rotateWithLeftChild(NodeElm k2){
    	NodeElm k1 = k2.getM_left();
        k2.setM_left(k1.getM_right());
        k1.setM_right(k2);
        k2.setM_DiffHight(max(height(k2.getM_left()),height(k2.getM_right()))+1);
        k1.setM_DiffHight(max(height(k1.getM_left()),k2.getM_DiffHight())+1);
        return k1;
    }

    private NodeElm rotateWithRightChild(NodeElm k1){
    	NodeElm k2 = k1.getM_right();
        k1.setM_right(k2.getM_left());
        k2.setM_left(k1);
        k1.setM_DiffHight(max(height(k1.getM_left()),height(k1.getM_right()))+1);
        k2.setM_DiffHight(max(height(k2.getM_right()),k1.getM_DiffHight())+1);
        return k2;
    }

    private NodeElm doubleWithLeftChild(NodeElm k){
        k.setM_left(rotateWithRightChild(k.getM_left()));
        return rotateWithLeftChild(k);
    }

    private NodeElm doubleWithRightChild(NodeElm k){
        k.setM_right(rotateWithLeftChild(k.getM_right()));
        return rotateWithRightChild(k);
    }

	private int height(NodeElm ne){
        return ne == null ? -1 : ne.getM_DiffHight();
    }

    private int max(int lhs,int rhs){
        return lhs > rhs ? lhs : rhs;
    }

    public NodeElm GreatestNode(){
    	return GreatestNode(m_Root);
    }
    public NodeElm SmallestNode(){
    	return SmallestNode(m_Root);
    }
    private NodeElm GreatestNode(NodeElm ne){
    	NodeElm nxt_ne = null;
    	while(ne!=null){
    		nxt_ne = ne;
   			ne = ne.getM_right();
    	}
    	return nxt_ne;
    }
    
    private NodeElm SmallestNode(NodeElm ne){
    	NodeElm nxt_ne = null;
    	while(ne!=null){
    		nxt_ne = ne;
   			ne = ne.getM_left();
    	}
    	return nxt_ne;
    }

    public String ToString(){
    	return ToString(m_Root,0);
    }
    
    private String ToString(NodeElm ne,int cnt){
    	String str = "";
    	if (ne!=null)
   		{
    		str = ToString (ne.getM_left(), cnt+1);
    		for (int i=0;i<cnt;i++) {
    			str += " ";
    		}
    		str += ne.ToString();
    		str +=	ToString (ne.getM_right(), cnt+1);
    	}
    	return str;
    }
    
    public NodeElm GetNext(NodeElm ne){
    	ne = NextNode(ne);
    	while(ne!=null&&ne.isDeleted()){
        	ne = NextNode(ne);    		
    	}
    	return ne;
    }
    public NodeElm GetLast(NodeElm ne){
    	ne = PrevNode(ne);
    	while(ne!=null&&ne.isDeleted()){
        	ne = PrevNode(ne);    		
    	}
    	return ne;
    }
    public NodeElm GetFirst(){
    	NodeElm ne = SmallestNode();
    	if(ne!=null&&ne.isDeleted()){
        	ne = GetNext(ne);    		
    	}    	
    	return ne;
    }
    public NodeElm PrevNode(NodeElm ne){
       	NodeElm nxt_ne = null;
    	if(ne!=null){
   			nxt_ne = ne.getM_left();
    		if(nxt_ne!=null){
   				nxt_ne = GreatestNode(nxt_ne);        			
    		}else{
    			nxt_ne = ne.getTopLeft();
    			while(nxt_ne==null&&ne!=null){
    				ne = ne.getTopRight();
    				if(ne!=null){
    					nxt_ne = ne.getTopLeft();
    				}
    			}   
    		}
    	}
    	return nxt_ne;
    }
    
    public NodeElm NextNode(NodeElm ne){
    	NodeElm nxt_ne = null;
    	if(ne!=null){
   			nxt_ne = ne.getM_right();
    		if(nxt_ne!=null){
   				nxt_ne = SmallestNode(nxt_ne);
    		}else{
    			nxt_ne = ne.getTopRight();
    			while(nxt_ne==null&&ne!=null){
    				ne = ne.getTopLeft();
    				if(ne!=null){
    					nxt_ne = ne.getTopRight();
    				}
    			}    			
    		}
    	}
    	return nxt_ne;
    }
}
