/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Igor Deplano
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package it.polito.ai.polibox.web.controllers.inputform.validation;

import it.polito.ai.polibox.web.controllers.inputform.FileInput;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class FileInputValidator implements Validator{
	private Pattern filePattern;
	private Matcher fileMatcher;
	private Pattern mimePattern;
	private Matcher mimeMatcher;
	private Pattern sha3Pattern;
	private Matcher sha3Matcher;
	
	
	private static final String FILE_PATTERN="^((\\/[A-Za-z0-9_-]+)*\\/[A-Za-z0-9_\\.-]+)?$";
	private static final String MIME_PATTERN="^[-\\w]+\\/[-\\w]+$";
	private static final String SHA3_PATTERN="^[0-9a-z]{128}$";
	
	public FileInputValidator() {
		filePattern=Pattern.compile(FILE_PATTERN);
		mimePattern=Pattern.compile(MIME_PATTERN);
		sha3Pattern=Pattern.compile(SHA3_PATTERN);
	}

	public boolean supports(Class<?> clazz) {
		return FileInput.class.equals(clazz);
	}

	public void validate(Object target, Errors errors) {
		FileInput file=(FileInput) target;
		if(file == null){
			errors.reject("file.notnull");
			return;
		}
		if(file.getName().equalsIgnoreCase("")){
			errors.reject("file.empty");
			return;
		}
		fileMatcher=filePattern.matcher(file.getName());
		
		if(!fileMatcher.matches())
			errors.reject("file.invalid");
		if(file.getSize()<0){
			errors.reject("file.sizeinvalid");
		}
		mimeMatcher=mimePattern.matcher(file.getMime());
		if(!mimeMatcher.matches()){
			errors.reject("file.invalidmime");
		}
		
		if(file.getChunkNumber()<=0){
			errors.reject("file.chunknumberinvalid");
		}
		sha3Matcher=sha3Pattern.matcher(file.getDigest());
		if(!sha3Matcher.matches()){
			errors.reject("file.digestinvalid");
		}
	}

}
