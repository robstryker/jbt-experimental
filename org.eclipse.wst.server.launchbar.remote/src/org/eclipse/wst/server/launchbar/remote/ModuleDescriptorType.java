/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.eclipse.wst.server.launchbar.remote;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.launchbar.core.ILaunchDescriptor;
import org.eclipse.launchbar.core.ILaunchDescriptorType;
import org.eclipse.wst.server.launchbar.remote.ModuleObjectProvider.MyObject;
import org.eclipse.wst.server.launchbar.remote.descriptors.MyObjectDescriptor;

public class ModuleDescriptorType implements ILaunchDescriptorType {

	public ModuleDescriptorType() {
	}
	
	@Override
	public boolean ownsLaunchObject(Object launchObject) throws CoreException {
		if( launchObject instanceof MyObject)
			return true;
		return false;
	}

	@Override
	public ILaunchDescriptor getDescriptor(Object launchObject) throws CoreException {
		if( launchObject instanceof MyObject ) 
			return new MyObjectDescriptor(((MyObject)launchObject).name, this);
		return null;
	}

}
