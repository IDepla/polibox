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
package it.polito.ai.polibox.service.components;

import it.polito.ai.polibox.service.notification.ServerSentEventInterface;
import it.polito.ai.polibox.service.notification.sse.SSEOnStartAsync;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

@Component
@Scope(value=ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RealTimeNotification implements 
											InitializingBean,
											DisposableBean{

	private ExecutorService executors;
	private int poolDeep;
	private int timeout;//seconds
	private final int timeoutSSE=10000000;
	
	private ConcurrentHashMap<Integer, AsyncContext> contextMap;
	
	public RealTimeNotification() {
		poolDeep=20;
		timeout=10;
		contextMap=new ConcurrentHashMap<Integer, AsyncContext>();
	}

	public void destroy() throws Exception {
		executors.shutdown(); // Disable new tasks from being submitted
	      try {  // Wait a while for existing tasks to terminate
	        if (! executors.awaitTermination(timeout, TimeUnit.SECONDS)) 
	        	executors.shutdownNow(); // Cancel currently executing tasks
	      } catch (InterruptedException ie) {
	    	  executors.shutdownNow();
	        Thread.currentThread().interrupt();
	      }
	      contextMap.clear();
    }

	public void afterPropertiesSet() throws Exception {
		executors=Executors.newFixedThreadPool(poolDeep);
		
	}
	
	public void pushContext(Integer device,AsyncContext ac){
		ac.setTimeout(timeoutSSE);
//		System.out.println("timeout del context="+ac.getTimeout());
		ac.addListener(new AsynchronousListener(device));
		contextMap.put(device, ac);
//		System.out.println("context pushed device="+device);
	}
	
	public void removeContext(Integer device,AsyncContext ac){
		contextMap.remove(device, ac);
		
//		System.out.println("context removed device="+device);
	}
	
	public void removeContext(Integer device){
		contextMap.remove(device);
		
//		System.out.println("context removed device="+device);
	}
	
	
	public void notifica(ServerSentEventInterface nm,Integer deviceId){
		AsyncContext ac=contextMap.get(deviceId);
//		System.out.println("dimensione della contextMap:"+contextMap.size()+"|device "+deviceId+" trovato:"+(ac!=null));
		if(ac!=null){
			if(!nm.getEvent().isEmpty()){
				executors.execute(new NotifyTask(ac, nm, deviceId));
			}
		}
	}
	

	private class NotifyTask implements Runnable{
		private AsyncContext ac;
		private ServerSentEventInterface nm;
		private Integer mapId;
		
		public NotifyTask(AsyncContext ac, ServerSentEventInterface nm,Integer deviceId) {
			this.ac=ac;
			this.nm=nm;
			mapId=deviceId;
		}
		
		public void run() {
//			System.out.println("notifica iniziata device:"+mapId+"|");
			
			try {
				syncronizedWriting(ac.getResponse(), nm.toEvent());
			}catch (JsonProcessingException e){
				return;//se ho problemi nel parsing scarto evento. non dovrebbe mai succedere.
			}catch (IOException e) {
				removeContext(mapId);
//				System.err.println("è partita una eccezzione"+e.toString());
			} 
//			System.out.println("la notifica è terminata timeout:"+ac.getTimeout()+"|device:"+mapId+"|");
		}
	}
	
	private void syncronizedWriting(ServletResponse r,String what) throws IOException{
		synchronized (r) {
			r.getWriter().write(what);
			r.flushBuffer();
		}
	}
	
	private class AsynchronousListener implements AsyncListener{

		private Integer key;
		
		public AsynchronousListener(Integer key) {
			this.key=key;
		}
		
		public void onComplete(AsyncEvent event) throws IOException {
//			syncronizedWriting(event.getAsyncContext().getResponse(), (new SSEOnComplete()).toEvent());
//  //      	System.out.println("complete");
        	removeContext(key);
		}

		public void onTimeout(AsyncEvent event) throws IOException {
//			syncronizedWriting(event.getAsyncContext().getResponse(), (new SSEOnTimeout()).toEvent());
			
//			System.out.println("timeout");
			removeContext(key);
		}

		public void onError(AsyncEvent event) throws IOException {
//			syncronizedWriting(event.getAsyncContext().getResponse(), (new SSEOnError()).toEvent());
			

//			System.out.println("error");
			removeContext(key);
		}

		public void onStartAsync(AsyncEvent event) throws IOException {
			syncronizedWriting(event.getAsyncContext().getResponse(), (new SSEOnStartAsync()).toEvent());
			
//			System.out.println("start");
		}
		
	}
}
