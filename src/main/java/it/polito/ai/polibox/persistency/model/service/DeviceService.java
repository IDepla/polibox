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
package it.polito.ai.polibox.persistency.model.service;

import java.util.List;

import javax.persistence.PersistenceContext;

import javax.persistence.PersistenceContextType;

import org.springframework.stereotype.Service;

import it.polito.ai.polibox.persistency.model.Device;
import it.polito.ai.polibox.persistency.model.NotificationOptionInterface;
import it.polito.ai.polibox.persistency.model.exception.DeviceExistsException;

@Service
@PersistenceContext(type=PersistenceContextType.EXTENDED)
public interface DeviceService {

	public List<Device> getDeviceList(int userId);
	public NotificationOptionInterface[] getDeviceOptions(Device d);
	public Device createDevice(String name, int userId);
	public Device renameDevice(int device,String name,int userId) throws DeviceExistsException;
	public void deleteDevice(int device,int userId) throws DeviceExistsException;

	public Device getDevice(int id,int userId) throws DeviceExistsException;

	
	public void modifyNotificationOption(int deviceId,int userId, int optionId, boolean optionValue) throws DeviceExistsException ;
}
