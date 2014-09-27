package cn.clickwise.clickad.feathouse;

public abstract class Receipt {

	private Dmp dmp;
	
	private String codeOfArea;
	
	private int pv;
	
	private int uv;
	
	private int day;
	
	private long time;
	
	private String receiptId;

	public Dmp getDmp() {
		return dmp;
	}

	public void setDmp(Dmp dmp) {
		this.dmp = dmp;
	}

	public int getPv() {
		return pv;
	}

	public void setPv(int pv) {
		this.pv = pv;
	}

	public int getUv() {
		return uv;
	}

	public void setUv(int uv) {
		this.uv = uv;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getCodeOfArea() {
		return codeOfArea;
	}

	public void setCodeOfArea(String codeOfArea) {
		this.codeOfArea = codeOfArea;
	}
	
	
	
}
