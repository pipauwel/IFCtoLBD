package nl.tue.isbe.BOT;

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

public class Property {

    private long lineNum;

    private Element relatedElement;
    private String propertyType;
    private String value;
    private String propertyName;
    private String propertyNameNoSpace;

    private IFCVO lineEntry;
    public static List<Property> propertyList = new ArrayList<Property>();

    public Property(IFCVO lineEntry){
        this.lineEntry = lineEntry;
        lineNum = lineEntry.getLineNum();
        propertyList.add(this);
        this.parse();
    }

    private void parse(){
        propertyName = ((String) lineEntry.getObjectList().get(0)).substring(1);
        propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
        propertyName = propertyName.replaceAll("[-+.^():/,]","");
        propertyNameNoSpace = propertyName.replaceAll("\\s+","");
        propertyType = (String) lineEntry.getObjectList().get(4);
        List<Object> prop = (List<Object>)lineEntry.getObjectList().get(5);
        value = prop.get(0).toString();
    }

    //------------
    // ACCESSORS
    //------------

    public long getLineNum() {
        return lineNum;
    }

    public IFCVO getLineEntry(IFCVO lineEntry) {
        return lineEntry;
    }

    public String getValue() {
        return value;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyNameNoSpace() {
        return propertyNameNoSpace;
    }

    public boolean isEmpty(){
        if(this.getValue().startsWith("\'") && this.getValue().length()==1) {
            return true;
        }
        else if(!this.getValue().startsWith("\'") && this.getValue().length()==0) {
            return true;
        }
        else
            return false;
    }
}
