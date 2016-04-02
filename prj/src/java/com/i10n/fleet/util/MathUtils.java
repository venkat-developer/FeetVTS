package com.i10n.fleet.util;
 
 
import java.util.StringTokenizer;
 
 
 
public class MathUtils {
 
   // private static Logger LOG = Logger.getLogger(MailUtils.class);
 
    /**
     * 
     * Gets integer value of the string. It is also safe and will not throw any
     * exceptions
     * 
     * @param value
     * @return
     */
    public static int getInteger(String value) {
        int returnValue = 0;
        if(value != null && !value.equals("")) {
            try {
                value = value.trim();
                if(value.endsWith(","))
                    value=value.substring(0, value.length()-1);
                returnValue = Integer.parseInt(value);
            }catch(NumberFormatException e) {
            //    LOG.fatal("Exception: getInteger(): " + e);
            }
        }
        return returnValue;
    }
    
    
    /**
     * 
     * Gets integer value of the string. It is also safe and will not throw any
     * exceptions
     * 
     * @param value
     * @return
     */
    public static int getHexToInteger(String value) {
        int returnValue = 0;
        if(value != null && !value.equals("")) {
            try {
                value = value.trim();
                if(value.endsWith(","))
                    value=value.substring(0, value.length()-1);
                returnValue = Integer.parseInt(value, 16);
            }catch(NumberFormatException e) {
                ///LOG.fatal("Exception: getHexToInteger(): " + e);
            }
        }
        return returnValue;
    }
 
 
    /**
     * 
     * Gets long value of the string. It is also safe and will not throw any
     * exceptions
     * 
     * @param value
     * @return
     */
    public static long getLong(String value) {
        long returnValue = 0;
        if(value != null && !value.equals("")) {
            try {
                value = value.trim();
                if(value.endsWith(","))
                    value=value.substring(0, value.length()-1);
                returnValue = Long.parseLong(value);
            }catch(NumberFormatException e) {
                //LOG.fatal("Exception: getLong(): " + e);
            }
        }
        return returnValue;
    }
 
    public static double getDouble(String data) {
        double val = 0;
        if(null != data && !"".equals(data)) {
            try {
                data =data.trim();
                if(data.endsWith(","))
                    data=data.substring(0, data.length()-1);
                val = Double.parseDouble(data);
            }catch(Exception e) {
                //LOG.fatal("Exception: getDouble(): " + e);
            }
        }
        return val;
    }
 
    public static float getFloat(String data) {
        float val = 0;
        if(null != data && !"".equals(data)) {
            try {
                data =data.trim();
                if(data.endsWith(","))
                    data=data.substring(0, data.length()-1);
                val = Float.parseFloat(data);
            }catch(Exception e) {
            //    LOG.fatal("Exception: getFloat(): " + e);
            }
        }
        return val;
    }
    
    public static int getVariance(int data[]){
        int mean = getMean(data);
        int temp[] = new int[data.length];
        for(int i=0;i<data.length;i++){
            temp[i] = (mean - data[i]) * (mean - data[i]);
        }       
        return (int)Math.round((double)getMean(temp));
    }
    
    public static int getStandardDeviation(int data[]){
        return (int)Math.round(Math.sqrt(getVariance(data)));
    }
    
    public static int getMean(int data[]){
        try{
            int sum = 0;
            for(int i=0;i<data.length;i++){
                sum += data[i];
            }
             
            return (int)Math.round((double)sum/data.length);
        }
        catch(Exception e){
            //LOG.fatal("Exception: getMean(): " , e);
            return 0;
        }
    }
    
    /**
     * to extract the exponent part of the double value
     * @param value value of which exponent part has to be extracted
     * @return exponent part
     */
    public static int getDecimalPart(double value){
        StringTokenizer tokens = new StringTokenizer(String.valueOf(value),".");
        if(tokens.hasMoreTokens() && tokens.countTokens() > 1){
            tokens.nextToken();
            return Integer.parseInt(tokens.nextToken());
        }
        return -1;
    }
} 