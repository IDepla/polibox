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
package it.polito.ai.polibox.test.database;

import static org.junit.Assert.*;
import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.persistency.model.dao.UserDao;
import it.polito.ai.polibox.persistency.model.exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@TestPropertySource(
	    locations = {
	    		"file:src/main/webapp/WEB-INF/configuration/logger/log4j.properties",
	    		"file:src/main/webapp/WEB-INF/language/EmailValidator_it_IT.properties",
				"file:src/main/webapp/WEB-INF/configuration/dao/persistence.properties"
	    }
)
@ContextConfiguration(
		
		locations={
		"file:src/main/webapp/WEB-INF/configuration/spring/context.xml",
		"file:src/main/webapp/WEB-INF/configuration/spring/spring-security.xml",
		"file:src/main/webapp/WEB-INF/configuration/dao/hibernate.xml",
		"file:src/main/webapp/WEB-INF/configuration/spring/spring-servlet.xml"
})
public class UserInsertTests {
	@Autowired
	private WebApplicationContext wac;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private MockMvc mock;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private FilterChainProxy springSecurityFilterChain;
	
	public UserInsertTests() {
		// TODO Auto-generated constructor stub
	}

	@Before
	public void init(){
		mock=MockMvcBuilders.webAppContextSetup(this.wac)
				.addFilter(this.springSecurityFilterChain, "/")
				.dispatchOptions(true)
				.build();
		sessionFactory.getCurrentSession().clear();
	}
	
	@Test
	public void hasSprinBeanSessionFactoryLoaded(){
		assertEquals(true,wac.containsBean("sessionFactory"));
	}
	@Test
	public void hasSprinBeanTransactionManagerLoaded(){
		assertEquals(true,wac.containsBean("transactionManager"));
	}
	@Test
	public void hasSprinBeanUserDaoLoaded(){
		assertEquals(true,wac.containsBean("userDao"));
	}
	@Test
	public void insertUser(){
		User user=new User();
		User user2;
		user.setCompany("società");
		user.setDeleted(false);
		user.setEmail("prova@asd.it");
		BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(10);
		String encodedPassword=encoder.encode("password");
		assertEquals(20, encodedPassword.length());
		user.setPassword(encodedPassword);
		user.setEnabled(true);
		user.setName("igo");
		user.setSurname("depla");
		
		try {
			userDao.save(user);
		} catch (Exception e1) {
			fail("user cannot be inserted");
		}
		
			user2=userDao.findByEmail("prova@asd.it");
			System.out.println(user2);
			assertEquals(true,user.getEmail().equals(user2.getEmail()));
	
	}
	
	@Test
	public void updateUser(){
		
	}
	
	@Test
	public void getUser(){
		
	}
	
	@Test
	public void deleteUser(){
		int res;
		
		res=sessionFactory.getCurrentSession()
			.createSQLQuery("delete from User where email=:email")
			.setParameter("email", "prova@asd.it")
			.executeUpdate();
		assertEquals(1, res);
	}
	
	@After
	public void close(){
		sessionFactory.getCurrentSession().close();
	}
}
