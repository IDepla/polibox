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
package it.polito.ai.polibox.security.authentication;

import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.polito.ai.polibox.persistency.model.User;

public class AutenticationKeygen {

	private BCryptPasswordEncoder encoder;
	private Random random;
	private String randomSalt;
	private String autoAuthenticationKey;
	
	private AutenticationKeygen() {
		random=new Random();
		encoder=new BCryptPasswordEncoder(10);
		randomSalt=DigestUtils.sha512(""+random.nextLong()).toString();
	}
	
	public AutenticationKeygen(User u) {
		this();
		autoAuthenticationKey=encoder.encode(u.getEmail()+"|"+u.getPassword()+"|"+randomSalt);
	}

	public String getRandomSalt() {
		return randomSalt;
	}

	public String getAutoAuthenticationKey() {
		return autoAuthenticationKey;
	}

	public static String encodePassword(String pass){
		BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(10);
		return encoder.encode(pass);
	}
	
	public static PasswordEncoder getEncoder(){
		return new BCryptPasswordEncoder(10);
	}
}
