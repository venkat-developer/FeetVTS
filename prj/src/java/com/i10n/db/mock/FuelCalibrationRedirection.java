package com.i10n.db.mock;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import com.i10n.fleet.container.GWTrackModuleDataBean;

/**
 * Mock class to redirect packet from specific IMEI to demo server
 * 
 * This class has to be deleted in near future
 * 
 * @author Dharmaraju V
 *
 */
public class FuelCalibrationRedirection {
	
	private static Logger LOG = Logger.getLogger(FuelCalibrationRedirection.class);
	
	public void redirectData(IoBuffer dis, GWTrackModuleDataBean dataBean) {
		
		LOG.debug("Redirecting the following Packet : "+dataBean.toString());
		Socket socket = null;
		ObjectOutputStream oos = null;
		try {
			socket = new Socket("demo.gwtrack.com", 8060);
			socket.setSoTimeout(5000);
			LOG.debug("Opened socket connection with demo.gwtrack.com server for fuel calibration redirection");
			oos = new ObjectOutputStream(socket.getOutputStream());
			byte[] byteArray = new byte[dis.remaining()];
			for(int i = 0 ; dis.remaining() != 0; i++){
				byteArray[i] = dis.get();
			}
			oos.write(byteArray);
			oos.flush();
		} catch (UnknownHostException e) {
			LOG.error("Error while redirecting ",e);
		} catch (IOException e) {
			LOG.error("Error while opening the connection",e);
		} catch(Exception e){
			LOG.error("Catching other exception while redirecting",e);
		} finally {
			try {
				if(oos != null)
					oos.close();
				if(socket != null)
					socket.close();
			} catch (IOException e) {
				LOG.error("Error while closing the connection",e);
			}
			
		}
		
	}
	
}
