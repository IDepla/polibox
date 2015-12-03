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
import it.polito.ai.polibox.persistency.model.Sharing;
import it.polito.ai.polibox.persistency.model.SharingId;
import it.polito.ai.polibox.persistency.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface SharingDao extends JpaRepository<Sharing, SharingId> {


	

	@Query("select sh " +
			"from Sharing sh " +
			"where " +
			"sh.pk.userTarget.id=?1 and " +
			"sh.requestAccepted=1")
	List<Sharing> getResourceSharedByTargetUserId(String pathParent,String pathMy,int resourceId,int targetUserId);
	
	@Query("select sh " +
			"from Sharing sh " +
			"where " +
			"sh.pk.userTarget=?1 and " +
			"sh.requestAccepted=1")
	List<Sharing> getResourceSharedByTargetUser(User u);
	
	@Query("select sh " +
			"from Sharing sh " +
			"where " +
			"sh.pk.userTarget.id=?1 and " +
			"sh.requestAccepted=0")
	List<Sharing> getPendingShareByTargetUserId(int id);
	
	@Query("select sh " +
			"from Sharing sh " +
			"where " +
			"sh.pk.userTarget=?1 and " +
			"sh.requestAccepted=0")
	List<Sharing> getPendingShareByTargetUser(User u);
	
	@Query("select sh " +
			"from Sharing sh " +
			"where " +
			"sh.requestAccepted=0 and "+
			"sh.pk.sharingResource.pk.user.id=?1")
	List<Sharing> getOwnerPendingSharingByUserId(int id);
	
	@Query("select sh " +
			"from Sharing sh " +
			"where " +
			"sh.requestAccepted=0 and "+
			"sh.pk.sharingResource.pk.user=?1")
	List<Sharing> getOwnerPendingSharingByUser(User u);
	
	@Query("select s " +
			"from Sharing s " +
			"where s.pk.sharingResource.pk.resource=?1")
	List<Sharing> getSharingByResource(Resource r);

	@Query("select s " +
			"from Sharing s " +
			"where " +
			"s.pk.sharingResource.pk.resource=?1 and " +
			"s.requestAccepted=1")
	List<Sharing> getAcceptedSharingByResource(Resource r);
	
	@Query("select s " +
			"from Sharing s " +
			"where " +
			"s.pk.sharingResource.pk.resource=?1 and " +
			"s.pk.userTarget=?2")
	List<Sharing> getSharingByResourceAndTarget(Resource r, User u);
	

}
