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
package it.polito.ai.polibox.test.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
public class FilterChainingTests{
	
	private MockMvc mock;
	@Autowired
	private WebApplicationContext wac;
	@Autowired
	private FilterChainProxy springSecurityFilterChain;
	
	@Before
	public void setup() throws Exception {
		mock=MockMvcBuilders.webAppContextSetup(this.wac)
				.addFilter(this.springSecurityFilterChain, "/")
				.dispatchOptions(true)
				.build();
	}
	
	@Test
	public void caricareVariUrl() throws Exception {
		mock.perform(get("/rs/manifest.html"))
				.andExpect(status().isOk());
		mock.perform(post("/asd_asdasd"))
				.andExpect(status().is4xxClientError());
		mock.perform(get("/index"))
				.andExpect(status().isOk());

	}
}
