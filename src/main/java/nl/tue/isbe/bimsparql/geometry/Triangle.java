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

public class Triangle implements Geometry{
	
	public Point p0;
	public Point p1;
	public Point p2; 
	final private Point[] vertices={p0,p1,p2};
	
	String id;
	
    
	public double area;
	
    public Triangle(){
    	super();
    }

	public Triangle(Point p0, Point p1, Point p2) {
		super();
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;

		vertices[0]=this.p0;
		vertices[1]=this.p1;
		vertices[2]=this.p2;
	}

  

    public String    geometryType()
    {
        return "Triangle" ;
    }


    ///
    ///
    ///
    public GeometryType   geometryTypeId()
    {
        return GeometryType.TYPE_TRIANGLE ;
    }


    ///
    ///
    ///
    int  coordinateDimension()
    {
        return p0.coordinateDimension() ;
    }


    ///
    ///
    ///
  public  boolean  isEmpty()
    {
        return p0.isEmpty();
    }


    ///
    ///
    ///
    public boolean  is3D()
    {
        return p0.is3D() ;
    }

    ///
    ///
    ///
    public boolean  isMeasured()
    {
        return p0.isMeasured() ;
    }

   public Polygon  toPolygon()
    {
        if ( isEmpty() ) {
            return new Polygon() ;
        }

        List< Point > points =new ArrayList<Point>();

        for ( int i = 0; i < 4; i++ ) {
            points.add( vertex( i ) );
        }

        return new Polygon( new LineString( points ) );
    }

    ///
    ///
    ///
    public void accept(GeometryVisitor visitor)
    {visitor.visit( this );
      
    }

    public Point vertex(int i ){
        switch (i){
        	case 0: return p0;
        	case 1: return p1;
        	case 2: return p2;
        	case 3: return p0;
        }
        return null;
    }
    
    public Point[] getVertices(){
    	return vertices;
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
	

public Segment[] getEdges() {
	Segment[] edges={new Segment(p0,p1),new Segment(p1,p2),new Segment(p2,p0)};
	return edges;
}



@Override
public String getId() {
	return id;
}

@Override
public void setId(String id) {
	this.id=id;
	
}
	

    
    
}
