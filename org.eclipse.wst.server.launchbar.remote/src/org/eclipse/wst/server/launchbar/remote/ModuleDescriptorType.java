package org.eclipse.wst.server.launchbar.remote;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.launchbar.core.ILaunchDescriptor;
import org.eclipse.launchbar.core.ILaunchDescriptorType;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.launchbar.remote.ModuleObjectProvider.ModuleArtifactWrapper;
import org.eclipse.wst.server.launchbar.remote.ModuleObjectProvider.ModuleWrapper;

public class ModuleDescriptorType implements ILaunchDescriptorType {

	public ModuleDescriptorType() {
	}
	
	@Override
	public boolean ownsLaunchObject(Object launchObject) throws CoreException {
		if( launchObject instanceof ModuleWrapper)
			return true;
		if( launchObject instanceof ModuleArtifactWrapper) 
			return true;
		return false;
	}

	public  static class ModuleLaunchDescriptor implements ILaunchDescriptor {
		private ModuleWrapper module;
		private ILaunchDescriptorType type;
		public ModuleLaunchDescriptor(ModuleWrapper wrap,ILaunchDescriptorType type) {
			this.module = wrap;
			this.type = type;
		}
		public IModule getModule() {
			return module.getModule();
		}
		
		public ModuleWrapper getWrapper() {
			return module;
		}
		
		@Override
		public Object getAdapter(Class adapter) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getName() {
			return getModule().getName();
		}

		@Override
		public ILaunchDescriptorType getType() {
			return type;
		}
	}
	
	public  static class ModuleArtifactLaunchDescriptor implements ILaunchDescriptor {
		private ModuleArtifactWrapper module;
		private ILaunchDescriptorType type;
		public ModuleArtifactLaunchDescriptor(ModuleArtifactWrapper wrap,ILaunchDescriptorType type) {
			this.module = wrap;
			this.type = type;
		}
		public IModule getModule() {
			return module.getModule();
		}
		
		public ModuleArtifactWrapper getArtifactWrapper() {
			return module;
		}
		
		@Override
		public Object getAdapter(Class adapter) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getName() {
			return module.getName();
		}

		@Override
		public ILaunchDescriptorType getType() {
			return type;
		}
	}
	
	@Override
	public ILaunchDescriptor getDescriptor(Object launchObject) throws CoreException {
		if( launchObject instanceof ModuleWrapper ) 
			return new ModuleLaunchDescriptor((ModuleWrapper)launchObject, this);
		if( launchObject instanceof ModuleArtifactWrapper ) 
			return new ModuleArtifactLaunchDescriptor((ModuleArtifactWrapper)launchObject, this);
		return null;
	}

}
