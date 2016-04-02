package com.i10n.fleet.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.postgis.Point;

import com.i10n.db.dao.AddressDaoimpl;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.entity.Address;
import com.i10n.db.entity.Directions;
import com.i10n.db.tools.DBManager;

/**
 * @author Varun
 * @update Dharmaraju V
 */
public class GeoUtils {

	private static Logger LOG = Logger.getLogger(GeoUtils.class);

	private static final double PI = 3.1415926;
	private static final double RAD_PER_DEG = PI / 180.0D;

	private static int currentZoomLevel = -1;

	private static double circum_px;
	private static double nTilesY;
	private static double xPerLon;
	private static double pixelHeight;

	/**
	 * Initializes variables with respect to zoom level
	 * 
	 * @param zoomLevel
	 */
	public static void initialize(int zoomLevel) {
		currentZoomLevel = zoomLevel;
		circum_px = 1 << (26 - zoomLevel);
		nTilesY = circum_px / 256;
		xPerLon = circum_px / 360.0;
		pixelHeight = nTilesY * 256;
	}

	/**
	 * 
	 * Gets a point and a polygon and checks if the point is inside the polygon
	 * 
	 * @param pointX
	 *            - X-Coordinate of the point
	 * @param pointY
	 *            - Y-Coordinate of the point
	 * @param polygonX
	 *            - Array of X-Coordinates of the polygon
	 * @param polygonY
	 *            - Array of Y-Coordinates of the polygon
	 * 
	 * @returns true if the point is inside the polygon; false if the point is
	 *          outside the polygon
	 */
	public static boolean pointInPolygon(double pointX, double pointY,
			double[] polygonX, double[] polygonY) {
		LOG.debug("GEO:::: Checking Weather in pointInPolygon region or not");
		boolean oddNodes = false;
		try {
			int i = 0, j = polygonX.length - 1;
			for (i = 0; i < polygonX.length; i++) {
				if ((polygonY[i] < pointY && polygonY[j] >= pointY) || (polygonY[j] < pointY && polygonY[i] >= pointY)) {
					if (polygonX[i] + (pointY - polygonY[i]) / (double) (polygonY[j] - polygonY[i]) * (polygonX[j] - polygonX[i]) < pointX) {
						oddNodes = !oddNodes;
						LOG.debug("GEO:::: Vehicle in pointInPolygon");
					}
				}
				j = i;
				LOG.debug("GEO:::: Vehicle is not Polygon ");
			}
		} catch (Exception ex) {
			LOG.debug("Error while checking point in polygon!");
			return false;
		}
		return oddNodes;
	}

	/**
	 * Gets a Point and Square and checking wheather point is inside the Square...
	 * @param points
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public static boolean isInsideSquareRegion(Point[] points, double latitude, double longitude) {
		Point currentPoint = new Point(latitude, longitude);
		LOG.debug("GEO:::: Checking vehicle Weather in Square region or not ");
		if (currentPoint.x > points[2].x && currentPoint.x < points[0].x) {
			if (currentPoint.y > points[2].y && currentPoint.y < points[0].y) {
				LOG.debug("GEO:::: Vehicle is in Square region");
				return true;
			}
		}
		LOG.debug("GEO:::: Vehicle is not in Square Region");
		return false;
	}

	/**
	 * This method identifies whether the Vehicle(Current Lat Lon) is inside the
	 * Circular Region Or Not
	 * 
	 * @param points
	 * @return boolean
	 */
	public static boolean isInsideCircleRegion(Point[] points, double latitude, double longitude) {
		// Finding the Radius of the Circle - here dist is the radius of the circle
		// Here point x,y Denotes the Latitude ,Longitude (X - Latitude , Y - Longitude)

		double radiusOfTheGeoFenceCircle = CustomCoordinates.distance(points[0], points[1]);// Point[0]is centre of the Circle
		// Point[1] is the point on Circumference
		LOG.debug("GEO:::: Checking Weather in circle region or not ");
		Point currentPoint = new Point(latitude, longitude);
		double distanceOfCurrentPointFromCircleCentre = CustomCoordinates.distance(points[0], currentPoint);
		LOG.debug("GEO:::: Actual dist in circle: "+ radiusOfTheGeoFenceCircle + " Dist b/w pt and center: "+ distanceOfCurrentPointFromCircleCentre);
		LOG.debug("GEO:::: Center Lattitude: " + points[0].x + " Center Longitude: "	+ points[0].y);
		LOG.debug("GEO:::: End Lattitude: " + points[1].x + " End Longitude: " + points[1].y);
		LOG.debug("GEO:::: Current Lattitude: " + latitude + " Current Lng: "+ longitude);
		if (radiusOfTheGeoFenceCircle < distanceOfCurrentPointFromCircleCentre) {			// Geo Fence Violated
			LOG.debug("GEO:::: Vehicle is not in Circle Region");
			return false;
		} else {																			// Geo Fence Not Violated
			LOG.debug("GEO:::: Vehicle is In Circle Region");
			return true;
		}
	}

	/**
	 * 
	 * Gets a point and returns the nearest location (within 500m) from database only
	 * 
	 * Eventually non priority calls should go to this method
	 * 
	 * @param x
	 *            - X-Coordinate of the point
	 * @param y
	 *            - Y-Coordinate of the point
	 * 
	 * @returns the nearest location of location specified
	 */
	public static Address fetchNearestLocationFromDB (double lat, double lon) {
		return fetchNearestLocation(lat, lon, false);
	}

	/**
	 * 
	 * Gets a point and returns the nearest location (within 500m) from the
	 * cached data
	 * 
	 * @param x
	 *            - X-Coordinate of the point
	 * @param y
	 *            - Y-Coordinate of the point
	 * 
	 * @returns the nearest location of location specified
	 */
	public static Address fetchNearestLocation(double lat, double lon, boolean fetchAsync) {
		Address address = null;
		
		try {
			address = fetchNearestLocationFromCache(lat, lon);
			if (address != null) {
				return address;
			} else {
				if (fetchAsync) {
					/** Queues fetch **/
					address = new Address(lat,lon);
					AddressRetrieverEngine.addressFetchPool.add(address);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching nearest location ",e);
		}
		return address;
	}

	/**
	 * 
	 * Gets a point and returns the nearest location (within 500m) from the
	 * cached data
	 * 
	 * @param x
	 *            - X-Coordinate of the point
	 * @param y
	 *            - Y-Coordinate of the point
	 * 
	 * @returns the nearest location of location specified
	 */
	public static Address fetchNearestLocationFromCache(double lat, double lon) {
		Address address = null;
		try{
			Date addressStartDate=new Date();
			List<Address> addressList=((AddressDaoimpl) DBManager.getInstance().getDao(DAOEnum.ADDRESS_DAO)).fetchLocationfromDB(lat,lon);
			Date addressEndDate =new Date();
			LOG.info("Address fetch took "+(addressEndDate.getTime()-addressStartDate.getTime())+" Ms");
			if(addressList.size() != 0){
				address = addressList.get(0);
				}
			else{
				/** Returning Null */
			}
		}
		catch (Exception e) {
			LOG.error("Error  while  fetching lat lon points from database",e);
		}
		return address;
	}




	public static void InsertInToDb(double lat1, double lon1, Address address) {

		if(lat1 == 0 || lon1 ==0){
			LOG.debug("lat or lon 0.. not inserting value into db");
			return;
		}
		try {
			String line1 = StringUtils.removeSpecialCharacter(address.getLine1());
			String line2 = StringUtils.removeSpecialCharacter(address.getLine2());
			String line3 = StringUtils.removeSpecialCharacter(address.getLine3());
			String line4 = StringUtils.removeSpecialCharacter(address.getLine4());
			String street = StringUtils.removeSpecialCharacter(address.getStreet());
			String state = StringUtils.removeSpecialCharacter(address.getState());
			String country = StringUtils.removeSpecialCharacter(address.getCountry());

			Address address1 = new Address();

			if (null != address) {
				address1.setLat(lat1);
				address1.setLng(lon1);
				address1.setLine1(line1);
				address1.setLine2(line2);
				address1.setLine3(line3);
				address1.setLine4(line4);
				address1.setStreet(street);
				address1.setState(state);
				address1.setCountry(country);
				address1.setLatlon_point(new Point(lon1,lat1));
				address = ((AddressDaoimpl) DBManager.getInstance().getDao(DAOEnum.ADDRESS_DAO)).insert(address1);
			}
		}
		catch (Exception e) {
			LOG.debug("Error while inserting values",e);
		}
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
	public static Address fetchNearestLocationFromYahoo(double x, double y) {
		Address address = new Address ();
		address.setLat(x);
		address.setLng(y);
		return AddressRetriever.fetchNearestLocationFromYahoo(address);
	}

	/**
	 * Fetch nearest location from Google 
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Address fetchNearestLocationFromGoogle(double x, double y) {
		Address address = new Address (x, y);
		return AddressRetriever.fetchNearestLocationFromGoogle(address);
	}


	/**
	 * 
	 * Gets two points and returns the driving directions
	 * 
	 * @param startX
	 *            - X-Coordinate of the starting point
	 * @param startY
	 *            - Y-Coordinate of the starting point
	 * @param endX
	 *            - X-Coordinate of the ending point
	 * @param endY
	 *            - Y-Coordinate of the ending point
	 * 
	 * @returns the nearest location of location specified
	 */
	public static Directions fetchDirections(double startX, double startY, double endX, double endY) {
		try {
			Directions directions = null;
			directions = fetchDirectionsFromYahoo(startX, startY, endX, endY);
			return directions;
		} catch (Exception e) {
			LOG.debug("Error while fetching directions ",e);
		}
		return null;
	}

	/**
	 * 
	 * Gets two points and returns the driving directions from Yahoo
	 * 
	 * @param startX
	 *            - X-Coordinate of the starting point
	 * @param startY
	 *            - Y-Coordinate of the starting point
	 * @param endX
	 *            - X-Coordinate of the ending point
	 * @param endY
	 *            - Y-Coordinate of the ending point
	 * 
	 * @returns the nearest location of location specified
	 */
	public static Directions fetchDirectionsFromYahoo(double startX,
			double startY, double endX, double endY) {
		try {
			// Construct data
			String query = String.valueOf(startX) + ","+ String.valueOf(startY) + "," + String.valueOf(endX) + ","+ String.valueOf(endY);
			String data = URLEncoder.encode("ll", "UTF-8") + "="+ URLEncoder.encode(query, "UTF-8");

			// Send data
			java.net.URL url = new java.net.URL("http://in.maps.yahoo.com/php/ymi_findroute.php");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String response = "";
			String line;
			while ((line = rd.readLine()) != null) {
				response += line;
				JSONObject jso = new JSONObject(line);
				JSONObject resultSet = jso.getJSONObject("ResultSet");
				if (null != resultSet) {
					if (!resultSet.isNull("Result")) {
						JSONObject result = resultSet.getJSONObject("Result");
						JSONObject yahooDirections = result.getJSONObject("yahoo_driving_directions");
						if (null != yahooDirections) {
							Directions directions = new Directions();
							directions.setOrigin(new Point(startX, startY));
							directions.setDestination(new Point(endX, endY));
							directions.setDistance(yahooDirections.getLong("total_distance"));
							List<Point> route = new ArrayList<Point>();
							JSONObject jsoDirections = yahooDirections.getJSONObject("directions");
							if (null != jsoDirections) {
								JSONArray routeArray = jsoDirections.getJSONArray("route_leg");
								if (null != routeArray
										&& routeArray.length() > 0) {
									for (int i = 0; i < routeArray.length(); i++) {
										JSONObject jsoRoute = routeArray.getJSONObject(i);
										if (null != jsoRoute) {
											if (!jsoRoute.isNull("lat") && !jsoRoute.isNull("lon")) {
												Point point = new Point(jsoRoute.getDouble("lat"), jsoRoute.getDouble("lon"));
												route.add(point);
											}
										}
									}
									if (route.size() > 0) {
										route.add(0, directions.getOrigin());
										route.add(directions.getDestination());
										directions.setRoute(route);
										return directions;
									} else
										return null;
								} else
									return null;
							} else
								return null;
						} else
							return null;
					} else
						return null;
				} else
					return null;
			}
			wr.close();
			rd.close();
			if (response != "")
				return parseJSONRouteSearchResults(response);
		} catch (Exception e) {
			LOG.error("Error while Fetching directions from Yahoo ",e);
		}
		return null;
	}

	/**
	 * Sort the geoStrings according to the algorithm of Yahoo to display the
	 * route consecutively.
	 * 
	 * @param geoString
	 * @return
	 */
	private static String sortPoints(String geoString) {
		StringBuffer geoData = new StringBuffer();
		String[] arr = StringUtils.splitString(geoString, "||");
		int[] indexValues = new int[arr.length - 2];
		int[] indices = new int[arr.length - 2];
		int defaulters = 0;
		int[] length = new int[arr.length - 2];
		int index = 0;
		for (int i = 1; i < (arr.length - 1); i++) {
			arr[i] = arr[i].substring(1);
			String arr1[] = StringUtils.splitString(arr[i], "|");
			for (int k = 1; k < arr1.length; k += 2) {
				String[] arr2 = StringUtils.splitString(arr1[k], ",");
				int val = Integer.parseInt(arr2[0]);
				int l = 0;
				for (l = 0; l < index; l++) {
					if (val == indexValues[l]) {
						defaulters = defaulters + 1;
						if (arr2.length > length[l]) {
							indexValues[l] = -1;
							indices[l] = -1;
							length[l] = -1;
							indices[index] = i;
							indexValues[index] = val;
							length[index] = arr2.length;
						} else {
							indexValues[index] = -1;
							indices[index] = -1;
							length[index] = -1;
						}
						break;
					}
				}
				if (l == index) {
					indices[index] = i;
					indexValues[index] = val;
					length[index] = arr2.length;
				}
				index = index + 1;
			}
		}
		for (int i = 0; i < indices.length; i++) {
			for (int j = 0; j < indices.length; j++) {
				if (indexValues[i] < indexValues[j]) {
					int temp = indexValues[i];
					indexValues[i] = indexValues[j];
					indexValues[j] = temp;
					temp = indices[i];
					indices[i] = indices[j];
					indices[j] = temp;
				}
			}
		}
		geoData.append(MathUtils.getInteger(arr[0]) - defaulters).append("||");
		for (int i = 1 + defaulters; i <= indices.length; i++) {
			geoData.append(arr[indices[i - 1]]).append("||");
		}
		return geoData.toString();
	}

	/**
	 * Decodes the Geom values
	 * 
	 * Typical GeoString :
	 * 2||46899,2375|2,322,-83,170,78,141,107||46900,2374|0,134
	 * ,106,100,138,66,173,-86,334||
	 * 
	 * @param geoString
	 * @return list of decoded lat-lon points
	 */
	public static List<Point> decodeGeo(String geoString, int zoomLevel) {
		List<Point> pointsDestination = new ArrayList<Point>();
		try {
			geoString = sortPoints(geoString);
			String[] arr = StringUtils.splitString(geoString, "||");
			for (int i = 1; i < arr.length; i++) {
				arr[i] = arr[i].substring(1);
			}

			for (int i = 0; i < MathUtils.getInteger(arr[0]); i++) {
				String[] arr1 = StringUtils.splitString(arr[i + 1], "|");
				String[] colrow = StringUtils.splitString(arr1[0], ",");
				int col = MathUtils.getInteger(colrow[0]);
				int row = MathUtils.getInteger(colrow[1]);

				for (int k = 1; k < arr1.length; k++) {
					String[] xyvals = StringUtils.splitString(arr1[k], ",");

					for (int j = 1; j < xyvals.length; j += 2) {
						int x = MathUtils.getInteger(xyvals[j]);
						int y = MathUtils.getInteger(xyvals[j + 1]);

						int globx = col * 256 + x * 1;
						int globy = row * 256 + y * 1;

						Point pd = pxy_to_ll(globx, globy, zoomLevel);

						pointsDestination.add(pd);
					}
				}
			}
		} catch (Exception e) {
			LOG.fatal("Exception: DecodeGeo(): ",e);
		}
		return pointsDestination;
	}

	/**
	 * Resolves xpix, ypix of geom to lat lon
	 * 
	 * @param x_pixel
	 * @param y_pixel
	 * @return
	 */
	public static Point pxy_to_ll(int x_pixel, int y_pixel, int zoomLevel) {

		if (zoomLevel != currentZoomLevel) {
			initialize(zoomLevel);
		}

		Point v_ll = new Point();

		double alon = x_pixel / xPerLon;
		double alat = (y_pixel / (pixelHeight / 2.0)) * PI;

		alat = Math.atan(Math.sinh(alat)) / RAD_PER_DEG;

		if (alon < 0 || alon > 360.0){
			return v_ll;
		}
		v_ll.y = alon - 180.0;
		if (alat <= -90.0 || alat >= 90.0){
			return v_ll;
		}
		v_ll.x = alat;

		return v_ll;
	}

	/**
	 * This function parses the JSON Result sent by the Server for a route query
	 * sent from the application
	 * 
	 * @param data
	 * @return A Vector that contains vectors of PointDoubles is returned.
	 */
	public static Directions parseJSONRouteSearchResults(String data) {
		Directions directions = new Directions();
		List<Point> route = new ArrayList<Point>();
		try {
			JSONObject jso = new JSONObject(data);
			JSONObject resultSet = jso.getJSONObject("ResultSet");
			int dataZoomLevel = -1;
			if (!resultSet.isNull("Result")) {
				JSONObject resultObject = resultSet.getJSONObject("Result");
				if (!resultObject.isNull("yahoo_driving_directions")) {
					JSONObject yahooDDirectionObject = resultObject.getJSONObject("yahoo_driving_directions");
					if (!yahooDDirectionObject.isNull("address")) {
						JSONArray addressArray = yahooDDirectionObject.getJSONArray("address");
						JSONObject currResult = (JSONObject) addressArray.get(0);
						Point origin = new Point();
						origin.setX(MathUtils.getDouble((String) currResult.get("lat")));
						origin.setY(MathUtils.getDouble((String) currResult.get("lon")));
						directions.setOrigin(origin);

						/** Destination information **/
						currResult = (JSONObject) addressArray.get(1);
						Point destination = new Point();
						destination.setX(MathUtils.getDouble((String) currResult.get("lat")));
						destination.setY(MathUtils.getDouble((String) currResult.get("lon")));
						directions.setDestination(destination);
					}

					if (!yahooDDirectionObject.isNull("total_distance")) {
						String distance = yahooDDirectionObject.getString("total_distance");
						directions.setDistance(MathUtils.getInteger(distance));
					}

					if (!yahooDDirectionObject.isNull("zoom")) {
						String zoom = yahooDDirectionObject.getString("zoom");
						dataZoomLevel = MathUtils.getInteger(zoom);
					}

					if (!yahooDDirectionObject.isNull("directions")) {
						JSONObject direction = yahooDDirectionObject.getJSONObject("directions");

						if (!direction.isNull("route_leg")) {
							JSONArray resultsArray = direction.getJSONArray("route_leg");

							for (int i = 0; i < resultsArray.length(); i++) {
								JSONObject currResult = (JSONObject) resultsArray.get(i);

								Point pd = new Point();
								pd.x = MathUtils.getDouble(currResult.getString("lat"));
								pd.y = MathUtils.getDouble(currResult.getString("lon"));
								route.add(pd);

								/**
								 * Parsing and adding lat lon associated with
								 * geom
								 **/
								if (!currResult.isNull("geometry")) {
									String jsonGeom = currResult.getString("geometry");
									List<Point> innerPoints = decodeGeo(jsonGeom, dataZoomLevel);
									route.addAll(innerPoints);
								}
							}
						} else
							return null;
					} else
						return null;
				} else
					return null;
			} else
				return null;

		} catch (JSONException e) {
			return null;
		}
		if (route.size() > 0) {
			route.add(0, directions.getOrigin());
			route.add(directions.getDestination());
		} else
			return null;
		directions.setRoute(route);
		return directions;
	}

	/**
	 * For formating the latlng to expected format , in some cases it will </br>
	 * come like -86.4314.2 so need convert it as -86.4314
	 * 
	 * @param value
	 *            latitude or longitude
	 * @return converted format
	 */
	public static String formatLocationData(String value) {
		try {
			String regX = "^-?[0-9]+(\\.[0-9]+)?";
			Pattern p = null;
			p = Pattern.compile(regX);
			Matcher m = p.matcher(value);
			String extractedValue = null;
			if (m.find()) {
				extractedValue = m.group();
			}
			return extractedValue;
		} catch (Exception e) {
			LOG.error("Error while formatting location data ",e);
		}
		return value;
	}

	/**
	 * fix for handling the negative value of location data. In the firm ware
	 * when the value is negative its not properly handled in 2's complement,so
	 * it need to be done here . Eg: if value is -87.675 , then do the following
	 * 
	 * 1. Extract the exponent part (i.e) 6753 2. abs((675 * 10) - 65536) =
	 * 58786 3. now use the above result as exponent of -87 which is -87.58786
	 * 
	 * @param data
	 *            data to be fixed
	 * @return fixed value
	 */
	public static String fixForNegativeLocationData(String data) {
		try {
			double value = Double.parseDouble(data);
			int exponent = MathUtils.getDecimalPart(value);
			exponent = extractFourValues(String.valueOf(exponent));
			int adjustedValue = Math.abs((exponent * 10) - 65536);
			StringBuilder fixedValue = new StringBuilder();
			fixedValue.append(String.valueOf((int) value));
			fixedValue.append(".");
			fixedValue.append(String.valueOf(adjustedValue));
			return fixedValue.toString();
		} catch (Exception e) {
			LOG.error("Error while doing fix for negative location data ",e);
		}
		return data;
	}


	private static int extractFourValues(String number) {
		try {
			char num[] = number.toCharArray();
			StringBuilder extractedNum = new StringBuilder();
			for (int i = 0; i < num.length && i <= 3; i++) {
				extractedNum.append(num[i]);
			}
			return Integer.parseInt(extractedNum.toString());
		} catch (Exception e) {
			return Integer.parseInt(number);
		}
	}
} 