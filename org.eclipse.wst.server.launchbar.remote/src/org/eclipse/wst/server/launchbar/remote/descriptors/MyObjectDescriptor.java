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
package org.eclipse.wst.server.launchbar.remote.descriptors;

import org.eclipse.launchbar.core.ILaunchDescriptor;
import org.eclipse.launchbar.core.ILaunchDescriptorType;

/**
 * A launch descriptor representing a module artifact
 */
public class MyObjectDescriptor implements ILaunchDescriptor {
	private String name;
	private ILaunchDescriptorType type;
	public MyObjectDescriptor(String name, ILaunchDescriptorType type) {
		this.name = name;
		this.type = type;
	}
	public String getName() {
		return name;
	}
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ILaunchDescriptorType getType() {
		return type;
	}
}