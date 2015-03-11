/**
 * Model class for the puzzle
 */
package com.netra.thepuzzler;

public class Puzzle {
	private int id=0;
	private String content="";
	private String answer="";
	private String hint="";
	private String lettersHint="";
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getHint() {
		return hint;
	}
	public void setHint(String hint) {
		this.hint = hint;
	}
	public String getLettersHint() {
		return lettersHint;
	}
	public void setLettersHint(String lettersHint) {
		this.lettersHint = lettersHint;
	}
}
