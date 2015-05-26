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
package org.eclipse.wst.server.launchbar;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.launchbar.core.ILaunchDescriptor;
import org.eclipse.launchbar.core.ILaunchDescriptorType;
import org.eclipse.wst.server.launchbar.descriptors.ModuleArtifactDetailsLaunchDescriptor;
import org.eclipse.wst.server.launchbar.descriptors.ModuleArtifactLaunchDescriptor;
import org.eclipse.wst.server.launchbar.descriptors.ModuleLaunchDescriptor;
import org.eclipse.wst.server.launchbar.objects.ModuleArtifactDetailsWrapper;
import org.eclipse.wst.server.launchbar.objects.ModuleArtifactWrapper;
import org.eclipse.wst.server.launchbar.objects.ModuleWrapper;

public class ModuleDescriptorType implements ILaunchDescriptorType {

	public ModuleDescriptorType() {
	}
	
	@Override
	public boolean ownsLaunchObject(Object launchObject) throws CoreException {
		if( launchObject instanceof ModuleWrapper)
			return true;
		if( launchObject instanceof ModuleArtifactDetailsWrapper) 
			return true;
		if( launchObject instanceof ModuleArtifactWrapper) 
			return true;
		return false;
	}

	@Override
	public ILaunchDescriptor getDescriptor(Object launchObject) throws CoreException {
		if( launchObject instanceof ModuleWrapper ) 
			return new ModuleLaunchDescriptor((ModuleWrapper)launchObject, this);
		if( launchObject instanceof ModuleArtifactDetailsWrapper ) 
			return new ModuleArtifactDetailsLaunchDescriptor((ModuleArtifactDetailsWrapper)launchObject, this);
		if( launchObject instanceof ModuleArtifactWrapper ) 
			return new ModuleArtifactLaunchDescriptor((ModuleArtifactWrapper)launchObject, this);
		return null;
	}

}
