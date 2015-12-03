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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.polito.ai.polibox.persistency.model.Device;
import it.polito.ai.polibox.persistency.model.User;

@Repository
@Transactional
public interface DeviceDao extends JpaRepository<Device, Integer>{

	@Query("select d " +
			"from Device d " +
			"where " +
			"d.owner=?1 " +
			"order by d.device_name")
	 List<Device> getMyDevices(User userId);
	
	@Query("select d " +
			"from Device d " +
			"where " +
			"d.owner=?1 and " +
			"d.id = ?2")
	Device getDevice(User u, int id);
	
	@Query("select d " +
			"from Device d " +
			"where " +
			"d.owner.id=?1 and " +
			"d.deviceDeletable = 0")
	Device getBrowserDevice(int userId);
	
	@Query("select d " +
			"from Device d " +
			"where " +
			"d.owner=?1 and " +
			"d.deviceDeletable = 0")
	Device getBrowserDevice(User user);
	
	@Query("select d " +
			"from Device d " +
			"where " +
			"d.owner=?1 and " +
			"d.autoAuthenticationKey = ?2")
	Device getDeviceByUserAndKey(User user,String key);
}
