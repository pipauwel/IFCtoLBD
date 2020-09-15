/*******************************************************************************
 *                                                                              *
 * This file is part of IfcOpenShell.                                           *
 *                                                                              *
 * IfcOpenShell is free software: you can redistribute it and/or modify         *
 * it under the terms of the Lesser GNU General Public License as published by  *
 * the Free Software Foundation, either version 3.0 of the License, or          *
 * (at your option) any later version.                                          *
 *                                                                              *
 * IfcOpenShell is distributed in the hope that it will be useful,              *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of               *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the                 *
 * Lesser GNU General Public License for more details.                          *
 *                                                                              *
 * You should have received a copy of the Lesser GNU General Public License     *
 * along with this program. If not, see <http://www.gnu.org/licenses/>.         *
 *                                                                              *
 ********************************************************************************/

/*******************************************************************************
 *                                                                              *
 * This class communicates with the JNI wrapper of IfcOpenShell. Note that,     *
 * contrary to the Bonsma IFC engine, if the wrapper crashes it will take the   *
 * BIMserver down with her. Since loading the wrapper involves loading a        *
 * considerable binary into memory, it would have been better to make the       *
 * System.load() call somewhere in IfcOpenShellEngine.java.                     *
 *                                                                              *
 ********************************************************************************/

package org.ifcopenshell;

/******************************************************************************
 * Copyright (C) 2009-2019  BIMserver.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see {@literal<http://www.gnu.org/licenses/>}.
 *****************************************************************************/

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bimserver.plugins.renderengine.EntityNotFoundException;
import org.bimserver.plugins.renderengine.RenderEngineException;
import org.bimserver.plugins.renderengine.RenderEngineFilter;
import org.bimserver.plugins.renderengine.RenderEngineInstance;
import org.bimserver.plugins.renderengine.RenderEngineModel;
import org.bimserver.plugins.renderengine.RenderEngineSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IfcOpenShellModel implements RenderEngineModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(IfcOpenShellModel.class);

    private InputStream ifcInputStream;

    private Map<Long, IfcOpenShellEntityInstance> instancesById;

    private IfcGeomServerClient client;

    public IfcOpenShellModel(IfcGeomServerClient client, InputStream ifcInputStream) throws RenderEngineException, IOException {
        this.client = client;
        this.ifcInputStream = ifcInputStream;

        client.loadModel(ifcInputStream);
    }

    public IfcOpenShellModel(IfcGeomServerClient client, InputStream ifcInputStream, long length) throws RenderEngineException, IOException {
        this.client = client;
        this.ifcInputStream = ifcInputStream;

        client.loadModel(ifcInputStream, length);
    }

    @Override
    public void close() throws RenderEngineException {
        if (instancesById != null) {
            instancesById.clear();
        }
        try {
            ifcInputStream.close();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void generateGeneralGeometry() throws RenderEngineException {
        // We keep track of instances ourselves
        instancesById = new HashMap<Long, IfcOpenShellEntityInstance>();

        final double t0 = (double) System.nanoTime();

        while (client.hasNext()) {
            IfcGeomServerClientEntity next = client.getNext();
            // Store the instance in our dictionary
            IfcOpenShellEntityInstance instance = new IfcOpenShellEntityInstance(next);
            instancesById.put(next.getId(), instance);
        }

        final double t1 = (double) System.nanoTime();

        LOGGER.debug(String.format("Took %.2f seconds to obtain representations for %d entities", (t1-t0) / 1.E9, instancesById.size()));
    }

    public Map<Long, IfcOpenShellEntityInstance> getInstancesById() {
        if (instancesById!=null)
            return instancesById;
        else
            return null;
    }

    @Override
    public RenderEngineInstance getInstanceFromExpressId(long expressId) throws RenderEngineException {
        if (instancesById.containsKey(expressId)) {
            return instancesById.get(expressId);
        } else {
            // Probably something went wrong with the processing of this element in
            // the IfcOpenShell binary, as it has not been included in the enumerated
            // set of elements with geometry.
            throw new EntityNotFoundException("Entity " + expressId + " not found in model");
        }
    }

    @Override
    public void setFormat(int format, int mask) throws RenderEngineException {
    }

    @Override
    public void setSettings(RenderEngineSettings settings) throws RenderEngineException {
    }

    @Override
    public void setFilter(RenderEngineFilter renderEngineFilter) {
    }

    @Override
    public Collection<RenderEngineInstance> listInstances() throws RenderEngineException {
        return null;
    }
}