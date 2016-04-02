package com.i10n.fleet.providers.mock;

import java.util.List;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.HardwareModuleDaoImp;
import com.i10n.db.entity.HardwareModule;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * This class provides data for the HardwareModule tab
 * Its giving the data as global for the total hardware tab.
 * It will also handle if a particular hardware selected for edit. 
 * @author joshua
 *
 */
public class HardwareManageDataProvider implements IDataProvider {

	@Override
	public IDataset getDataset(RequestParameters params) {
		IDataset result = new Dataset();
		IDataset hardwareAttributeMap = new Dataset();
		String login = SessionUtils.getCurrentlyLoggedInUser().getLogin();
		String hardwareID = params.getParameter(RequestParams.hardwareID);
		hardwareID =StringUtils.stripCommas(hardwareID);
		if (null != hardwareID) {
			Long hid = Long.parseLong(hardwareID);
			List<HardwareModule> hardwaremodule = ((HardwareModuleDaoImp) DBManager.getInstance().getDao(DAOEnum.HARDWARE_MODULES_DAO)).selectByPrimaryKey(new LongPrimaryKey(hid));
			if (null != hardwaremodule) {
				HardwareModule hd = hardwaremodule.get(0);
				result.put("imei", hd.getImei());
				result.put("moduleversion", hd.getModuleVersion()+"");
				result.put("firmwareversion", hd.getFirmwareversion()+"");
				result.put("oldimei", hd.getImei());
				result.put("mobilenumber", hd.getMobileNumber()+"");
				result.put("simid", hd.getSimId()+"");
				result.put("simprovider", hd.getSimProvider());
				result.put("oldmobilenumber", hd.getMobileNumber());
				result.put("oldsimid", hd.getSimId());
			}
		} else if (login != null ) {
			List<HardwareModule> hardwareList = ((HardwareModuleDaoImp) DBManager.getInstance().getDao(DAOEnum.HARDWARE_MODULES_DAO)).selectAll();
			if (null != hardwareList) {
				for (int i = 0; i < hardwareList.size(); i++) {
					HardwareModule hd = hardwareList.get(i);
					hardwareAttributeMap.put("hardware-" + hd.getId().getId()+ ".id", hd.getId().getId());
					hardwareAttributeMap.put("hardware-" + hd.getId().getId()+ ".imei", hd.getImei());
					hardwareAttributeMap.put("hardware-" + hd.getId().getId()+ ".moduleversion", hd.getModuleVersion());
					hardwareAttributeMap.put("hardware-" + hd.getId().getId()+ ".firmwareversion", hd.getFirmwareversion());
					hardwareAttributeMap.put("hardware-" + hd.getId().getId()+ ".mobilenumber", hd.getMobileNumber());
					hardwareAttributeMap.put("hardware-" + hd.getId().getId()+ ".simid", hd.getSimId());
					
				}
				result.put("hardwares", hardwareAttributeMap);
			}
		} else {
			result.put("Error.code", "1404");
			result.put("Error.name", "ResourceNotFoundError");
			result.put("Error.message", "The requested resource was not found");
			result.put("hardwares", hardwareAttributeMap);
		}
		return result;
	}

	@Override
	public String getName() {
		return "hardwaremanage";
	}

}