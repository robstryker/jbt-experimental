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
import org.eclipse.wst.server.launchbar.remote.descriptors.ModuleArtifactDetailsLaunchDescriptor;
import org.eclipse.wst.server.launchbar.remote.descriptors.ModuleArtifactLaunchDescriptor;
import org.eclipse.wst.server.launchbar.remote.descriptors.ModuleLaunchDescriptor;

public class ModuleLaunchConfigurationProvider implements ILaunchConfigurationProvider {

	public ModuleLaunchConfigurationProvider() {
		// TODO Auto-generated constructor stub
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
	
	
	@Override
	public ILaunchConfiguration createLaunchConfiguration(ILaunchManager launchManager, ILaunchDescriptor descriptor)
			throws CoreException {
		ILaunchConfigurationType type = getLaunchConfigurationType();
		String descriptorName = descriptor.getName();
		String validName = getValidLaunchConfigurationName(descriptorName);
		ILaunchConfigurationWorkingCopy wc = type.newInstance(null, validName);
		
		
		if( descriptor instanceof ModuleLaunchDescriptor ) {
			
			// I would prefer to somehow pass an actual Object here, but cannot persist it in launch config
			// So instead i must workaround by passing the project, and later trying to get 
			// module from project. Unfortunately, its not 1:1 mapping, so I may lose my module selection
			wc.setAttribute(ServerLaunchBarDelegate.ATTR_LAUNCH_TYPE, ServerLaunchBarDelegate.ATTR_LAUNCH_TYPE_MODULE);
			wc.setAttribute(ServerLaunchBarDelegate.ATTR_PROJECT, ((ModuleLaunchDescriptor)descriptor).getModule().getProject().getName());
			return wc;
		}
		
		if( descriptor instanceof ModuleArtifactDetailsLaunchDescriptor ) {
			wc.setAttribute(ServerLaunchBarDelegate.ATTR_LAUNCH_TYPE, ServerLaunchBarDelegate.ATTR_LAUNCH_TYPE_ARTIFACT);
			wc.setAttribute(ServerLaunchBarDelegate.ATTR_ARTIFACT_STRING, ((ModuleArtifactDetailsLaunchDescriptor)descriptor).getArtifactWrapper().getArtifactString());
			wc.setAttribute(ServerLaunchBarDelegate.ATTR_ARTIFACT_CLASS, ((ModuleArtifactDetailsLaunchDescriptor)descriptor).getArtifactWrapper().getArtifactClass());
		}

		if( descriptor instanceof ModuleArtifactLaunchDescriptor ) {
			wc.setAttribute(ServerLaunchBarDelegate.ATTR_LAUNCH_TYPE, ServerLaunchBarDelegate.ATTR_LAUNCH_TYPE_ARTIFACT);
			wc.setAttribute(ServerLaunchBarDelegate.ATTR_ARTIFACT_STRING, ((ModuleArtifactLaunchDescriptor)descriptor).getArtifactWrapper().getArtifactString());
			wc.setAttribute(ServerLaunchBarDelegate.ATTR_ARTIFACT_CLASS, ((ModuleArtifactLaunchDescriptor)descriptor).getArtifactWrapper().getArtifactClass());
		}
		
		// These launches are private and should not appear in the UI
		wc.setAttribute(ILaunchManager.ATTR_PRIVATE, true);
		
		return wc.doSave();
	}

}
