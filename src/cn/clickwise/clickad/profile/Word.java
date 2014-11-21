package cn.clickwise.clickad.profile;

public class Word {

	private String word;
	
	private int value;

	public Word(String word,int value)
	{
	  this.word=word;
	  this.value=value;
	}
	
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
