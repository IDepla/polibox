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
package it.polito.ai.polibox.service.impl;

import it.polito.ai.polibox.persistency.model.service.NotificationService;
import it.polito.ai.polibox.service.MasterNotificationService;
import it.polito.ai.polibox.service.components.EMailService;
import it.polito.ai.polibox.service.components.RealTimeNotification;
import it.polito.ai.polibox.service.notification.NotificationMessageInterface;
import it.polito.ai.polibox.service.notification.sse.ServerCommandEvent;
import it.polito.ai.polibox.service.notification.sse.ServerMessageEvent;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component 
public class MasterNotificationServiceImpl implements MasterNotificationService{

	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private RealTimeNotification realTimeNotification;
	
	public NotificationService getNotificationService() {
		return notificationService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public RealTimeNotification getRealTimeNotification() {
		return realTimeNotification;
	}

	public void setRealTimeNotification(RealTimeNotification realTimeNotification) {
		this.realTimeNotification = realTimeNotification;
	}

	public MasterNotificationServiceImpl() {
	}

	/**
	 * ci sono tre tipi di notifica:
	 * -notifica nel database --> salvo
	 * -notifica per email --> spedisco email
	 * -notifica nel device --> websocket
	 */
	public void notifica(NotificationMessageInterface msg) {
		int i=0;
		notificationService.create(msg, msg.getUser());//notificato utente 
		List<Integer> d=notificationService.getDevicesToNotify(msg);
		List<String> s=notificationService.getEmailsToNotify(msg);
		
		for(i=0;i<d.size();i++){//notifica ai device
			realTimeNotification.notifica(new ServerMessageEvent(msg), d.get(i));//notifica Messaggio
			
			realTimeNotification.notifica(new ServerCommandEvent(msg), d.get(i));//notifica comando
		}
		
		for(i=0;i<s.size();i++){//gestisce la spedizione email all'utente
			sendEmail(msg,s.get(i));
		}
	}
	
	private void sendEmail(NotificationMessageInterface msg,String targetEmail){
		EMailService.send(targetEmail, "polibox notification", msg.getMessage());
	}
}
