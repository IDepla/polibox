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
package it.polito.ai.polibox.persistency.model.dao;

import java.util.List;

import javax.transaction.Transactional;

import it.polito.ai.polibox.persistency.model.Resource;
import it.polito.ai.polibox.persistency.model.ResourceVersion;
import it.polito.ai.polibox.persistency.model.ResourceVersionId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface ResourceVersionDao extends JpaRepository<ResourceVersion, ResourceVersionId> {
/*
 * @Query("SELECT r " +
			"FROM resource_version r " +
			"where " +
			"r.resource_id = 4 " + 
			"and version = " +
				"(select max(version) " +
				"from resource_version " +
				"where r.resource_id=4)")
	
 */
	@Query("SELECT r " +
			"FROM ResourceVersion r " +
			"where " +
			"r.pk.resource = ?1 " + 
			"and r.pk.version = " +
				"(select max(k.pk.version) " +
				"from ResourceVersion k " +
				"where k.pk.resource=?1)")
	public ResourceVersion getLastResourceVersion(Resource r);
	
	@Query("SELECT r " +
			"FROM ResourceVersion r " +
			"where " +
			"r.pk.resource.id = ?1 " + 
			"and r.pk.version = ?2 " +
			"and r.creator.id = ?3")
	public ResourceVersion isCreator(int resourceId,int resourceVersion,int userId);
	
	@Query("select r " +
			"from ResourceVersion r " +
			"where " +
			"r.pk.resource=?1 " +
			"order by r.creationTime DESC")
	public List<ResourceVersion> getHistory(Resource res);
	
	
}
