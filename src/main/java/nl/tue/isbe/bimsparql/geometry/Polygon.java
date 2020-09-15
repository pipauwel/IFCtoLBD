/*******************************************************************************
 * Copyright (C) 2017 Chi Zhang
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package nl.tue.isbe.bimsparql.geometry;

import java.util.ArrayList;
import java.util.List;

import nl.tue.isbe.bimsparql.geometry.visitor.GeometryVisitor;

public class Polygon implements Geometry{
	
	List<LineString> rings=new ArrayList<LineString>();

    String id;

	public Polygon () {
		super();
		rings.add(new LineString());
	}

	public LineString exteriorRing() {
		// TODO Auto-generated method stub
		return rings.get(0);
	}

	public void addRing(LineString interiorRing) {
		rings.add(interiorRing);
		
	}
	

    


	public Polygon (LineString exteriorRing ){
		super();
	    rings.add( exteriorRing );
	}


     public Polygon(Triangle triangle ){
	    
	super();
	    rings.add( new LineString() );

	    if ( ! triangle.isEmpty() ) {
	        for ( int i = 0; i < 4; i++ ) {
	            exteriorRing().addPoint( triangle.vertex( i ) );
	        }
	    }
	}






	public int coordinateDimension(){
	    return rings.get(0).coordinateDimension() ;
	}



	public String geometryType(){
	    return "Polygon" ;
	}


	public GeometryType geometryTypeId(){
	    return GeometryType.TYPE_POLYGON ;
	}




	public boolean isEmpty()
	{
	    return exteriorRing().isEmpty() ;
	}


	public boolean  is3D()
	{
	    return exteriorRing().is3D() ;
	}

	///
	///
	///
	public boolean  isMeasured()
	{
	    return exteriorRing().isMeasured() ;
	}


     public void reverse(){
	    for ( int i = 0; i < numRings(); i++ ) {
        rings.get(i).reverse();
       }
    }

     public int numRings(){
	    return rings.size();
     }



	public void accept( GeometryVisitor visitor )
	{
	    visitor.visit( this );
	}



	public LineString ringN(int i) {
		// TODO Auto-generated method stub
		return rings.get(i);
	}

	public int numInteriorRings() {
		// TODO Auto-generated method stub
		return rings.size()>0 ? rings.size()-1 : 0;
	}
	
	public LineString interiorRingN(int i){
		return rings.get(i+1);
	}



	@Override
	public Geometry boundary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int numGeometries() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Geometry geometryN(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public void setId(String id) {
		// TODO Auto-generated method stub
		this.id=id;
	}





}
