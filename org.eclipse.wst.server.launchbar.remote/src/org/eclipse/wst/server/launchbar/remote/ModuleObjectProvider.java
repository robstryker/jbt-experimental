package org.eclipse.wst.server.launchbar.remote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.launchbar.core.ILaunchBarManager;
import org.eclipse.launchbar.core.ILaunchObjectProvider;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.ModuleArtifactDelegate;
import org.eclipse.wst.server.ui.internal.Trace;

public class ModuleObjectProvider implements ILaunchObjectProvider, 
	IResourceChangeListener, ILaunchConfigurationListener {
	
	private ILaunchBarManager manager;
	private HashMap<IProject, ModuleWrapper[]> knownModules;

	
	public static class ModuleWrapper {
		private IModule module;
		public ModuleWrapper(IModule m) {
			this.module = m;
		}
		
		public IModule getModule() {
			return module;
		}
	}
	
	@Override
	public void init(ILaunchBarManager manager) throws CoreException {
		this.manager = manager;
		
		// Initialize the modulewrapper objects (wrappers with no artifacts)
		knownModules = new HashMap<IProject, ModuleWrapper[]>();
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			resourceChanged(project, null);
		}
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
		
		
		ILaunchConfiguration[] all = getLaunchManager().getLaunchConfigurations();
		for( int i = 0; i < all.length; i++ ) {
			if( all[i].getType().getIdentifier().equals(WTP_LAUNCH_TYPE)) {
				launchConfigurationAdded(all[i]);
			}
		}
		// initialize the artifact adapter launches
		getLaunchManager().addLaunchConfigurationListener(this);
	}
	
	// To allow override by tests
	ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		try {
			event.getDelta().accept(new IResourceDeltaVisitor() {
				@Override
				public boolean visit(IResourceDelta delta) throws CoreException {
					IResource res = delta.getResource();
					if (res instanceof IProject) {
						return resourceChanged((IProject)res, delta);
					} else if (res instanceof IFile || res instanceof IFolder) {
						return false;
					}
					return true;
				}
			});
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(e.getStatus());
		}
	}
	
	private ModuleWrapper[] convert(IModule[] modules) {
		ArrayList<ModuleWrapper> list = new ArrayList<ModuleWrapper>();
		for( int i = 0; i < modules.length; i++ ) {
			list.add(new ModuleWrapper(modules[i]));
		}
		return (ModuleWrapper[]) list.toArray(new ModuleWrapper[list.size()]);
	}
	
	private boolean resourceChanged(IProject project, IResourceDelta delta) throws CoreException {
		if( project == null )
			return false;
		
		IModule[] modules = ServerUtil.getModules((project));
		int kind = delta == null ? IResourceDelta.ADDED : delta.getKind();
		ModuleWrapper[] known = knownModules.get(project);
		if ((kind & IResourceDelta.ADDED) != 0) {
			if( known != null ) {
				alertRemoved(known);
			}
			ModuleWrapper[] newModules = convert(modules);
			alertAdded(newModules);
			knownModules.put(project, newModules);
		} else if ((kind & IResourceDelta.REMOVED) != 0) {
			if( known != null ) {
				alertRemoved(known);
			}
			knownModules.remove(project);
		} else if ((kind & IResourceDelta.CHANGED) != 0) {
			ModuleWrapper[] newModules = convert(modules);
			handleChanged(known, newModules);
		}
		return false;
	}
	
	private void alertRemoved(ModuleWrapper[] all) throws CoreException {
		for( int i = 0; i < all.length; i++ ) {
			manager.launchObjectRemoved(all[i]);
		}
	}
	private void alertAdded(ModuleWrapper[] all) throws CoreException {
		for( int i = 0; i < all.length; i++ ) {
			manager.launchObjectAdded(all[i]);
		}
	}
	private void alertChanged(ModuleWrapper[] all) throws CoreException {
		for( int i = 0; i < all.length; i++ ) {
			manager.launchObjectChanged(all[i]);
		}
	}
	private void handleChanged(ModuleWrapper[] old, ModuleWrapper[] nnew) throws CoreException {
		if( old == null && nnew != null ) {
			// All are added
			alertAdded(nnew);
		} else if( nnew == null && old != null ) {
			// All are removed
			alertRemoved(old);
		} else {
			ModuleWrapper[] missing = findMissing(old, nnew);
			ModuleWrapper[] added = findMissing(nnew, old);
			ArrayList<ModuleWrapper> changed = new ArrayList<ModuleWrapper>(Arrays.asList(nnew));
			changed.removeAll(Arrays.asList(added));
			changed.removeAll(Arrays.asList(missing));
			ModuleWrapper[] changedArray = (ModuleWrapper[]) changed.toArray(new ModuleWrapper[changed.size()]);
			alertAdded(added);
			alertRemoved(missing);
			alertChanged(changedArray);
		}
	}
	
	private ModuleWrapper[] findMissing(ModuleWrapper[] old, ModuleWrapper[] nnew) {
		ArrayList<ModuleWrapper> missing = new ArrayList<ModuleWrapper>();
		for( int i = 0; i < old.length; i++ ) {
			if( !isPresent(old[i], nnew)) {
				missing.add(old[i]);
			}
		}
		return (ModuleWrapper[]) missing.toArray(new ModuleWrapper[missing.size()]);
	}
	
	private boolean isPresent(ModuleWrapper needle, ModuleWrapper[] haystack) {
		for( int i = 0; i < haystack.length; i++ ) {
			if( needle.getModule().getId().equals(haystack[i].getModule().getId())) {
				return true;
			}
		}
		return false;
	}

	
	
	public static class ModuleArtifactWrapper {
		protected String artifact, clazz, name;
		protected IModule module;
		protected ModuleArtifactDelegate moduleArtifact;
		boolean attemptedArtifactLoad = false;
		private ModuleArtifactWrapper(String name, String artifact, String clazz ) {
			this.artifact = artifact;
			this.clazz = clazz;
			this.name = getArtifactDelegate() == null ? name : getArtifactDelegate().getName();
		}
		
		public ModuleArtifactDelegate getArtifactDelegate() {
			if( moduleArtifact == null && !attemptedArtifactLoad) {
				moduleArtifact = ServerLaunchBarDelegate.getArtifact(clazz, artifact);
				attemptedArtifactLoad = true;
			}
			return moduleArtifact;
		}
		
		public String getArtifactClass() {
			return clazz;
		}
		
		public String getArtifactString() {
			return artifact;
		}
		
		public String getName() {
			return name;
		}
		
		public IModule getModule() {
			return module;
		}
		
		public boolean equals(Object other) {
			if(other instanceof ModuleArtifactWrapper) {
				String otherArt = ((ModuleArtifactWrapper)other).artifact;
				String otherClazz = ((ModuleArtifactWrapper)other).clazz;
				return otherArt.equals(this.artifact) && otherClazz.equals(clazz); 
			}
			return false;
		}
		public int hashcode() {
			return (artifact + "::" + clazz).hashCode();
		}
	}
	
	private static String WTP_LAUNCH_TYPE = "org.eclipse.wst.server.ui.launchConfigurationType";
	
	@Override
	public void launchConfigurationAdded(ILaunchConfiguration configuration) {
		try {
			String typeId = configuration.getType().getIdentifier();
			if(WTP_LAUNCH_TYPE.equals(typeId)) {
				manager.launchObjectAdded(getArtifactWrapperFor(configuration));
			}
		} catch(CoreException ce) {
			ce.printStackTrace();
		}
		
	}
	
	private ModuleArtifactWrapper getArtifactWrapperFor(ILaunchConfiguration configuration) {
		System.out.println("Found one");
		
		String ATTR_SERVER_ID = "server-id";
		String ATTR_MODULE_ARTIFACT = "module-artifact";
		String ATTR_MODULE_ARTIFACT_CLASS = "module-artifact-class";

		String ATTR_LAUNCHABLE_ADAPTER_ID = "launchable-adapter-id";
		String ATTR_CLIENT_ID = "client-id";
		
		try {
			String artifact = configuration.getAttribute(ATTR_MODULE_ARTIFACT, (String)null);
			String clazz = configuration.getAttribute(ATTR_MODULE_ARTIFACT_CLASS, (String)null);
			
			ModuleArtifactWrapper wrapper = new ModuleArtifactWrapper(configuration.getName(), artifact, clazz);
			return wrapper;
		} catch(CoreException ce) {
			ce.printStackTrace();
		}
		return null;
	}

	@Override
	public void launchConfigurationChanged(ILaunchConfiguration configuration) {
		try {
			String typeId = configuration.getType().getIdentifier();
			if("org.eclipse.wst.server.ui.launchConfigurationType".equals(typeId)) {
				ModuleArtifactWrapper w = getArtifactWrapperFor(configuration);
				if( w != null )
					manager.launchObjectChanged(w);
			}
		} catch(CoreException ce) {
			ce.printStackTrace();
		}
	}

	@Override
	public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
		try {
			String typeId = configuration.getType().getIdentifier();
			if("org.eclipse.wst.server.ui.launchConfigurationType".equals(typeId)) {
				ModuleArtifactWrapper w = getArtifactWrapperFor(configuration);
				if( w != null )
					manager.launchObjectRemoved(w);
			}
		} catch(CoreException ce) {
			ce.printStackTrace();
		}
	}
}
