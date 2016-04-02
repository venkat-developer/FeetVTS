
package com.i10n.fleet.providers.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.LogsDaoImp;
import com.i10n.db.dao.UserDaoImp;
import com.i10n.db.entity.Logs;
import com.i10n.db.entity.User;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.providers.mock.managers.ILogsManager;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * Mock : Mock Logs Data Provider : This class will be removed in future.
 * 
 * @author Sabarish
 * 
 */
public class LogsDataProvider implements IDataProvider {
	private static Logger LOG = Logger.getLogger(LogsDataProvider.class);

	private ILogsManager m_logsManager;
	String startdate,localTime;
	String enddate;
    /**
     * 
     * @param value
     * @return
     */
	
	/**
	 * Inserts the action performed by the user into the logs table
	 * @param userid
	 * @param date
	 * @param ipaddress
	 * @param username
	 * @param action
	 */
	public void getupDatedlogs(Long userid, Date date, String ipaddress, String username, String action) {
		Logs log = new Logs(null, userid, date, ipaddress, username, action);
		try {
			log = ((LogsDaoImp) (DBManager.getInstance().getDao(DAOEnum.LOGS_DAO))).insert(log);
		} catch (OperationNotSupportedException e) {
			LOG.error(e);
		}
	}

	/**
	 * @see IDataProvider#getDataset(RequestParameters)
	 */

	@SuppressWarnings("deprecation")
	public IDataset getDataset(RequestParameters params) {

		IDataset logsData = null;
		Long uid = null;
		User loguser = SessionUtils.getCurrentlyLoggedInUser();
		String login = loguser.getLogin();
		Long ownerid = loguser.getId();

		if (login != null) {
			if (uid == null) {
				IDataset data = new Dataset();
				List<User> userResultset = ((UserDaoImp) DBManager.getInstance().getDao(DAOEnum.USER_DAO)).getUsersForOwner(ownerid);
				if (null != userResultset) {
					for (int i = 0; i < userResultset.size(); i++) {
						User user = userResultset.get(i);
						data.put("user-" + user.getId() + ".firstname", "\n "+ user.getFirstname() + "\n ");
						data.put("user-" + user.getId() + ".lastname", "\n "+ user.getLastname() + "\n ");
					}
				}
				data.put("user-" + loguser.getId() + ".firstname", "\n "+ loguser.getLogin() + "\n ");
				data.put("user-" + loguser.getId() + ".lastname", "\n "+ loguser.getLogin() + "\n ");
				logsData = new Dataset();
				logsData = data;
			}
			if ((params.getParameter(RequestParams.log)) != null) {
				String userid = params.getParameter(RequestParams.userID);
				String userId = null;
				String id[] = userid.split("-");
				for (int i = 0; i < id.length; i++) {
					userId = id[i];
				}
				uid = Long.parseLong(StringUtils.stripCommas(userId.trim()));
				IDataset res = new Dataset();
				IDataset ips = new Dataset();
				List<Logs> logsip = ((LogsDaoImp) (DBManager.getInstance().getDao(DAOEnum.LOGS_DAO))).selectByUserId(uid);

				if (null != logsip){
					List<User> userResult = ((UserDaoImp) DBManager.getInstance().getDao(DAOEnum.USER_DAO)).selectByPrimaryKey(new LongPrimaryKey(uid));
					for (int i = 0; i < logsip.size(); i++) {
						Logs logg = logsip.get(i);
						String ipaddr = null;
						ipaddr = logg.getIpaddress();
						ipaddr = ipaddr.replace('.', '_');
						User user = userResult.get(0);
						
						res.put("user-" + user.getId() + ".firstname", "\n "+ user.getFirstname() + "\n ");
						res.put("user-" + user.getId() + ".lastname", "\n "+ user.getLastname() + "\n ");
						ips.put(ipaddr + ".ipaddr", logg.getIpaddress());
						res.put("user-" + logg.getUserid() + ".ips", ips);
					}
					logsData = new Dataset();
					logsData = res;
				}
			} else if ((params.getParameter(RequestParams.ip)) != null) {
				String userID = params.getParameter(RequestParams.userID);
				startdate = params.getParameter(RequestParams.startdate);
				enddate = params.getParameter(RequestParams.enddate);
				localTime = params.getParameter(RequestParams.localTime);
				Date clientTime = new Date(localTime);
				Date sdate = new Date(startdate);
				Date edate = new Date(enddate);
				Date startDate = DateUtils.adjustToLocalTime(sdate, clientTime);
				Date endDate = DateUtils.adjustToLocalTime(edate, clientTime);
				String ipaddress = params.getParameter(RequestParams.ipaddr);
				String ipaddressarray[] = null;
				String ipaddrs = null;
				ipaddressarray = ipaddress.split("\\|");
				IDataset ip = new Dataset();
				IDataset result = new Dataset();
				List<IDataset> logs = null;
				IDataset showlogset = null;
				for (int i = 0; i < ipaddressarray.length; i++) {
					ipaddrs = ipaddressarray[i];
					String userId = null;
					String id[] = userID.split("-");
					for (int j = 0; j < id.length; j++) {
						userId = id[j];
					}
					Long userid1 = Long.parseLong(StringUtils.stripCommas(userId.trim()));
					List<Logs> showlog = ((LogsDaoImp) (DBManager.getInstance().getDao(DAOEnum.LOGS_DAO))).selectByIpaddress(userid1, startDate, endDate,ipaddrs);

					if (null != showlog) {
						List<User> userset = ((UserDaoImp) DBManager.getInstance().getDao(DAOEnum.USER_DAO)).selectByPrimaryKey(new LongPrimaryKey(userid1));
						for (int j = 0; j < showlog.size(); j++) {
							Logs newlog = showlog.get(j);
							logs = new ArrayList<IDataset>();
							String ipaddrr = null;
							ipaddrr = newlog.getIpaddress();
							ipaddrr = ipaddrr.replace('.', '_');

							User user = userset.get(0);
							result.put("user-" + user.getId() + ".firstname","\n " + user.getFirstname() + "\n ");
							result.put("user-" + user.getId() + ".lastname","\n " + user.getLastname() + "\n ");
							List<Logs> showlogsip = ((LogsDaoImp) (DBManager.getInstance().getDao(DAOEnum.LOGS_DAO))).selectByIpaddress(userid1, startDate, endDate,newlog.getIpaddress());
							for (int l = 0; l < showlogsip.size(); l++) {
								Logs actions = showlogsip.get(l);
								ip.put(ipaddrr + ".ipaddr", actions.getIpaddress());

								showlogset = new Dataset();
								showlogset.put(".date", DateUtils.adjustToLocalTime(actions.getDat(),clientTime).getTime()+ "");
								showlogset.put(".action", actions.getNewdata());
								logs.add(showlogset);
								ip.put(ipaddrr + ".logs", logs);
							}
							result.put("user-" + newlog.getUserid() + ".ips",ip);
						}
					}
					logsData = new Dataset();
					logsData = result;
				}
			}
		}
		return logsData;
	}

	/**
	 * @see IDataProvider#getName()
	 */
	public String getName() {
		return "logs";
	}

	/**
	 * Returns the Logs Manager
	 * 
	 * @return
	 */
	public ILogsManager getLogsManager() {
		return m_logsManager;
	}

	/**
	 * Sets the Logs Manager
	 * 
	 * @param logsManager
	 */
	public void setLogsManager(ILogsManager logsManager) {
		m_logsManager = logsManager;
	}

}