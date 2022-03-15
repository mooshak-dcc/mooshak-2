package pt.up.fc.dcc.mooshak.message;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a collection of sibling nodes in Mooshak's data tree.
 * Nodes are uniquely identified by objects (keys) 
 * similar to file system inodes. 
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
@XmlRootElement
public class MooshakSiblingNodes implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public Object parent = null;
	public Map<Object,MooshakNode> nodes = new LinkedHashMap<>();
	
	public static class MooshakNode implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public Object key;
		public String name;
		public boolean isFolder;

		public MooshakNode() {};

		public MooshakNode(Object key, String name,
				boolean isFolder) {
			super();
			this.key = key;
			this.name = name;
			this.isFolder = isFolder;
		}

	}
	
	public MooshakSiblingNodes() {}
	
	public MooshakSiblingNodes(Object parent) {
		this.parent = parent;
	}
	
	/**
	 * Add a node to the sibling collection
	 * @param key
	 * @param parent
	 * @param name
	 * @param isFolder
	 */
	public void addNode(Object key, String name, boolean isFolder) {
		nodes.put(key,new MooshakNode(key,name,isFolder));
	}
	
}
