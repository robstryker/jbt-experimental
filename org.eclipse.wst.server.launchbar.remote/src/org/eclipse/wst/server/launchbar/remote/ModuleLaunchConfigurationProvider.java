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
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.launchbar.core.ILaunchConfigurationProvider;
import org.eclipse.launchbar.core.ILaunchDescriptor;
import org.eclipse.wst.server.launchbar.remote.descriptors.MyObjectDescriptor;

public class ModuleLaunchConfigurationProvider implements ILaunchConfigurationProvider {

	public ModuleLaunchConfigurationProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ILaunchConfiguration createLaunchConfiguration(ILaunchManager launchManager, ILaunchDescriptor descriptor)
			throws CoreException {
		
		// Ideally, I would like to at least SEE the target, here...
		// So I can check internal details about its type and respond with
		// differently-configured launch configs, or different specific
		// launch configs that are already created...
		
		// But I have no ability to do that here. 
		
		ILaunchConfigurationType type = getLaunchConfigurationType();
		String descriptorName = descriptor.getName();
		String validName = getValidLaunchConfigurationName(descriptorName);
		ILaunchConfigurationWorkingCopy wc = type.newInstance(null, validName);
		
		
		if( descriptor instanceof MyObjectDescriptor ) {
			wc.setAttribute(ServerLaunchBarDelegate.ATTR_LAUNCH_TYPE, "ExampleProperty");
			return wc;
		}
		return wc.doSave();
	}

	
	@Override
	public Object launchConfigurationAdded(ILaunchConfiguration configuration) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean launchConfigurationRemoved(ILaunchConfiguration configuration) throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ILaunchConfigurationType getLaunchConfigurationType() throws CoreException {
		return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType("org.eclipse.wst.server.launchbar.remote.serverAdapterLaunch");
	}

	
	
	protected static final char[] INVALID_CHARS = new char[] {'/','\\', ':', '*', '?', '"', '<', '>', '|', '\0', '@', '&'};
	protected String getValidLaunchConfigurationName(String s) {
		if (s == null || s.length() == 0)
			return "1";
		int size = INVALID_CHARS.length;
		for (int i = 0; i < size; i++) {
			s = s.replace(INVALID_CHARS[i], '_');
		}
		return s;
	}
}
