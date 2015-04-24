package org.eclipse.wst.server.launchbar.remote;

import java.util.List;

import org.eclipse.remote.core.IRemoteConnection;
import org.eclipse.remote.core.IRemoteConnectionType;
import org.eclipse.remote.core.IRemoteConnectionWorkingCopy;
import org.eclipse.remote.core.IRemoteServicesManager;
import org.eclipse.remote.core.exception.RemoteConnectionException;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class RemoteConnectionHandler implements IServerLifecycleListener {
	private IRemoteServicesManager  manager;
	
	
	public RemoteConnectionHandler() {
		manager = getRemoteServicesManager();
	}
	IRemoteServicesManager getRemoteServicesManager() {
		return getService(IRemoteServicesManager.class);
	}
	public static <T> T getService(Class<T> service) {
		BundleContext context = Activator.getDefault().getBundle().getBundleContext();
		ServiceReference<T> ref = context.getServiceReference(service);
		return ref != null ? context.getService(ref) : null;
	}
	@Override
	public void serverAdded(IServer server) {
		IRemoteConnectionType type = manager.getConnectionType("org.eclipse.wst.server.launchbar.remote.connection");
		try {
			IRemoteConnectionWorkingCopy wc = type.newConnection(server.getName());
			wc.setAttribute("serverId", server.getName());
			IRemoteConnection con = wc.save();
		} catch (RemoteConnectionException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void serverChanged(IServer server) {
		// The change event could be a name change, which would screw us up a bit
		
		IRemoteConnectionType type = manager.getConnectionType("org.eclipse.wst.server.launchbar.remote.connection");
		List<IRemoteConnection> con = type.getConnections();
		IServer[] allServers = ServerCore.getServers();
		
		// remove connections with no matching server
		for( int i = 0; i < con.size(); i++ ) {
			IRemoteConnection rc = con.get(i);
			if( findServerFor(rc, allServers) == null ) {
				try {
					type.removeConnection(rc);
				} catch(RemoteConnectionException rce) {
					// TODO
				}
			}
		}
		
		// Add any connections for new servers
		allServers = ServerCore.getServers();
		for( int i = 0; i < allServers.length; i++ ) {
			if( findConnectionFor(allServers[i], con) == null) {
				serverAdded(allServers[i]);
			}
		}
	}
	
	private IRemoteConnection findConnectionFor(IServer server, List<IRemoteConnection> con) {
		for( int i = 0; i < con.size(); i++ ) {
			if( con.get(i).getName().equals(server.getName())) {
				return con.get(i);
			}
		}
		return null;
	}
	
	private IServer findServerFor(IRemoteConnection rc, IServer[] all) {
		for( int i = 0; i < all.length; i++ ) {
			if( all[i].getName().equals(rc.getName())) {
				return all[i];
			}
		}
		return null;
	}
	
	@Override
	public void serverRemoved(IServer server) {
		IRemoteConnectionType type = manager.getConnectionType("org.eclipse.wst.server.launchbar.remote.connection");
		IRemoteConnection con = type.getConnection(server.getName());
		if( con != null ) {
			try {
				type.removeConnection(con);
			} catch (RemoteConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
