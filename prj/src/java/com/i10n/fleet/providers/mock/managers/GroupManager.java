package com.i10n.fleet.providers.mock.managers;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.GroupDaoImpl;
import com.i10n.db.entity.Group;
import com.i10n.db.entity.GroupValues;
import com.i10n.db.entity.User;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * Mock : Group Manager - manages group data.This class will be removed in
 * future
 * 
 * @author Sabarish
 * 
 */
public class GroupManager extends AbstractDataManager implements IGroupManager {
	
	private static Logger LOG = Logger.getLogger(GroupManager.class);

	private static final String FILE_DOCUMENT = "/mock/groups.xml";
	private IDataset g_dataset = null;
	/**
	 * @see AbstractDataManager#getDocumentName()
	 */
	@Override
	protected String getDocumentName() {
		return FILE_DOCUMENT;
	}

	/**
	 * @see IDataManager#getData(IDataset)
	 */
	public IDataset getData(IDataset options) {
		IDataset result = new Dataset();
		if ( g_dataset == null){
			result=parseDataset();
		}
		return result;
	}

	private IDataset parseDataset(){
		LOG.debug("In Groupmanager parseDataset starting");
		IDataset groupData= new Dataset();
		User user = SessionUtils.getCurrentlyLoggedInUser();
		Long groupId = user.getGroupId();
		List <Group> groupDataset = ((GroupDaoImpl)DBManager.getInstance().getDao(DAOEnum.GROUP_DAO)).selectByPrimaryKey(new LongPrimaryKey(groupId));
		Group group=null;
		if(groupDataset != null){
			for(int i=0; i<groupDataset.size();i++){
				group=groupDataset.get(i);
				Iterator<GroupValues> it = group.getGroupValues().iterator();
				while (it.hasNext()) {  
					GroupValues groupvalues = (GroupValues) it.next();
					groupData.put("group-"+groupvalues.getId().getId()+".id","group-"+groupvalues.getId().getId());
					groupData.put("group-"+groupvalues.getId().getId()+".name", groupvalues.getGroupValue());
				}
			}
		}
		else{
			LOG.debug("Group Data is NULL");
		}
		LOG.debug("In Groupmanager parseDataset starting");
		return groupData; 
	}
}