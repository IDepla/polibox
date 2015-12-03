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

import java.util.ArrayList;
import java.util.List;

import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.persistency.model.dao.UserDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private UserDao userDao;
	
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		
		User user = userDao.findByEmail(username);
		if(user== null){
			throw new UsernameNotFoundException(username);
		}
		List<GrantedAuthority> authorities = buildUserAuthority(user);
		return buildUserForAuthentication(user, authorities);
	
		
		
	}
	
	// Converts it.polito.ai.polibox.persistency.model.User user to
	// org.springframework.security.core.userdetails.User
	private org.springframework.security.core.userdetails.User buildUserForAuthentication(User user, 
		List<GrantedAuthority> authorities) {
		return new org.springframework.security.core.userdetails.User(
				user.getEmail(), 
				user.getPassword(), 
				user.isEnabled(), 
                !user.isDeleted(), 
                true, 
                !user.isDeleted(), 
                authorities);
	}
 
	private List<GrantedAuthority> buildUserAuthority(User user) {
		List<GrantedAuthority> result = new ArrayList<GrantedAuthority>();
		if(user != null){
			result.add(new SimpleGrantedAuthority("ROLE_EXISTENCE"));
		}
		return result;
	}
 
	public UserDao getUserDao() {
		return userDao;
	}
 
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
}
