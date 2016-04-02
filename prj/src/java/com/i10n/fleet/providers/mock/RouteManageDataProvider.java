package com.i10n.fleet.providers.mock;

import java.util.List;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.RoutesDaoImp;
import com.i10n.db.entity.Routes;
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
 * This class provides data for the Route tab
 * Its giving the data as global for the total Route tab.
 * It will also handle if a particular Route selected for edit. 
 * @author vikram
 *
 */

public class RouteManageDataProvider implements IDataProvider {
	
	@Override
	public IDataset getDataset(RequestParameters params) {
		IDataset result = new Dataset();
		IDataset routeAttributeMap = new Dataset();
		String login = SessionUtils.getCurrentlyLoggedInUser().getLogin();
		String routeID = params.getParameter(RequestParams.routeID);
		routeID = StringUtils.stripCommas(routeID);

		if (null != routeID) {
			Long rid = Long.parseLong(routeID);
			List<Routes> routes = ((RoutesDaoImp) DBManager.getInstance().getDao(DAOEnum.ROUTES_DAO)).selectByPrimaryKey(new LongPrimaryKey(rid));
			if (null != routes) {
				Routes rt = routes.get(0);
				result.put("routename", rt.getRouteName());
				result.put("startpoint", rt.getStartPoint());
				result.put("endpoint", rt.getEndPoint());
				result.put("starttime", rt.getStartTime());
				result.put("endtime", rt.getEndTime());
			}
		} else if (login != null ) {
			List<Routes> routeList = ((RoutesDaoImp) DBManager.getInstance().getDao(DAOEnum.ROUTES_DAO)).selectAll();
			if (null != routeList) {
				for (int i = 0; i < routeList.size(); i++) {
					Routes rt = routeList.get(i);
					
					routeAttributeMap.put("route-" + rt.getId().getId()+ ".id", rt.getId().getId());
					routeAttributeMap.put("route-" + rt.getId().getId()+ ".routename", rt.getRouteName());
					routeAttributeMap.put("route-" + rt.getId().getId()+ ".startpoint", rt.getStartPoint());
					routeAttributeMap.put("route-" + rt.getId().getId()+ ".endpoint", rt.getEndPoint());
					routeAttributeMap.put("route-" + rt.getId().getId()+ ".starttime", rt.getStartTime());
					routeAttributeMap.put("route-" + rt.getId().getId()+ ".endtime", rt.getEndTime());
				}
				result.put("routes", routeAttributeMap);
			}
		} else {
			result.put("Error.code", "1404");
			result.put("Error.name", "ResourceNotFoundError");
			result.put("Error.message", "The requested resource was not found");
			result.put("routes", routeAttributeMap);
		}
		return result;
	}

	@Override
	public String getName() {
		return "routemanage";
	}
}