/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.orcid.core.manager.PasswordGenerationManager;
import org.orcid.password.constants.OrcidPasswordConstants;

/**
 * Class to randomly generate a password of given length and that satisfies the rules for the Orcid password policy
 * @author jamesb
 * 
 */
public class PasswordGenerationManagerImpl implements PasswordGenerationManager {

	private int passwordLength;	
	private static final int NUM_PRESET_CHARS=3;
	
	public PasswordGenerationManagerImpl(int passwordLength) {
		super();
		this.passwordLength = passwordLength;
	}	
	

	public int getPasswordLength() {
		return passwordLength;
	}



	public void setPasswordLength(int passwordLength) {
		this.passwordLength = passwordLength;
	}



	@Override
	public char[] createNewPassword() {

		// must contain at least 12 chars
		char [] presetChars = new char[NUM_PRESET_CHARS];

		// one each of symbol, character and number
		presetChars[0] = randomSymbol();
		presetChars[1] = randomNumber();
		presetChars[2] = randomCharacter();
		
		//rest are also random but from any of the classes
		char[] mixedCharacterClasses = RandomStringUtils.random(passwordLength-NUM_PRESET_CHARS,OrcidPasswordConstants.getEntirePasswordCharsRange()).toCharArray();
	
		// shuffle the string so that the pattern of first 3 chars
		// doesn't become predictable
		return shuffleChar(ArrayUtils.addAll(mixedCharacterClasses, presetChars));

	}

	private char randomSymbol() {
		return RandomStringUtils.random(1, OrcidPasswordConstants.UNESCAPED_SYMBOL_RANGE).charAt(0);
	}

	private char randomNumber() {
		return RandomStringUtils.random(1,OrcidPasswordConstants.CHAR_CLASS_NUMBERS).charAt(0);
	}

	private char randomCharacter() {
		return RandomStringUtils.random(1, OrcidPasswordConstants.LOWERCASE_ALPHABET).charAt(0);
	}

	private char[] shuffleChar(char[] randomChars) {
		for (int i = randomChars.length; i > 1; i--) {
			char temp = randomChars[i - 1];
			int randIx = (int) (Math.random() * i);
			randomChars[i - 1] = randomChars[randIx];
			randomChars[randIx] = temp;
		}
		return randomChars;
	}

}
