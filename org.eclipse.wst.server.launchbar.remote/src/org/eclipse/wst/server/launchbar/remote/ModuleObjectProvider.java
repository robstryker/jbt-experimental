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
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.launchbar.core.ILaunchBarManager;
import org.eclipse.launchbar.core.ILaunchObjectProvider;

/**
 * This class provides launchable objects to the framework. 
 * It currently returns three types of objects:
 *    1) Existing modules
 *    2) Module artifact details as pulled from existing launch configs
 *    3) Module artifacts based on the current workspace selection
 */
public class ModuleObjectProvider implements ILaunchObjectProvider  {
	
	private ILaunchBarManager manager;

	
	@Override
	public void init(ILaunchBarManager manager) throws CoreException {
		this.manager = manager;	
		manager.launchObjectAdded(new MyObject("Object1"));
		manager.launchObjectAdded(new MyObject("Object2"));
		manager.launchObjectAdded(new MyObject("Object3"));
	}
	
	public static class MyObject {
		public String name;
		public MyObject(String name) {
			this.name = name;
		}
		public boolean equals(Object other) {
			return other instanceof MyObject ? ((MyObject)other).name.equals(name) : false;
		}
		public int hashCode() {
			return name.hashCode();
		}
	}
	
	// To allow override by tests
	ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	@Override
	public void dispose() {
	}
}
