package com.i10n.fleet.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.i10n.db.entity.Address;
import com.i10n.db.entity.Address.ADDRESS_STATUS_ENUM;

/**
 * 
 * Location Data Retriever uses threads to pull data from Google
 * Address DB
 * 
 * @author vishnu
 *
 */
public class AddressRetrieverEngine {

	private static final Logger LOG = Logger.getLogger(AddressRetrieverEngine.class);

	public static BlockingQueue<Address> addressFetchPool  = new ArrayBlockingQueue<Address>(3000);

	private static AddressRetrieverEngine _instance = null;
	ThreadPoolExecutor executorService = null;
	Thread thread;
	private final int threadPoolSize;
	int threadId = 0;

	public static boolean shutdownInitiated = false;

	private AddressRetrieverEngine(){
		/* As the features and processes are being plugged to PushToDB and hence the size of the pool has to be increased.*/
		threadPoolSize = 100;	 
		executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPoolSize);
		initialize();
	}

	public static AddressRetrieverEngine getInstance () {
		if (_instance == null) {
			_instance = new AddressRetrieverEngine();
		}
		return _instance;
	}

	private void initialize() {
		for(int i=0; i< threadPoolSize; i++){
			try {
				executorService.execute(new AddressRetriever(threadId++));
			} catch (Exception e) {
				LOG.error(e);
			}
		}	        
	}

	public void stop(){
		shutdownInitiated = true;
		AddressRetrieverEngine.addressFetchPool.clear();
		kill();
	}

	/**
	 * Kills the executor threads
	 */
	private void kill(){
		executorService.shutdown();
		try {
			LOG.warn("IN kill method of AddressRetrieverEngine");
			// Wait a while for existing tasks to terminate
			if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
				LOG.warn("IN kill method of if loop AddressRetrieverEngine");
				executorService.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
					LOG.warn("Pool didn't termitate");
				}
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			executorService.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
}


/**
 * Fetches address
 *
 */
class AddressRetriever implements Runnable {

	//private static final String OpenGIS_URL="http://open.mapquestapi.com/geocoding/v1/reverse?key=Fmjtd|luub2h0721%2Crw%3Do5-9utxgy&callback=renderReverse&location=12.9205872,76.5678";

	public static Logger LOG = Logger.getLogger(AddressRetriever.class);

	private static int googleHit=0;

	int threadId;

	/**
	 * Constructor
	 * @param threadId
	 */
	public AddressRetriever (int threadId) {
		this.threadId = threadId;
	}

	/**
	 * Thread which processes the packets.
	 */
	@Override
	public void run() {
		Date startTime, endTime;
		while (!AddressRetrieverEngine.shutdownInitiated) {	
			/* Fetch the dataPacket from the DataPool and use that datapacket to store the data into the Database..*/
			Address addressRequest;
			try {
				addressRequest = AddressRetrieverEngine.addressFetchPool.take();

				LOG.debug("Pending address fetch requests : "+AddressRetrieverEngine.addressFetchPool.size());

				startTime = new Date();
				addressRequest = fetchNearestLocationFromGoogle(addressRequest);
				endTime = new Date();

				LOG.debug("Fetching location from Google took (ms) = "+(endTime.getTime() - startTime.getTime())+" fetch status is "+(addressRequest.getFetchStatus() != ADDRESS_STATUS_ENUM.FETCHED));
				GeoUtils.InsertInToDb(addressRequest.getLat(), addressRequest.getLng(), addressRequest);
				/*else {
					startTime = new Date();
					addressRequest = fetchNearestLocationFromYahoo(addressRequest);
					endTime = new Date();
					LOG.debug("Fetching location from Yahoo took (ms) = "+(endTime.getTime() - startTime.getTime()));
					if(addressRequest != null)
						GeoUtils.InsertInToDb(addressRequest.getLat(), addressRequest.getLng(), addressRequest);
				}*/
			} catch (InterruptedException e) {
				// intentionally ignored
			} catch (Exception e){
				LOG.error(e);
			}
		}
	}	

	/**
	 * Gets array from string
	 * @param s
	 * @return
	 */
	public static byte[] getByteArrayFromString(String s) {
		char ac[];
		byte abyte0[] = new byte[(ac = s.toCharArray()).length];
		for(int i = 0; i < ac.length; i++)
			abyte0[i] = (byte) ac[i];

		return abyte0;
	}

	/**
	 * Sends request using proxy
	 * 
	 * @param proxyServer
	 * @param proxyPort
	 * @param urlString
	 * @return
	 */
	/*public static String sendGetRequestUsingProxy(String proxyServer, int proxyPort, String urlString)   {
		Socket socket = null;
		DataOutputStream dos = null;
		BufferedReader br = null;
		String last="", secondLast="";
		try {
			socket = new Socket(proxyServer, proxyPort);
			dos = new DataOutputStream(socket.getOutputStream());

			int i = urlString.indexOf('/', 7);
			String s1 = urlString.substring(7, i);
			String s2 = "GET " + urlString + " HTTP/1.1\r\n";
			s2 = s2 + "Host: " + s1 + "\r\n";
			s2 = s2 + "Connection: close\r\n";
			s2 = s2 + "User-Agent: Mozilla/5.0\r\n\r\n";

			byte abyte0[] = getByteArrayFromString(s2);
			dos.write(abyte0);            
			dos.flush();
			String line;

			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while ((line = br.readLine()) != null) {
				if (line.length()>3) {
					last = line;
				}
			}
		} catch (UnknownHostException e) {
			LOG.error("Host doesnt exist",e);
		} catch (ConnectException e){
			LOG.error("Problem with connecting to the proxy server",e);
		} catch (IOException e) {
			LOG.error("Other IO exception",e);
		} catch (Exception e){
			LOG.error("Other exceptions", e);
		}
		finally {
			try {
				br.close();
			} catch (Exception e) {
				//ignore
			};
			try {
				dos.close();
			} catch (Exception e) {
				//ignore
			};
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e) {
					LOG.error(e);
				}
			}
		}
		return last;
	}*/


	/**
	 * Method to fetch address from Google
	 * @param x
	 * @param y
	 * @return
	 */
	public static Address fetchNearestLocationFromGoogleUsingProxy(Address address) {
		Calendar cal = Calendar.getInstance();
		if(googleHit<2300){
			//Use Google_URL
			if(cal.get(Calendar.HOUR_OF_DAY)==0){
				googleHit=0;	
				LOG.info("googleHit Count "+googleHit);
			}else{
				googleHit++;
				LOG.info("GoogleHit Connection Count"+googleHit);
			}
			return fetchAddressUsingGoogle(address.getLat(),address.getLng());
		}else{
			return fetchAddressUsingOpenGIS(address.getLat(),address.getLng());
		}
	}


	@SuppressWarnings("deprecation")
	private static Address fetchAddressUsingOpenGIS(double x, double y) {
		Address address = new Address();
		try{
			String OpenGIS_URL="http://open.mapquestapi.com/geocoding/v1/reverse?key=Fmjtd|luub2h0721%2Crw%3Do5-9utxgy&callback=renderReverse&location="+ x + "," + y;
			URL url=new URL(OpenGIS_URL);
			LOG.info("OpenGIS_URL"+OpenGIS_URL);
			try{
				URLConnection connection=url.openConnection();
				connection.setConnectTimeout(1000);
				InputStream is=connection.getInputStream();
				DataInputStream bis=new DataInputStream(is);
				String string="";
				StringBuffer stb=new StringBuffer();
				while ((string = bis.readLine())!=null) {
					stb.append(string);
				}
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject = new JSONObject(stb.toString().substring(stb.indexOf("{")));
					//System.out.println("Json Object"+jsonObject);
					JSONObject ss=jsonObject.getJSONArray("results").getJSONObject(0);
					LOG.debug("JSON Object"+ss);
					JSONObject location=ss.getJSONArray("locations").getJSONObject(0);
					String street=location.getString("street");
					String city=location.getString("adminArea5");
					String district=location.getString("adminArea4");
					String state=location.getString("adminArea3");
					address.setLine1(street);
					address.setLine2(city);
					address.setLine3(district+","+state);
					address.setLine4("India");
					address.setLat(x);
					address.setLng(y);
					address.setState(state);
					address.setStreet(street);
					address.setCountry("India");
					address.setFetchStatus(ADDRESS_STATUS_ENUM.FETCHED);
					is.close();
					bis.close();
					LOG.info("OpneGIS Address Fetch "+address.toString());
				}
				catch (JSONException e) {
					e.printStackTrace();
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return address;	
	}

	@SuppressWarnings("deprecation")
	private static Address fetchAddressUsingGoogle(double x, double y) {
		LOG.debug("fetchAddressUsingGoogle ");
		Address address=new Address();
		try{
			String Google_URL=("http://maps.googleapis.com/maps/api/geocode/json?latlng="+ x + "," + y +"&sensor=true");
			URL url=new URL(Google_URL);
			LOG.info("Google_URL"+Google_URL);
			try{
				URLConnection connection=url.openConnection();
				connection.setConnectTimeout(1000);
				InputStream response=connection.getInputStream();
				DataInputStream responseDataInPutstream=new DataInputStream(response);
				String string="";
				StringBuffer addressBuilder=new StringBuffer();
				while ((string = responseDataInPutstream.readLine())!=null) {
					addressBuilder.append(string);
				}
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject = new JSONObject(addressBuilder.toString());
					if(!jsonObject.getString("status").equals("OVER_QUERY_LIMIT") || jsonObject.getString("status").equals("OK")){
						LOG.debug("Json Object is "+jsonObject.toString());
						if(jsonObject.getJSONArray("results")!=null){
							LOG.debug("Results data is : "+jsonObject.getJSONArray("results"));
							JSONObject  jsonData=jsonObject.getJSONArray("results").getJSONObject(0);
							String formattedAddress = jsonData.getString("formatted_address");
							String[] splitAddress=formattedAddress.split(",");
							if(splitAddress.length>0){
								address.setCountry(splitAddress[splitAddress.length - 1].trim());
								address.setLine4(splitAddress[splitAddress.length - 1].trim());
								LOG.debug("Country "+splitAddress[splitAddress.length - 1].trim());
							}
							if (splitAddress.length > 1) {
								address.setLine3(splitAddress[splitAddress.length - 2].trim());
								address.setState(splitAddress[splitAddress.length - 2].trim());
								LOG.debug("State "+splitAddress[splitAddress.length - 2].trim());
							}
							if (splitAddress.length > 2) {
								address.setLine2(splitAddress[splitAddress.length - 3].trim());
								LOG.debug("City "+splitAddress[splitAddress.length - 3].trim());
							}
							StringBuilder remAddress = new StringBuilder();
							if (splitAddress.length >=4) {
								for(int m=0;m<splitAddress.length-3;m++){
									remAddress.append(splitAddress[m]);
									remAddress.append(",");
								}
							}
							if(remAddress!=null){
								address.setLine1(remAddress.toString());
								address.setStreet(remAddress.toString());
								LOG.debug("Address Line1 is not null "+remAddress.toString());
							}
							address.setLat(x);
							address.setLng(y);
							address.setFetchStatus(ADDRESS_STATUS_ENUM.FETCHED);
						}
					}else{
						LOG.error("Address empty");
					}
					responseDataInPutstream.close();
					response.close();
				} catch (JSONException e) {
					LOG.error("Error while converting to Json object",e);
				}
			}catch (IOException e) {
				// TODO Auto-generated catch block
				LOG.error("Error while reading reposonse",e);
			}
		} catch (Exception e) {
			LOG.error("Error while fetching location from Google ",e);
			return null;
		}
		LOG.info("ADDRESS : address toString is "+address.toString());
		return address;
	}



	/**
	 * Method to fetch address from Google
	 * @param x
	 * @param y
	 * @return
	 */
	public static Address fetchNearestLocationFromGoogle(Address address) {
		LOG.debug("fetchNearestLocationFromGoogle");
		return fetchNearestLocationFromGoogleUsingProxy(address); 
	}

	/**
	 * 
	 * Gets a point and returns the nearest location from Yahoo
	 * 
	 * @param x
	 *            - X-Coordinate of the point
	 * @param y
	 *            - Y-Coordinate of the point
	 * 
	 * @returns the nearest location of location specified
	 */
	public static Address fetchNearestLocationFromYahoo(Address address) {
		double x = address.getLat();
		double y = address.getLng();
		try {
			// Send data
			// http://in.maps.yahoo.com/services/1.0.0/onebox?locale=en_IN&oflags=J&dirflags=XLH&q=12.86054824561739,77.66355514526367
			// java.net.URL url = new
			// java.net.URL("http://in.maps.yahoo.com/php/ymi_geocode.php");
			java.net.URL url = new java.net.URL("http://where.yahooapis.com/geocode?gflags=R&flags=J&appid=100&q="+ x + "," + y);

			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(1000);

			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;

			while ((line = rd.readLine()) != null) {
				JSONObject jso = new JSONObject(line);
				JSONArray resultSet = jso.getJSONObject("ResultSet").getJSONArray("Results");
				if (null != resultSet && null != resultSet.get(0)) {
					JSONObject currResult = (JSONObject) resultSet.get(0);

					address.setLine1(currResult.getString("line1"));
					address.setLine2(currResult.getString("line2"));
					address.setLine3(currResult.getString("line3"));
					address.setLine4(currResult.getString("line4"));
					address.setLat(x);
					address.setLng(y);
					address.setState(currResult.getString("state"));
					address.setStreet(currResult.getString("street"));
					address.setCountry(currResult.getString("country"));
					address.setFetchStatus(ADDRESS_STATUS_ENUM.FETCHED);

					return address;
				} else {
					return null;
				}
			}
			wr.close();
			rd.close();
		} catch (Exception e) {
			LOG.error("Error while fetching location from Yahoo server",e);
		}
		return null;
	}

	/**
	 * Method to fetch address from Google
	 * @param x
	 * @param y
	 * @return
	 */
	public static Address fetchNearestLocationFromGoogleTemp(Address address) {		
		double x = address.getLat();
		double y = address.getLng();
		try {
			String urlString = "http://maps.google.com/maps/geo?output=csv&oe=utf-8&ll="+ x + "," + y;

			java.net.URL url = new java.net.URL(urlString);

			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(1000);

			conn.setDoOutput(true);

			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;

			while ((line = rd.readLine()) != null) {
				LOG.debug("Google address fetch for lat"+address.getLat()+" lng "+address.getLng()+" result "+line);
				String[] lines = line.split("\"");

				if (lines.length<2) {
					return address;
				}

				String[] splitAddress = lines[1].split(",");

				if (splitAddress.length > 0) {
					address.setCountry(splitAddress[splitAddress.length - 1].trim());
					address.setLine4(splitAddress[splitAddress.length - 1].trim());
				}
				if (splitAddress.length > 1) {
					address.setLine3(splitAddress[splitAddress.length - 2].trim());
					address.setState(splitAddress[splitAddress.length - 2].trim());
				}
				if (splitAddress.length > 2) {
					address.setLine2(splitAddress[splitAddress.length - 3].trim());
				}

				StringBuilder remAddress = new StringBuilder();

				if (splitAddress.length > 3) {
					for (int i = 0; i < splitAddress.length - 4; i++) {
						remAddress.append(splitAddress[i]);
						if (i != splitAddress.length - 4) {
							remAddress.append(",");
						}
					}
					address.setLine1(remAddress.toString());
				}
			}
			address.setFetchStatus(ADDRESS_STATUS_ENUM.FETCHED);
			rd.close();
		} catch (IOException ioe) {
			LOG.error("Error while fetching location from Google ",ioe);
		} catch (Exception e) {
			LOG.error("Error while fetching location from Google ",e);
		}
		return address;
	}


}
