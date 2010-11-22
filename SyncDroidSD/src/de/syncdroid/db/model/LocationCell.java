package de.syncdroid.db.model;

public class LocationCell implements Model {
	private Long id;
	private Long locationId;

	private Integer lac;
	private Integer cid;
	

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		
		// object must be Test at this point
		LocationCell test = (LocationCell) obj;
		return 
			(lac != null && lac.equals(test.lac)) && 
			(cid != null && cid.equals(test.cid)) 
			;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (null == lac ? 0 : lac.hashCode());
		hash = 31 * hash + (null == cid ? 0 : cid.hashCode());
		return hash;
	}	

	@Override
	public String toString() {
		return "[" + lac + ", " + cid + "]";
	}
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public Integer getLac() {
		return lac;
	}

	public void setLac(Integer lac) {
		this.lac = lac;
	}

	public Integer getCid() {
		return cid;
	}

	public void setCid(Integer cid) {
		this.cid = cid;
	}

}
