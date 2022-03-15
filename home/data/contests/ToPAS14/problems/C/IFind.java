/*
 * IFind.java created on 06/05/2014
 * Copyright 2008 Computer Laboratory Univ Cambridge (CL). All rights reserved.
 * 
 * CL grants you ("Licensee") a non-exclusive, royalty free, license
 * to use, modify and redistribute this software in source and binary
 * code form, provided that i) this copyright notice and license appear
 * on all copies of the software; and ii) Licensee does not utilize the
 * software in a manner which is disparaging to CL.
 * 
 * This software is provided "AS IS," without a warranty of any kind.
 * ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND
 * ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THE
 * SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL CL OR ITS LICENSORS
 * BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 * HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING
 * OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 */
//package pt.up.fc.dcc.topas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author pbrandao
 *
 */
public class IFind {

	
	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Set defaults
	    int curMinLat = 0;
	    int curMinLon = 0;
	    int minDist = -1;

		//Read
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    String line = null;
	    
	    try
		{
	    	//read 1st line with current location
	    	line = br.readLine();
	    	String[] initValues = line.split(" ");
	    	// no error checking on size
	        int curLat = Integer.parseInt(initValues[0]);
	        int curLon = Integer.parseInt(initValues[1]);
	        String typeWanted = initValues[2];
	        //read nr of shops
	        line = br.readLine();
	        int numOfShops = Integer.parseInt(line);
	        String[] newValues;
	        int lat,lon,dist;
	        int countL = 0;
			while(countL<numOfShops && (line=br.readLine())!=null){
				countL++;
				newValues = line.split(" ");
				if(newValues[2].equals(typeWanted)){ //check distance
					lat = Integer.parseInt(newValues[0]);
			        lon = Integer.parseInt(newValues[1]);
			        dist = new Float(Math.pow(lat-curLat,2) + Math.pow(lon-curLon,2)).intValue();
		            if(dist < minDist || minDist ==-1){ //too lazy to get just the first or find the max for int
		                minDist = dist;
		                curMinLat = lat;
		                curMinLon = lon;
		            }
				}
			}
			if(minDist<0)
		        System.out.println("Nenhum");
		    else
		    	System.out.println(curMinLat + " " + curMinLon +" " + typeWanted +" " +minDist);
		}
		catch (IOException e)
		{
			System.out.print("Error reading line" + e);
		}
	}

}
