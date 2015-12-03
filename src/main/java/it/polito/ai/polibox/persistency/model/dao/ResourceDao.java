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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface ResourceDao extends JpaRepository<Resource, Integer> {

	@Query("select r " +
		   "from ResourceOwners s, Resource r " +
		   "where " +
		   		"s.pk.resource = r AND "+
		   		"s.pk.user.id=?1 and " +
				"r.deleted=0 " +
			"order by r.name " )
	List<Resource> getAllYourResources(int id);
	
	
	@Query("select r " +
			   "from ResourceOwners s, Resource r " +
			   "where " +
			   		"s.pk.resource = r AND "+
			   		"s.pk.user.id=?1 " +
				"order by r.name " )
	List<Resource> getAllYourResourcesWithDeleted(int id);
	
	@Query("select r " +
			"from ResourceOwners s, Resource r " +
			   "where " +
			   	"s.pk.resource = r AND "+
			   	"s.pk.user.id=?1 and " +
				"r.deleted=1 ")
	List<Resource> getYourTrash(int id);
	
	@Query(value="select r.id " +
			"from resource_owners s, resource r " +
			   "where " +
			   	"s.resource_id = r.id AND "+
			   	"s.user_id = ?1 and " +
			   	"r.deleted = 0 and "+
				"r.name REGEXP CONCAT('^',?2,'\\\\/[A-Za-z0-9_\\\\.\\\\-]+(\\\\.[A-Za-z0-9]+)?$')", nativeQuery=true)
	List<Integer> getYourResources(int id,String basePath);
	
	@Query(value=
				"select r.id " +
				"from resource_owners s,resource r " +
				"where " +
				"s.resource_id = r.id AND "+
			   	"s.user_id = ?2 and " +
				"r.name REGEXP CONCAT('^',( " +
					"select r.name " +
					"from resource_owners s, resource r " +
				    "where " +
				   	"s.resource_id = r.id AND "+
				   	"s.user_id = ?2 and " +
				   	"r.id=?1 " +
			   	"),'\\\\/.*$')", nativeQuery=true)
	List<Integer> getResourceChild(int resource_id,int userId);
	
	@Query(value="select r.* " +
			"from resource r ,resource_owners k " +
			   "where " +
			    "k.resource_id=r.id and " +
			   	"k.user_id = ?2 and " +
				"r.name REGEXP CONCAT('^',?1,'\\\\/.*$')", nativeQuery=true)
	List<Resource> getResourceChild(String filename,int yourId);
	
	@Query(value="select r.* " +
			"from resource r ,resource_owners k " +
			   "where " +
			    "k.resource_id=r.id and " +
			   	"k.user_id = ?2 and " +
			   	"r.deleted = 0 and " +
				"r.name REGEXP CONCAT('^',?1,'\\\\/.*$')", nativeQuery=true)
	List<Resource> getResourceChildNoDelete(String filename,int yourId);
	
	@Query(value="select r.* " +
			"from resource r ,resource_owners k " +
			   "where " +
			    "k.resource_id=r.id and " +
			   	"k.user_id = ?2 and " +
				"r.name REGEXP CONCAT('^',?1,'$')", nativeQuery=true)
	List<Resource> getResourceByName(String filename,int yourId);
	
	@Query(value="select r.id " +
			"from resource_owners s, resource r " +
			   "where " +
			   	"s.resource_id = r.id AND "+
			   	"s.user_id = ?1 and " +
			   	"r.deleted = 1 and "+
				"r.name REGEXP CONCAT('^',?2,'\\\\/[A-Za-z0-9_\\\\.\\\\-]+(\\\\.[A-Za-z0-9]+)?$')", nativeQuery=true)
	List<Integer> getYourTrash(int id,String basePath);
	
	@Query(value="select r.id " +
			"from resource_owners s, resource r " +
			   "where " +
			   	"s.resource_id = r.id AND "+
			   	"s.user_id = ?2 and " +
				"r.name REGEXP CONCAT('^',?1,'$')", nativeQuery=true)
	Integer getFileByName(String fileName, int yourId);
	
	
	
}
