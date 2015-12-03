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
import it.polito.ai.polibox.persistency.model.ResourceChunk;
import it.polito.ai.polibox.persistency.model.ResourceChunkId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface ResourceChunkDao extends JpaRepository<ResourceChunk, ResourceChunkId> {


	
	
	/*
	 * devo confrontare con il chunk number della mia versione.
	 * questo perchè in una versione precedente posso aver messo anche più chunk, 
	 * in questo modo prendo solo quelli della mia versione
	 * 
	 * 
	 * ( " +
			"select v.chunkNumber "+
			"from ResourceVersion v " +
			"where " +
			"v.pk.resource=(?1) and " +
			"v.pk.version=(?2)" +
			")"+
	 * */
	@Query(value="select r " +
			"from it.polito.ai.polibox.persistency.model.ResourceChunk r " +
			"where ( " +
			"r.pk.resourceVersion.pk.resource, " +
			"r.pk.resourceVersion.pk.version, "+
			"r.pk.number "+
			") in (select k.pk.resourceVersion.pk.resource, " +
						  "max(k.pk.resourceVersion.pk.version), " +
						  "k.pk.number " +
									"from ResourceChunk k " +
									"where " +
									"k.pk.resourceVersion.pk.resource=(?1) and " +
									"k.pk.resourceVersion.pk.version<=(?2) " +
									"group by k.pk.number " +
									"having k.pk.number < (?3)"+
				 ") " +
			"order by r.pk.number ")
	List<ResourceChunk> getResourceChunkList(Resource resource,int version,int chunkNumber);
	
}
