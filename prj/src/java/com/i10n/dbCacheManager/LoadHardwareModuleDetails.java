package com.i10n.dbCacheManager;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.HardwareModuleDaoImp;
import com.i10n.db.entity.HardwareModule;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;

/**
 * 
 * @author Dharmaraju V
 *
 */
public class LoadHardwareModuleDetails {

	private static Logger LOG = Logger.getLogger(LoadHardwareModuleDetails.class);
	private static LoadHardwareModuleDetails _instance = null;
	public HashMap<Long/*Hardware instance id */, HardwareModule/*Module details*/> cacheHardwareModules = 
			new HashMap<Long, HardwareModule>(); 
	public ConcurrentHashMap<Long, Vector<Long>> cacheHardwareModuleDetails = new ConcurrentHashMap<Long, Vector<Long>>();
	public static LoadHardwareModuleDetails getInstance() {
		if(_instance == null){
			_instance = new LoadHardwareModuleDetails();
			_instance.loadHardwareModuleDetails();
		}
		return _instance;
	}

	public HardwareModule retrieve(Long id){
		if(id == null){
			return null;
		}
		HardwareModule hardwareModule = cacheHardwareModules.get(id);
		if(hardwareModule == null){
			getDetailsOfUncachedHardwareModule(id);
			hardwareModule = cacheHardwareModules.get(id);
		}
		return hardwareModule;
	}

	private void loadHardwareModuleDetails(){
		LOG.info("Caching hardwaremodule details");
		List<HardwareModule> hardwareModuleList = ((HardwareModuleDaoImp)DBManager.getInstance().getDao(DAOEnum.HARDWARE_MODULES_DAO)).loadAll();
		processCacheUpdate(hardwareModuleList);
		LOG.info("Processed  hardwareModuleList data ... ");
	}

	private void processCacheUpdate(List<HardwareModule> hardwareModuleList) {
		if(hardwareModuleList != null){
			for(HardwareModule hardwareModule : hardwareModuleList){
				LOG.info("hardwareModule.getId().getId() is "+hardwareModule.getId().getId());		
				cacheHardwareModules.put(hardwareModule.getId().getId(), hardwareModule);	
			}
		}
	}

	public void getDetailsOfUncachedHardwareModule(Long id){
		LOG.debug("Caching newly added hardwaremodule details");
		List<HardwareModule> hardwareModuleList = ((HardwareModuleDaoImp)DBManager.getInstance().getDao(DAOEnum.HARDWARE_MODULES_DAO)).selectByPrimaryKey(new LongPrimaryKey(id));
		processCacheUpdate(hardwareModuleList);
	}
	
	public void refresh() {
		clearCache();
		loadHardwareModuleDetails();
	}

	private void clearCache() {
		cacheHardwareModules.clear();
	}

}
