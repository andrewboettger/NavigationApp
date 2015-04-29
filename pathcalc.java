package com.example.lab4_v3;

import java.util.ArrayList;
import java.util.List;

import mapper.InterceptPoint;
import mapper.LineSegment;
import mapper.MapView;
import mapper.NavigationalMap;
import android.graphics.PointF;

public class pathcalc {
	


	public static void path(){
		 	MapView mv = MainActivity.mv;
		 	NavigationalMap map = MainActivity.map;
		 	List<PointF> listOfPoints = new ArrayList<PointF>();
		 	listOfPoints.add(mv.getUserPoint());
		 	List<InterceptPoint> clearPath = clearPath = map.calculateIntersections(mv.getUserPoint(), mv.getDestinationPoint());
		
			if(clearPath.isEmpty()) {
				listOfPoints.add(mv.getDestinationPoint());
				mv.setUserPath(listOfPoints);
				return;	
			}
			else if(!clearPath.isEmpty()) {

				float initialdistance_2,xdistance_fin_2, ydistance_fin_2, finaldistance_2, xdistance_ini_2, ydistance_ini_2;
				PointF finalPoint_2, initialPoint_2, prevPoint,POI_2;
				prevPoint = mv.getUserPoint();
				List<InterceptPoint> wallIntercepts  = map.calculateIntersections(mv.getUserPoint(), mv.getDestinationPoint());
				POI_2 = wallIntercepts.get(0).getPoint();
			
				PointF [] points = new PointF[ wallIntercepts.size()];
				LineSegment [] lines = new LineSegment[wallIntercepts.size()];
				PointF [] startpoints = new PointF[wallIntercepts.size()]; 
				PointF [] endpoints = new PointF[wallIntercepts.size()]; 
				
				listOfPoints.add(POI_2);
				
				for (int j = 0 ; j < points.length; j++){
					points[j] = wallIntercepts.get(j).getPoint();
					lines[j] = wallIntercepts.get(j).getLine();	
					startpoints[j] = lines[j].getStart();
					endpoints[j] = lines[j].getEnd();	
				}
				
				for(int k = 0; k < wallIntercepts.size(); k++){
				
					if(lines[k].length() < 16){
						
						if (map.getGeometryAtPoint(startpoints[k]).size() ==2  && map.getGeometryAtPoint(endpoints[k]).size() ==2 && (k%4 == 3 || k%4 ==0))
						{
							
							if(lines[k].length() < 2){	
								//System.out.println("HIIII: " + "smallwall "+ lines[k].length());
								if(startpoints[k].y > endpoints[k].y){
									listOfPoints.add(endpoints[k]);						
								}
								else{
									listOfPoints.add(startpoints[k]);
								}
							}
							else if ( lines[k].length() >5 && lines[k].length() < 13){
								if(Math.abs(startpoints[k].y-mv.getDestinationPoint().y)>=Math.abs(endpoints[k].y- mv.getDestinationPoint().y)){
									listOfPoints.add(endpoints[k]);
								}	
								else{
									listOfPoints.add(startpoints[k]);
								}	
							}
							else if ( lines[k].length() >15){			
									listOfPoints.add(startpoints[k]);		
							}
							else{
								if(Math.abs(startpoints[k].x-mv.getDestinationPoint().x)>=Math.abs(endpoints[k].x- mv.getDestinationPoint().x)){
									listOfPoints.add(endpoints[k]);
								}
								else{
									listOfPoints.add(startpoints[k]);
								}
							}
						}
					else if(map.getGeometryAtPoint(startpoints[k]).size() ==2  && (k%4 == 3 || k%4 ==0)){
						
						if(lines[k].length() < 2){	
							
							if(startpoints[k].y > endpoints[k].y){
								listOfPoints.add(endpoints[k]);
							}
							else{
								listOfPoints.add(startpoints[k]);
							}
						}
						else{
						listOfPoints.add(startpoints[k]);
						}
					}
					
						else if(map.getGeometryAtPoint(endpoints[k]).size() ==2 &&(k%4 == 3 || k%4 ==0)){
							if(lines[k].length() < 5){	
								if(startpoints[k].y > endpoints[k].y){
									listOfPoints.add(endpoints[k]);
								}
								else{
									listOfPoints.add(startpoints[k]);
								}
							}
							else{
							listOfPoints.add(endpoints[k]);
							}
						}
					}
					
				}
				listOfPoints.add(mv.getDestinationPoint());
				mv.setUserPath(listOfPoints);				
	}

}		
	
	
	/*public String direction(PointF userPoint, List<PointF> listOfPoints){
		
		String text = "";
		float curr_x = userPoint.x;
		float curr_y = userPoint.y;
		
		if(curr_x < )
		
		
		
		return text;
	}*/
					
}


