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
package org.eclipse.wst.server.launchbar.remote.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.actions.RunOnServerActionDelegate;

/**
 * A subclass of wst.servertools' RunOnServerActionDelegate
 * which is instantiated with a given server, rather than going
 * through the workflow to choose one. 
 */
public class DeclaredServerRunOnServerActionDelegate extends RunOnServerActionDelegate {
	private IServer s2;
	public DeclaredServerRunOnServerActionDelegate(IServer s) {
		this.s2 = s;
	}
	public IServer getServer(IModule module, IModuleArtifact moduleArtifact, IProgressMonitor monitor) throws CoreException {
		return s2;
	}
}