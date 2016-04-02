/*package com.i10n.navteq;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.PriorityQueue;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import com.mchange.lang.ByteUtils;

*//**
 * Handling navteq data to be pushed
 * @author Dharmaraju V
 *
 *//*
public class NavteqDataPushHelper {

	private static Logger LOG = Logger.getLogger(NavteqDataPushHelper.class);
	
	private static PriorityQueue<IoBuffer> disQueue = new PriorityQueue<IoBuffer>();

	public static void sendDataToNavteqStagingBox(IoBuffer dis){
		disQueue.add(dis);
		if(disQueue.size() >= 150){
			LOG.debug("Navteq : Sending data to the Staging box");
			for(int i =0 ; i< disQueue.size(); i++){
				try {
					Socket navteqSocket = new Socket(InetAddress.getLocalHost(), 8070);
					ByteArrayOutputStream outStream = new ByteArrayOutputStream();
					DataOutputStream responseDataOS = new DataOutputStream(outStream);
					PrintWriter out = new PrintWriter(navteqSocket.getOutputStream(), true);
					out.write(disQueue.remove().toString());
					responseDataOS.write(ByteUtils.fromHexAscii(dis.toString()));
					Thread.sleep(5000);
					responseDataOS.close();
					outStream.close();
					navteqSocket.close();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
			disQueue.clear();
		}else{
			LOG.debug("Navteq : Size has not yet reached 150. Current Size is : "+disQueue.size());
		}
	}

}
*/