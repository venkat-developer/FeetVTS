/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.i10n.mina.codec;

import com.i10n.mina.codec.decoder.BytesPosition;

/**
 * Provides SumUp protocol constants.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class Constants {
	
	public static final String CONFIG_FILE_LOCATION = "/producer.config";

	public static final String DATA_FILE_PREFIX = "/moduledata";

	public static final String DATA_FILE_SUFFIX = ".dat";

    public static final int DATA_OBJECTS_PER_FILE = 1000;

    public static final int TYPE_LEN = 2;

    public static final int SEQUENCE_LEN = 4;

    public static final int HEADER_LEN = TYPE_LEN + SEQUENCE_LEN;

    public static final int BODY_LEN = 12;

    public static final int RESULT = 0;

    public static final int ADD = 1;

    public static final int RESULT_CODE_LEN = 2;

    public static final int RESULT_VALUE_LEN = 4;

    public static final int ADD_BODY_LEN = 4;

    public static final int RESULT_OK = 0;

    public static final int RESULT_ERROR = 1;
    
	/*
	 * Static fields reflecting the current # of bytes and position of each data
	 */
	public static final BytesPosition DP_YEAR = new BytesPosition(37, 2);
	public static final BytesPosition DP_MONTH = new BytesPosition(39, 1);
	public static final BytesPosition DP_DAY = new BytesPosition(40, 1);
	public static final BytesPosition DP_IMEI = new BytesPosition(41, 15);

	/* Old Version Modules */
	public static final BytesPosition POS_COUNT = new BytesPosition(0, 2);
	public static final BytesPosition POS_GPS_SIGNAL = new BytesPosition(2, 1);
	public static final BytesPosition POS_DIRECTION = new BytesPosition(3, 2);
	public static final BytesPosition POS_GSM_SIGNAL = new BytesPosition(5, 1);
	public static final BytesPosition POS_TOTAL_DATA_PACKETS_SENT = new BytesPosition(6, 4);
	public static final BytesPosition POS_SUCCESSFUL_DATA_PACKETS_SENT = new BytesPosition(10, 4);
	public static final BytesPosition POS_BATTERY_VOLTAGE = new BytesPosition(14, 2);
	public static final BytesPosition POS_CUMULATIVE_DISTANCE = new BytesPosition(16, 4);
	public static final BytesPosition POS_SPEED = new BytesPosition(20, 2);
	public static final BytesPosition POS_ANALOGUE_2 = new BytesPosition(22, 2);
	public static final BytesPosition POS_ANALOGUE_3 = new BytesPosition(24, 2);
	public static final BytesPosition POS_ERROR = new BytesPosition(26, 1);
	public static final BytesPosition POS_LAC = new BytesPosition(27, 4);
	public static final BytesPosition POS_CID = new BytesPosition(31, 4);
	public static final BytesPosition POS_HEALTH_DATA = new BytesPosition(35, 1);
	public static final BytesPosition POS_VERSION = new BytesPosition(36, 1);
	public static final BytesPosition POS_IMEI = new BytesPosition(37, 15);
	public static final BytesPosition POS_LATITUDE = new BytesPosition(52, 4);
	public static final BytesPosition POS_LONGITUDE = new BytesPosition(56, 4);
	public static final BytesPosition POS_DELTA_DISTANCE = new BytesPosition(60, 2);
	public static final BytesPosition POS_TOW = new BytesPosition(62, 4);
	public static final BytesPosition POS_ANALOGUE_1_FUEL_DATA = new BytesPosition(66, 2);
	public static final BytesPosition POS_CRC = new BytesPosition(68, 1);
	/* Old Version Modules */

	public static final BytesPosition POS_WEEK_NUMBER = new BytesPosition(70, 1);
	public static final BytesPosition POS_SEQUENCE_NUMBER = new BytesPosition(71, 1);


	/* for DP version */
	public static final BytesPosition POS_YEAR = new BytesPosition(37, 2);
	public static final BytesPosition POS_MONTH = new BytesPosition(39, 1);
	public static final BytesPosition POS_DAY = new BytesPosition(40, 1);


	public static final int SECONDS_IN_WEEK = 7*24*60*60;

	/**
	 * Compute Two powers. These variables are used for extracting individual bits from a byte
	 */
	public static final double TWO_POW_ZERO = Math.pow(2,0);
	public static final double TWO_POW_ONE = Math.pow(2,1);
	public static final double TWO_POW_TWO = Math.pow(2,2);
	public static final double TWO_POW_THREE = Math.pow(2,3);
	public static final double TWO_POW_FOUR = Math.pow(2,4);
	public static final double TWO_POW_FIVE = Math.pow(2,5);
	public static final double TWO_POW_SIX = Math.pow(2,6);
	public static final double TWO_POW_SEVEN = Math.pow(2,7);




	public static final BytesPosition DP_LATITUDE = new BytesPosition(56, 4);
	public static final BytesPosition DP_LONGITUDE = new BytesPosition(60, 4);
	public static final BytesPosition DP_DELTA_DISTANCE = new BytesPosition(64, 2);
	public static final BytesPosition DP_TOW = new BytesPosition(66, 4);
	public static final BytesPosition DP_WEEK_NO = new BytesPosition(70, 1);
	public static final BytesPosition DP_SEQ_NO = new BytesPosition(71, 1);

	public static final BytesPosition DP_CRC = new BytesPosition(72, 1);
	
    private Constants() {
    }
}
