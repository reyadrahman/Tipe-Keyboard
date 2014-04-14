package com.bontric.tipeKeyboard;

import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;

public class InputHandler {

	private TipeService mTipeService;
	
	// Word for composing
	private String 			composedWord;
	private CandidateView mCandidateView;
	
	public InputHandler() {
		
	}

	public void setIMS(TipeService tService) {
		mTipeService = tService;
	}

	public void sendKey(char c) {
		smallVibrate();

		composedWord += c;
		mCandidateView.getSuggestionsForWord(composedWord);
		mTipeService.setCandidatesViewShown(true);
		
		mTipeService.getCurrentInputConnection().commitText("" + c, 1);
		KeyboardHandler.shift_state = false;
		KeyboardHandler.handleShift();
	}

	public void handleSpace() {
		smallVibrate();
		
		resetComposedWord();
		sendKey((char) 32);
		
	}

	public void handleDelete() {
		// Match composed word
		smallVibrate();

		if(composedWord.length() <= 1)
			composedWord = "";
		else
			composedWord = composedWord.substring(0, composedWord.length()-1);
		mCandidateView.getSuggestionsForWord(composedWord);
		
		if(composedWord.isEmpty())
			mTipeService.setCandidatesViewShown(false);
		else
			mTipeService.setCandidatesViewShown(true);
		
		keyDownUp(KeyEvent.KEYCODE_DEL);
	}

	public void handleEnter() {
		smallVibrate();

		resetComposedWord();
		keyDownUp(KeyEvent.KEYCODE_ENTER);
	}

	/**
	 * use this for enter & delete key!
	 * 
	 * @param keyEventCode
	 */
	private void keyDownUp(int keyEventCode) {
		mTipeService.getCurrentInputConnection().sendKeyEvent(
				new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
		mTipeService.getCurrentInputConnection().sendKeyEvent(
				new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
	}
	
	
	/*------------------------------------------------------------
	*	From here functions for candidate view
	*	@Jakob Frick
	*------------------------------------------------------------*/
	
	public CandidateView initCandidateView(InputMethodService ims){
		mCandidateView = new CandidateView(ims);
		mCandidateView.setInputHandler(this);
		resetComposedWord();
		
		return mCandidateView;
	}
	
	public void resetComposedWord() {
		composedWord = "";
		mTipeService.setCandidatesViewShown(false);
	}
	
	public void setComposedWord(String soFarComposed){
		composedWord = soFarComposed;
		mTipeService.setCandidatesViewShown(true);
	}
	
	public void getSuggestionFromCandView(String suggestion){
		InputConnection ic = mTipeService.getCurrentInputConnection();
		
		ic.deleteSurroundingText(composedWord.length(), 0);
		ic.commitText(suggestion + " ", composedWord.length() + 1);
		resetComposedWord();
	}
	
	public void smallVibrate(){
		mTipeService.getCurVibrator().vibrate(60);		
	}
	
	
}
