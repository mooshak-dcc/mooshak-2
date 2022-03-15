package pt.up.fc.dcc.mooshak.evaluation.graph.data;

import java.util.Map;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Match;

public class CompositePropertyName extends PropertyName {
	private String name;
	private String type;
	private GObject reference;

	
	
	public CompositePropertyName(String type, String name) {
		this.type = type;
		this.name = name;
		this.reference = null; //Alterações Helder Correia 28/11/2017********************************
	}
	
	public CompositePropertyName(String type, String name,GObject reference) {
		this.type = type;
		this.name = name;
		this.reference = reference; //Alterações Helder Correia 28/11/2017********************************
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	public boolean isSimple(){
		return false;
	}
	
	public PropertyName deepCopy(){
		return new CompositePropertyName(this.type, this.name);
	}
	
	public String toString(){
		return "type = " + this.type + " | name = " + this.name  +   "  | reference= "+this.reference;
	}
	
	
	
	
//	/* (non-Javadoc)
//	 * @see java.lang.Object#hashCode()
//	 */
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((name == null) ? 0 : name.hashCode());
//		result = prime * result + ((type == null) ? 0 : type.hashCode());
//		return result;
//	}
	
	
	
	

//	/* (non-Javadoc)
//	 * @see java.lang.Object#equals(java.lang.Object)
//	 */
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		CompositePropertyName other = (CompositePropertyName) obj;
//		if (name == null) {
//			if (other.name != null)
//				return false;
//		} else if (!name.equals(other.name))
//			return false;
//		if (type == null) {
//			if (other.type != null)
//				return false;
//		} else if (!type.equals(other.type))
//			return false;
//		return true;
//	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompositePropertyName other = (CompositePropertyName) obj;
//		if (name == null) {
//			if (other.name != null)
//				return false;
//		} else if (!name.equals(other.name))
//			return false;
		if (reference == null) {
			if (other.reference != null)
				return false;
		} else if (!reference.equals(other.reference))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((reference == null) ? 0 : reference.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}



	public boolean equals(Object obj,Map<Node, Match> map) {
//		System.out.println("MAP -> \n" + map +" \n\n");
		
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompositePropertyName other = (CompositePropertyName) obj;
//		if (name == null) {
//			if (other.name != null)
//				return false;
//		} else if (!name.equals(other.name))
//			return false;
		
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if(this.reference == null &&((CompositePropertyName) obj).reference !=null)
			return false;
		else if(this.reference != null &&((CompositePropertyName) obj).reference ==null)
			return false;
		
		 if(map!=null){
//			 System.out.println("(map.containsKey(this.reference) " +  (map.get(this.reference).getAttempt()) + 
//		 "\n \n ref" + (((CompositePropertyName) obj).reference) + "\n \n boolean "+
//					 ((map.containsKey(this.reference) && 
//							  (map.get(this.reference).getAttempt()!=null) && 
//							 (map.get(this.reference).getAttempt().equals( (((CompositePropertyName) obj).reference)))) +"\n\n\n")
//					 );
			if((map.containsKey(this.reference) && 
			  (map.get(this.reference).getAttempt()!=null) && 
			 (map.get(this.reference).getAttempt().equals( (((CompositePropertyName) obj).reference)))) )
			 return true;
		 }
		 
		 else{
			 if (type.equals(other.type) && (this.reference == null) && (((CompositePropertyName) obj).reference ==null))
				 return true;
		 }
		
		 
		return false;
	}
	
	
	
	
	/**
	 * 
	 * public boolean equals(Object obj,Map<Node, Match> map) {
//		System.out.println("MAP -> \n" + map +" \n\n");
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompositePropertyName other = (CompositePropertyName) obj;
//		if (name == null) {
//			if (other.name != null)
//				return false;
//		} else if (!name.equals(other.name))
//			return false;
		
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if(this.reference == null &&((CompositePropertyName) obj).reference !=null)
			return false;
		else if(this.reference != null &&((CompositePropertyName) obj).reference ==null)
			return false;
		
		 if(map!=null){
//			 System.out.println("(map.containsKey(this.reference) " +  (map.get(this.reference).getAttempt()) + 
//		 "\n \n ref" + (((CompositePropertyName) obj).reference) + "\n \n boolean "+
//					 ((map.containsKey(this.reference) && 
//							  (map.get(this.reference).getAttempt()!=null) && 
//							 (map.get(this.reference).getAttempt().equals( (((CompositePropertyName) obj).reference)))) +"\n\n\n")
//					 );
			if((map.containsKey(this.reference) && 
			  (map.get(this.reference).getAttempt()!=null) && 
			 (map.get(this.reference).getAttempt().equals( (((CompositePropertyName) obj).reference)))) )
			 return true;
		 }	
//		 else{
//			 if(this.reference.equals( (((CompositePropertyName) obj).reference)))
//				 return true;
//		 }
		
		 
		return false;
	}
	 * 
	 * 
	 * 
	 * **/
	
	

	public GObject getReference() {
		return reference;
	}

	public void setReference(GObject reference) {
		this.reference = reference;
	}
	
}