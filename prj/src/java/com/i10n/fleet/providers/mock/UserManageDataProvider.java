package com.i10n.fleet.providers.mock;

import java.util.List;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.UserDaoImp;
import com.i10n.db.entity.User;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;
import com.i10n.fleet.web.utils.SessionUtils;

public class UserManageDataProvider implements IDataProvider {

    @Override
    public IDataset getDataset(RequestParameters params) {
    	
        IDataset result = new Dataset();
        IDataset userAttributeMap = new Dataset();
        String login = SessionUtils.getCurrentlyLoggedInUser().getLogin();
        String userID = params.getParameter(RequestParams.userID);
        
        if(null != userID){
        	userID=StringUtils.stripCommas(userID);
            Long uid=Long.parseLong(userID);
                            
        	User user = ((UserDaoImp)DBManager.getInstance().getDao(DAOEnum.USER_DAO)).getUserList(uid);
        	if(null != user){
                result.put("firstname", user.getFirstname());
                result.put("lastname", user.getLastname());
                result.put("loginid", user.getLogin());
                result.put("passwd", user.getPassword());
                result.put("offroadcount", user.getOffroadCount());
                result.put("nogprscount", user.getNoGPRSCount());
        	}
        }else if(login != null){
			List<User> users = ((UserDaoImp)DBManager.getInstance().getDao(DAOEnum.USER_DAO)).getUsersForOwner(SessionUtils.getCurrentlyLoggedInUser().getId());
			if(null != users){
				for(int i=0;i<users.size();i++){
					User user = users.get(i);
		        	userAttributeMap.put("user-" + user.getId() + ".id", user.getId());
		            userAttributeMap.put("user-" + user.getId() + ".firstname", user.getFirstname()+"");
		            userAttributeMap.put("user-" + user.getId() + ".lastname", user.getLastname()+"");
		            userAttributeMap.put("user-" + user.getId() + ".loginid", user.getLogin());
				}
			}
			result.put("users", userAttributeMap);
        }
        else {
            result.put("Error.code", "1404");
            result.put("Error.name", "ResourceNotFoundError");
            result.put("Error.message", "The requested resource was not found");
            result.put("users", userAttributeMap);
        }
        return result;
    }
   
    @Override
    public String getName() {
        return "usermanage";
    }

}