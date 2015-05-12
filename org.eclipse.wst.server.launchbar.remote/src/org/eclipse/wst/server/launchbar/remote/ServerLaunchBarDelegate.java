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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.launchbar.core.internal.Activator;
import org.eclipse.remote.core.IRemoteConnection;
import org.eclipse.remote.core.IRemoteServicesManager;
import org.eclipse.remote.core.launch.IRemoteLaunchConfigService;
import org.eclipse.wst.server.ui.internal.actions.RunOnServerProcess;


/**
 * The launch configuration in charge of launching our 
 * RunOnServer actions. 
 */
public class ServerLaunchBarDelegate implements ILaunchConfigurationDelegate {

	public static final String ATTR_LAUNCH_TYPE = "wst.launchbar.launchtype";
	public static final String ATTR_LAUNCH_TYPE_MODULE = "module";
	public static final String ATTR_LAUNCH_TYPE_ARTIFACT = "artifact";
	public static final String ATTR_ARTIFACT_STRING = "artifactString";
	public static final String ATTR_ARTIFACT_CLASS = "artifactClass";
	public static final String ATTR_PROJECT = "my.projectname";
	
	
	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		IProcess dummyProcess = new RunOnServerProcess(launch);
		launch.addProcess(dummyProcess);
		
		// Fire a different thread, to simulate another UI action or user action
		// or an API call from some other plugin using public o.e.remote APIs. 
		fireThread(configuration);
		
		// Perform long-running task such as check a remote status
		for( int i = 0; i < 6; i++ ) {
			try {
				Thread.sleep(300);
			} catch(InterruptedException ie) {
				// ignore
			}
		}
		
		
		// Get target
		IRemoteLaunchConfigService service = Activator.getService(IRemoteLaunchConfigService.class);
		IRemoteConnection connection = service.getLastActiveConnection(configuration.getType());
		
		// Make sure the connection is NOT Connection2
		System.out.println(connection.getName());
		if( connection.getName().equals("Connection2")) {
			System.out.println("FAILURE:  Wrong Target");
		}
		
		// Now use this connection

		dummyProcess.terminate();
		launch.terminate();
	}
	
	public void fireThread(ILaunchConfiguration configuration) throws CoreException {
		new Thread("Name") {
			public void run() {
				// Get target
				IRemoteLaunchConfigService service = Activator.getService(IRemoteLaunchConfigService.class);
				IRemoteConnection rc = getRemoteServicesManager().getConnectionType("org.eclipse.remote.JSch").getConnection("Connection2");
				service.setActiveConnection(configuration, rc);
			}
		}.start();
	}
	
	// To allow override by tests
	IRemoteServicesManager getRemoteServicesManager() {
		return Activator.getService(IRemoteServicesManager.class);
	}
}
