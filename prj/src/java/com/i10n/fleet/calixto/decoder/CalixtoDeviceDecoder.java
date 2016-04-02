package com.i10n.fleet.calixto.decoder;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.i10n.fleet.container.BulkUpdateDataBean;
import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.module.dataprocessor.DataPool;

/**
 * Decoder class for Calixto devices
 * @author DVasudeva
 *
 */
public class CalixtoDeviceDecoder implements Runnable {

	private static Logger LOG = Logger.getLogger(CalixtoDeviceDecoder.class);
	
	private static final String PERIODIC_PACKET = "$loc";
	
	private Socket clientSocket;
	
	public CalixtoDeviceDecoder(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		try {
			readCalixtoDataPacket();
		} catch (IOException e) {
			LOG.error("Error while reading Calixto packet",e);
		} catch (Exception e){
			LOG.error("",e);
		} finally {
			if(clientSocket != null){
				try {
					clientSocket.close();
				} catch (IOException e) {
					LOG.error("Error while closing the client connection"+e);
				}
			}
		}
	}

	/**
	 * <table>
	 * <tr><td><b>Sl no.</b></td>	<td><b>element name</b></td>			<td><b>no of bytes</b></td><td><b>status</b></td><td><b>remarks</b></td></tr>
	 * <tr><td>1</td>	<td>		$</td>	<td>							1</td>	<td>
	 * <tr><td>2</td>	<td>		loc	</td>	<td>						3</td>	<td>
	 * <tr><td>3</td>	<td>		IMEI</td>	<td>						15</td>	<td>
	 * <tr><td>4</td>	<td>		current time</td>	<td>				6</td>	<td>
	 * <tr><td>5</td>	<td>		current date</td>	<td>				6</td>	<td>
	 * <tr><td>6</td>	<td>		logged date	</td>	<td>				6</td>	<td>
	 * <tr><td>7</td>	<td>		logged time	</td>	<td>				6</td>	<td>
	 * <tr><td>8</td>	<td>		gps data valid	</td>	<td>			1	</td>	<td>		1- gps data are valid ,0-gps data are not valid</td>	
	 * <tr><td>9</td>	<td>		latitude	</td>	<td>				9</td>	<td>
	 * <tr><td>10</td>	<td>		latitude direction</td>	<td>			1</td>	<td>
	 * <tr><td>11</td>	<td>		longitude	</td>	<td>				10</td>	<td>
	 * <tr><td>12</td>	<td>		longitude direction	</td>	<td>		1</td>	<td>
	 * <tr><td>13</td>	<td>		speed(km/hr)</td>	<td>				3</td>	<td>
	 * <tr><td>14</td>	<td>		heading(angle)	</td>	<td>			6</td>	<td>
	 * <tr><td>15</td>	<td>		meter status</td>	<td>				1</td>	<td>			0-Idle, 1-Hired</td>
	 * <tr><td>16</td>	<td>		trip id	</td>	<td>					4</td>	<td>			trip number</td>
	 * <tr><td>17</td>	<td>		gsm signal strength(%)</td>	<td>		2</td>	<td>			gsm  signal strength</td>
	 * <tr><td>18</td>	<td>		panic status	</td>	<td>			1</td>	<td>			0- Normal, 1- Panic</td>
	 * <tr><td>19</td>	<td>		unit cover tamper status</td>	<td>	1</td>	<td>			0- Normal, 1- Tampered</td>
	 * <tr><td>20</td>	<td>		no.of satellites</td>	<td>			2</td>	<td>		 	no.of satellite</td>
	 * <tr><td>21</td>	<td>		packet status	</td>	<td>			1</td>	<td>			0- for current data and 1- for history data</td>
	 * <tr><td>22</td>	<td>		firmware version</td>	<td>		5</td>	<td>
	 * <tr><td>23</td>	<td>		*			</td>	<td>				1</td>	<td>
	 * <tr><td>24</td>	<td>		CR LF	</td>	<td>				2</td>	<td>
	 * <tr><td>			total	</td>	<td>					90</td>	<td>		
	 * </table>
	 * @throws IOException 
	 */
	private void readCalixtoDataPacket() throws IOException {
		LOG.debug("Initializing packet read process");
		DataInputStream in = new DataInputStream(this.clientSocket.getInputStream());
		
		StringBuffer packet = new StringBuffer();
		byte[] oneByte = new byte[1];
		in.read(oneByte);
		String str = new String(oneByte);
		if(str.equals("$")){
			packet.append(str);
			while(!str.equals("*")){
				in.read(oneByte);
				str = new String(oneByte);
				packet.append(str);
			}
		} else {
			LOG.debug("First byte read is not a $ : "+str);
		}
		LOG.debug("Packet String : "+packet.toString());
		String[] packetArray = packet.toString().split(",");
		String packetId = packetArray[0];
		if(packetId != null && packetId.equals(PERIODIC_PACKET)){
			LOG.debug("Its Periodic data packet");
			try {
				processPeriodicPacket(packetArray);
			} catch (IOException e) {
				LOG.error("",e);
			} catch (Exception e){
				LOG.error("", e);
			}
		} else {
			// TODO : Integrate for other packet types ..
			LOG.debug("returning the control as not interested in any other packet type from Calixto device");
			return;
		}
	}

	private void processPeriodicPacket(String[] packetArray) throws IOException, ParseException {
		GWTrackModuleDataBean dataPacket = new GWTrackModuleDataBean();
		dataPacket.setGpsSignalStrength(0);
		dataPacket.setModuleBatteryVoltage(0);
		dataPacket.setGsmSignalStrength(0.0);               
		dataPacket.setPktCount(0);

		dataPacket.setNumberOfPacketSendingAttempts(0);
		dataPacket.setNumberOfSuccessPackets(0);            dataPacket.setCumulativeDistance(0);
		dataPacket.setAnalogue1(0);                        dataPacket.setAnalogue2(0);
		dataPacket.setNumberOfErrorsInHardwareModule(0);    dataPacket.setLocationAreaCode(0);
		dataPacket.setCellId(0);                            dataPacket.setFirmwareVersion(50);
		dataPacket.setModuleUpdateTime(new Date());
		
		String IMEI = packetArray[1];
		dataPacket.setImei(IMEI);
		LOG.debug("IMEI : "+IMEI);
		String currentTimeStr = packetArray[2];
		String currentDateStr = packetArray[3];
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat yy = new SimpleDateFormat( "ddMMyy" );  
        Date currentTime = yy.parse(currentDateStr);
		cal.setTime(currentTime);
		LOG.debug(Integer.valueOf(currentDateStr.substring(4))+", "+(Integer.valueOf(currentDateStr.substring(2, 4))-1)+", "+ Integer.valueOf(currentDateStr.substring(0, 2))+", "+
				Integer.valueOf(currentTimeStr.substring(0, 2))+", "+Integer.valueOf(currentTimeStr.substring(2, 4))+", "+ Integer.valueOf(currentTimeStr.substring(4)));
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE),
				Integer.valueOf(currentTimeStr.substring(0, 2)), Integer.valueOf(currentTimeStr.substring(2, 4)), Integer.valueOf(currentTimeStr.substring(4)));
		currentTime = cal.getTime();
		LOG.debug("CurrentTime : "+currentTime.toString());
		String loggedDateStr = packetArray[4];
		String loggedTimeStr = packetArray[5];
		Date loggedTime = yy.parse(loggedDateStr);
		cal.setTime(loggedTime);
		LOG.debug(Integer.valueOf(loggedDateStr.substring(4))+", "+(Integer.valueOf(loggedDateStr.substring(2, 4))-1)+", "+Integer.valueOf(loggedDateStr.substring(0, 2))+", "+
				Integer.valueOf(loggedTimeStr.substring(0, 2))+", "+Integer.valueOf(loggedTimeStr.substring(2, 4))+", "+Integer.valueOf(loggedTimeStr.substring(4)));
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE),
				Integer.valueOf(loggedTimeStr.substring(0, 2)), Integer.valueOf(loggedTimeStr.substring(2, 4)), Integer.valueOf(loggedTimeStr.substring(4)));
		loggedTime = cal.getTime();
		LOG.debug("LoggedTime : "+loggedTime.toString());
		int gpsDataValid = Integer.valueOf(packetArray[6]);
		LOG.debug("GPSDataValid : "+gpsDataValid);
		double latitudeTBC = Double.parseDouble(packetArray[7]);
		LOG.debug("Latitude : "+latitudeTBC);
		String latitudeDir = packetArray[8];
		LOG.debug("LatitudeDir : "+latitudeDir);
		double longitudeTBC = Double.parseDouble(packetArray[9]);
		LOG.debug("Longitude : "+longitudeTBC);
		String longitudeDir = packetArray[10];
		LOG.debug("LongitudeDir : "+longitudeDir);
		int speed = Integer.valueOf(packetArray[11]);
		LOG.debug("Speed : "+speed);
		dataPacket.setMaxSpeed(speed);
		float heading = Float.valueOf(packetArray[12]);
		LOG.debug("Heading : "+ heading);
		int meterStatus = Integer.valueOf(packetArray[13]);
		LOG.debug("MeterStatus : "+meterStatus);
		int tripId = Integer.valueOf(packetArray[14]);
		LOG.debug("TripID : "+tripId);
		int gpsSignalStrenght = Integer.valueOf(packetArray[15]);
		LOG.debug("GPSSignalStrength : "+gpsSignalStrenght);
		int panicStatus = Integer.valueOf(packetArray[16]);
		LOG.debug("PanicStatus : "+panicStatus);
		dataPacket.setPanicData(panicStatus == 1 ? true: false);
		int unitCoverTamperStatus = Integer.valueOf(packetArray[17]);
		LOG.debug("UnitCoverTamperStatus : "+unitCoverTamperStatus);
		int noOfSatellite = Integer.valueOf(packetArray[18]);
		LOG.debug("NoOfSatellite : "+noOfSatellite);
		int packetStatus = Integer.valueOf(packetArray[19]);
		LOG.debug("PacketStatus : "+packetStatus);
		float firmwareVersion = Integer.valueOf(packetArray[20]);
		LOG.debug("FirmwareVersion : "+firmwareVersion);
		
		double latitudePart1 = (int) latitudeTBC;
		LOG.debug("latitudePart1 : "+latitudePart1);
		double latitudePart2= ((latitudeTBC-latitudePart1)* 100)/60;
		LOG.debug("latitudePart2 :"+latitudePart2);
		double latitude=latitudePart1+latitudePart2;
		LOG.debug("latitude : "+latitude);

		double longitudePart1 = (int) longitudeTBC;
		LOG.debug("longitudePart1 : "+longitudePart1);
		double longitudePart2= ((longitudeTBC-longitudePart1)* 100)/60;
		LOG.debug("longitudePart2 : "+longitudePart2);
		double longitude=longitudePart1+longitudePart2;
		LOG.debug("longitude : "+longitude);
		
		BulkUpdateDataBean bulkData = new BulkUpdateDataBean(latitude, longitude, 0, loggedTime, 0, speed, heading);
		bulkData.setImei(IMEI);
		ArrayList<BulkUpdateDataBean> bulkUpdateData = new ArrayList<BulkUpdateDataBean>();
		bulkUpdateData.add(bulkData);
		dataPacket.setBulkUpdateData(bulkUpdateData);
		
		LOG.info("Bean Data From Calixto device is:"+dataPacket.toString());
		DataPool.addPacket(dataPacket);
	}
	
	/**
	 * The first 4 byte defines the type of the packet 
	 * String 		PacketType
	 * $loc			periodic data
	 * $alt			event based data
	 * $alm			alarm data
	 * $hth			health status data
	 * $mtr			trip metering data
	 * $sim			sim card information data
	 * @param in
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private String readPacketIdentifier(DataInputStream in) throws IOException {
		byte[] dollar=new byte[1];
		in.read(dollar);
		LOG.debug("First byte : "+new String(dollar));
		byte[] packetIdByteArray=new byte[3];
		in.read(packetIdByteArray);
		String packetId = new String(packetIdByteArray);
		LOG.debug("PakcetID read : "+packetId);
		return packetId;
	}

}
