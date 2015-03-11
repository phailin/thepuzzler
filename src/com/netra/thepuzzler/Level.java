package com.netra.thepuzzler;

import java.util.ArrayList;

public class Level {
	private int level_id=0;
	private int isOpen=0;
	private int isComplete=0;
	private int isCleared=0;
	private int solved =0;
	private int hintsUsed=0;
	private int lettersHintsUsed=0;
	private int ansViewed=0;
	private int score=0;
	private int cur_puz_id=0;
    private ArrayList<Integer> listPuzSolved; // comma separated puzzle id for solved ones
    private ArrayList<Integer> listHintsUsed;	
    private ArrayList<Integer> listLettersHintsUsed;
    private ArrayList<Integer> listAnsViewed;
    
    public ArrayList<Integer> getListAnsViewed() {
		return listAnsViewed;
	}
	public void setListAnsViewed(ArrayList<Integer> listAnsViewed) {
		this.listAnsViewed = listAnsViewed;
	}
	    
	public int getLettersHintsUsed() {
		return lettersHintsUsed;
	}
	public void setLettersHintsUsed(int lettersHintsUsed) {
		this.lettersHintsUsed = lettersHintsUsed;
	}
	public ArrayList<Integer> getListLettersHintsUsed() {
		return listLettersHintsUsed;
	}
	public void setListLettersHintsUsed(ArrayList<Integer> listLettersHintsUsed) {
		this.listLettersHintsUsed = listLettersHintsUsed;
	}
	public int getLevel_id() {
		return level_id;
	}	
	public ArrayList<Integer> getListPuzSolved() {
		return listPuzSolved;
	}
	public void setListPuzSolved(ArrayList<Integer> listPuzSolved) {
		this.listPuzSolved = listPuzSolved;
	}
	public ArrayList<Integer> getListHintsUsed() {
		return listHintsUsed;
	}
	public void setListHintsUsed(ArrayList<Integer> listHintsUsed) {
		this.listHintsUsed = listHintsUsed;
	}

	public void setLevel_id(int level_id) {
		this.level_id = level_id;
	}
	public int getCur_puz_id() {
		return cur_puz_id;
	}
	public void setCur_puz_id(int cur_puz_id) {
		this.cur_puz_id = cur_puz_id;
	}
	public int getIsOpen() {
		return isOpen;
	}
	public void setIsOpen(int isOpen) {
		this.isOpen = isOpen;
	}
	public int getIsComplete() {
		return isComplete;
	}
	public void setIsComplete(int isComplete) {
		this.isComplete = isComplete;
	}
	public int getSolved() {
		return solved;
	}
	public void setSolved(int solved) {
		this.solved = solved;
	}
	public int getHintsUsed() {
		return hintsUsed;
	}
	public void setHintsUsed(int hintsUsed) {
		this.hintsUsed = hintsUsed;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getAnsViewed() {
		return ansViewed;
	}
	public void setAnsViewed(int ansViewed) {
		this.ansViewed = ansViewed;
	}
	public int getIsCleared() {
		return isCleared;
	}
	public void setIsCleared(int isCleared) {
		this.isCleared = isCleared;
	}
	
}
