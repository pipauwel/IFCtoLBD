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
package nl.tue.isbe.IFC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class IfcVersion {

    private String label;

    public IfcVersion(String label) {
        this.label = label;
    }

    public static final IfcVersion IFC2X3_TC1 = new IfcVersion("IFC2X3_TC1");

    public static final IfcVersion IFC2X3_FINAL = new IfcVersion("IFC2X3_Final");

    public static final IfcVersion IFC4 = new IfcVersion("IFC4");

    public static final IfcVersion IFC4X1_RC3 = new IfcVersion("IFC4x1_RC3");

    public static final IfcVersion IFC4_ADD1 = new IfcVersion("IFC4_ADD1");

    public static final IfcVersion IFC4_ADD2 = new IfcVersion("IFC4_ADD2");

    public static final IfcVersion IFC4_ADD2_TC1 = new IfcVersion("IFC4_ADD2_TC1");

    public static final IfcVersion IFC4x1 = new IfcVersion("IFC4x1");

    public static final IfcVersion IFC4x3_RC1 = new IfcVersion("IFC4x3_RC1");

    public static Map<IfcVersion, String> IfcNSMap = new HashMap<IfcVersion, String>();

    public static Map<String, IfcVersion> NSIfcMap = new HashMap<String, IfcVersion>();


    public String getLabel() {
        return label;
    }

    public static IfcVersion getIfcVersion(String versionName) throws IfcVersionException {
        IfcVersion[] versions = {IFC2X3_TC1, IFC2X3_FINAL, IFC4, IFC4X1_RC3, IFC4_ADD1, IFC4_ADD2, IFC4_ADD2_TC1, IFC4x1, IFC4x3_RC1};
        for (IfcVersion v : versions) {
            if (v.getLabel().equalsIgnoreCase(versionName)) {
                return v;
            }
        }
        throw new IfcVersionException("Cannot find required IFC version: " + versionName);
    }
}
