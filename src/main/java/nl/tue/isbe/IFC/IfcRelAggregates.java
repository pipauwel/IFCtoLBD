package nl.tue.isbe.IFC;

/*
 *
 * Copyright 2019 Pieter Pauwels, Eindhoven University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.buildingsmart.tech.ifcowl.vo.IFCVO;

import java.util.ArrayList;
import java.util.List;

public class IfcRelAggregates {

    private IFCVO lineEntry;
    public static List<IfcRelAggregates> RelAggregatesList  = new ArrayList<IfcRelAggregates>();

    private IfcObjectDefinition relatingObject;
    private List<IfcObjectDefinition> relatedObjects = new ArrayList<IfcObjectDefinition>();

    public IfcRelAggregates(IFCVO lineEntry){
        this.lineEntry = lineEntry;
        RelAggregatesList.add(this);
        this.parse();
    }

    private void parse(){
        //relatingObject
        relatingObject = new IfcObjectDefinition((IFCVO)lineEntry.getObjectList().get(8));
        //relatedObjects
        List<Object> lvo = (List<Object>)lineEntry.getObjectList().get(10);
        for(IFCVO j : removeClutterFromList(lvo)) {
            relatedObjects.add(new IfcObjectDefinition(j));
        }
    }

    public static List<IfcObjectDefinition> getRelatedObjectsForRelatingObject(long lineNum){
        for(IfcRelAggregates ira : RelAggregatesList){
            if(ira.getRelatingObject().getLineNum() == lineNum)
                return ira.getRelatedObjects();
        }
        return null;
    }

    private List<IFCVO> removeClutterFromList(List<Object> lvo){
        List<IFCVO> theRealList = new ArrayList<IFCVO>();
        for(Object o : lvo) {
            if(o.getClass().equals(IFCVO.class))
                theRealList.add((IFCVO)o);
        }
        return theRealList;
    }

    //------------
    // ACCESSORS
    //------------

    public IFCVO getLineEntry() {
        return lineEntry;
    }

    public IfcObjectDefinition getRelatingObject() {
        return relatingObject;
    }

    public List<IfcObjectDefinition> getRelatedObjects() {
        return relatedObjects;
    }
}
