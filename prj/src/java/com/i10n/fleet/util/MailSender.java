package com.i10n.fleet.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.MailinglistReportDaoImp;
import com.i10n.db.entity.BrowserDate;
import com.i10n.db.entity.DateRange;
import com.i10n.db.entity.MailingListReport;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

public class MailSender implements Runnable {

	private static Logger LOG = Logger.getLogger(MailSender.class);

	private static final String SMTP_HOST_NAME = "mailer.gwtrack.com";
	private static final int SMTP_HOST_PORT = 25;
	private static final String SMTP_AUTH_USER = "fleetcheck@i10n.com";
	private static final String SMTP_AUTH_PWD = "fltck@hmn";
	
	BrowserDate browserTime = new BrowserDate();

	@SuppressWarnings("deprecation")
	public void scheduler() {
		List<MailingListReport> reportList = ((MailinglistReportDaoImp) DBManager.getInstance().getDao(DAOEnum.MAILINGLIST_REPORT_DAO)).selectAll();
		MailingListReport reportMail = null;
		int schedule;
		String mailId = null;
		String mailText = new String();
		int lastScheduledAt, diff;
		int currDate = new Timestamp(Calendar.getInstance().getTimeInMillis()).getDate();
		boolean vehicleStatistics, vehicleStatus, offlineVehiclereport;
		String reportFilePath1, reportFilePath2, reportFilePath3 = null;
		String filename1 = "VehicleStatistics.txt";
		String filename2 = "VehicleStatus.txt";
		String filename3 = "OfflineVehicleReport.txt";
		if(reportList != null)
			for (int i = 0; i < reportList.size(); i++) {
				reportMail = reportList.get(i);
				/**
				 * Value 	Type of mail subscription
				 * 
				 * 0		Daily
				 * 1		Weekly
				 * 2		Monthly 
				 */
				schedule = reportMail.getSchedule();
				mailId = reportMail.getMailId();
				lastScheduledAt = reportMail.getLastScheduledAt().getDate();
				vehicleStatistics = reportMail.getVehicleStatistics();
				vehicleStatus = reportMail.getVehicleStatus();
				offlineVehiclereport = reportMail.getOfflineVehicleReport();
				DateRange dateRange = new DateRange();
				dateRange.setStart(new Date(reportMail.getLastScheduledAt().getTime()));
				dateRange.setEnd(new Date(Calendar.getInstance().getTimeInMillis()));

				diff = currDate - lastScheduledAt;

				if (diff < 0)
					diff = -(diff);
				int lastScheduledMonth = reportMail.getLastScheduledAt().getMonth();
				int lastScheduledYear = reportMail.getLastScheduledAt().getYear();
				if (schedule == 0 && diff < 7) {
					if (vehicleStatistics) {
						mailText = "The attachments of the mail provides you the daily vehiclestatisticsreport of your vehicles";
						mailText += " under the user " + reportMail.getName() +" with id "+reportMail.getId().getId()+ " of Fleet Tracking.";
						reportFilePath1 = VehicleReportsGenerator.createVehicleStatisticsReport(dateRange,reportMail.getId().getId(), reportMail.getUserId());
						this.sendMail(mailId, mailText, reportFilePath1, filename1);
					}
					if (vehicleStatus) {
						mailText = "The attachments of the mail provides you the daily vehiclestatusreport of your vehicles";
						mailText += " under the subscribed user " + reportMail.getName() +" with id "+reportMail.getId().getId()+ " of Fleet Tracking.";
						reportFilePath2 = VehicleReportsGenerator.createVehicleStatusReport(dateRange, reportMail.getId().getId(), reportMail.getUserId());
						this.sendMail(mailId, mailText, reportFilePath2, filename2);
					}

					if (offlineVehiclereport) {
						mailText = "The attachments of the mail provides you the daily offlinevehiclereport of your vehicles";
						mailText += " under the subscribed user " + reportMail.getName() +" with id "+reportMail.getId().getId()+ " of Fleet Tracking.";
						reportFilePath3 = VehicleReportsGenerator.createOfflineVehicleReport(dateRange, reportMail.getId().getId(), reportMail.getUserId());
						this.sendMail(mailId, mailText, reportFilePath3, filename3);
					}
					MailingListReport report1 = new MailingListReport(reportMail.getName(), reportMail.getUserId(), mailId, schedule,
							new Timestamp(Calendar.getInstance().getTimeInMillis()), reportMail.getVehicleStatistics(), 
							reportMail.getVehicleStatus(), reportMail.getOfflineVehicleReport());

					try {
						report1 = ((MailinglistReportDaoImp) DBManager.getInstance().getDao(DAOEnum.MAILINGLIST_REPORT_DAO)).update(report1);
					} catch (OperationNotSupportedException e) {
						LOG.error("Error while updating mailing listReport");
					}
				} else if (schedule == 1 && diff == 7) {
					if (vehicleStatistics == true) {
						mailText = "The attachments of the mail provides you the weekly vehiclestatisticsreport of your vehicles";
						mailText += " under the subscribed user " + reportMail.getId().getId()
								+ " of Fleet Tracking.";
						reportFilePath1 = VehicleReportsGenerator.createVehicleStatisticsReport(dateRange, reportMail.getId().getId(), reportMail.getUserId());
						this.sendMail(mailId, mailText, reportFilePath1, filename1);
					}
					if (vehicleStatus == true) {
						mailText = "The attachments of the mail provides you the subscribed weekly vehiclestatusreport of your vehicles";
						mailText += " under the subscribed user " + reportMail.getId().getId()+ " of Fleet Tracking.";
						reportFilePath2 = VehicleReportsGenerator.createVehicleStatusReport(dateRange, reportMail.getId().getId(), reportMail.getUserId());
						this.sendMail(mailId, mailText, reportFilePath2, filename2);
					}
					if (offlineVehiclereport == true) {
						mailText = "The attachments of the mail provides you the weekly offlinevehiclereport of your vehicles";
						mailText += " under the user " + reportMail.getId().getId()+ " of Fleet Tracking.";
						reportFilePath3 = VehicleReportsGenerator.createOfflineVehicleReport(dateRange, reportMail.getId().getId(), reportMail.getUserId());
						this.sendMail(mailId, mailText, reportFilePath3, filename3);
					}
					MailingListReport report1 = new MailingListReport(reportMail.getName(), reportMail.getUserId(), mailId, schedule,
							new Timestamp(Calendar.getInstance().getTimeInMillis()), reportMail.getVehicleStatistics(), 
							reportMail.getVehicleStatus(), reportMail.getOfflineVehicleReport());
					try{
						report1 = ((MailinglistReportDaoImp) DBManager.getInstance().getDao(DAOEnum.MAILINGLIST_REPORT_DAO)).update(report1);
					} catch (OperationNotSupportedException e) {
						LOG.error("Error while updating mailing listReport");
					}
				}

				else if ((lastScheduledMonth == 0 || lastScheduledMonth == 2 || lastScheduledMonth == 4 || lastScheduledMonth == 6
						|| lastScheduledMonth == 7 || lastScheduledMonth == 9 || lastScheduledMonth == 11) && schedule == 2 && diff == 31) {

					if (vehicleStatistics == true) {
						mailText = "The attachments of the mail provides you the monthly vehiclestatisticsreport of your vehicles";
						mailText += " under the sbscribed user " + reportMail.getId().getId()+ " of Fleet Tracking.";
						reportFilePath1 = VehicleReportsGenerator.createVehicleStatisticsReport(dateRange,reportMail.getId().getId(), reportMail.getUserId());
						this.sendMail(mailId, mailText, reportFilePath1, filename1);
					}
					if (vehicleStatus == true) {
						mailText = "The attachments of the mail provides you the monthly vehiclestatusreport of your vehicles";
						mailText += " under the subscribed user " + reportMail.getId().getId()+ " of Fleet Tracking.";
						reportFilePath2 = VehicleReportsGenerator.createVehicleStatusReport(dateRange, reportMail.getId().getId(), reportMail.getUserId());
						this.sendMail(mailId, mailText, reportFilePath2, filename2);
					}
					if (offlineVehiclereport == true) {
						mailText = "The attachments of the mail provides you the monthly offlinevehiclereport of your vehicles";
						mailText += " under the subscribed user " + reportMail.getId().getId()+ " of Fleet Tracking.";
						reportFilePath3 = VehicleReportsGenerator.createOfflineVehicleReport(dateRange, reportMail.getId().getId(), reportMail.getUserId());
						this.sendMail(mailId, mailText, reportFilePath3, filename3);
					}
					MailingListReport report1 = new MailingListReport(reportMail.getName(), reportMail.getUserId(), mailId, schedule,
							new Timestamp(Calendar.getInstance().getTimeInMillis()), reportMail.getVehicleStatistics(), 
							reportMail.getVehicleStatus(), reportMail.getOfflineVehicleReport());
					try{
						report1 = ((MailinglistReportDaoImp) DBManager.getInstance().getDao(DAOEnum.MAILINGLIST_REPORT_DAO)).update(report1);
					} catch (OperationNotSupportedException e) {
						LOG.error("Error while updating mailing listReport");
					}
				}

				else if ((lastScheduledMonth == 3 || lastScheduledMonth == 5 || lastScheduledMonth == 8 || lastScheduledMonth == 12)
						&& schedule == 2 && diff == 30) {
					if (vehicleStatistics == true) {
						mailText = "The attachments of the mail provides you the monthly vehiclestatisticsreport of your vehicles";
						mailText += " under the subscribed user " + reportMail.getId().getId()+ " of Fleet Tracking.";
						reportFilePath1 = VehicleReportsGenerator.createVehicleStatisticsReport(dateRange, reportMail.getId().getId(), reportMail.getUserId());
						this.sendMail(mailId, mailText, reportFilePath1, filename1);
					}
					if (vehicleStatus == true) {
						mailText = "The attachments of the mail provides you the monthly vehiclestatusreport of your vehicles";
						mailText += " under the subscribed user " + reportMail.getId().getId()+ " of Fleet Tracking.";
						reportFilePath2 = VehicleReportsGenerator.createVehicleStatusReport(dateRange, reportMail.getId().getId(), reportMail.getUserId());
						this.sendMail(mailId, mailText, reportFilePath2, filename2);
					}
					if (offlineVehiclereport == true) {
						mailText = "The attachments of the mail provides you the monthly offlinevehiclereport of your vehicles";
						mailText += " under the subscribed user " + reportMail.getId().getId()+ " of Fleet Tracking.";
						reportFilePath3 = VehicleReportsGenerator.createOfflineVehicleReport(dateRange, reportMail.getId().getId(), reportMail.getUserId());
						this.sendMail(mailId, mailText, reportFilePath3, filename3);
					}
					MailingListReport report1 = new MailingListReport(reportMail.getName(), reportMail.getUserId(), mailId, schedule,
							new Timestamp(Calendar.getInstance().getTimeInMillis()), reportMail.getVehicleStatistics(), 
							reportMail.getVehicleStatus(), reportMail.getOfflineVehicleReport());
					try {
						report1 = ((MailinglistReportDaoImp) DBManager.getInstance().getDao(DAOEnum.MAILINGLIST_REPORT_DAO)).update(report1);
					} catch (OperationNotSupportedException e) {
						LOG.error("Error while updating mailing listReport");
					}
				}

				else if ((lastScheduledMonth == 1 && (lastScheduledYear % 400 == 0 || (lastScheduledYear % 100 != 0 && lastScheduledYear % 4 == 0)))
						&& schedule == 2 && diff == 29) {
					if (vehicleStatistics == true) {
						mailText = "The attachments of the mail provides you the monthly vehiclestatisticsreport of your vehicles";
						mailText += " under the subscribed user " + reportMail.getId().getId()+ " of Fleet Tracking.";
						reportFilePath1 = VehicleReportsGenerator.createVehicleStatisticsReport(dateRange,reportMail.getId().getId(), reportMail.getUserId());
						this.sendMail(mailId, mailText, reportFilePath1, filename1);
					}
					if (vehicleStatus == true) {
						mailText = "The attachments of the mail provides you the monthly vehiclestatusreport of your vehicles";
						mailText += " under the subscribed user " + reportMail.getId().getId()+ " of Fleet Tracking.";
						reportFilePath2 = VehicleReportsGenerator.createVehicleStatusReport(dateRange, reportMail.getId().getId(), reportMail.getUserId());
						this.sendMail(mailId, mailText, reportFilePath2, filename2);
					}
					if (offlineVehiclereport == true) {
						mailText = "The attachments of the mail provides you the monthly offlinevehiclereport of your vehicles";
						mailText += " under the subscribed user " + reportMail.getId().getId()+ " of Fleet Tracking.";
						reportFilePath3 = VehicleReportsGenerator.createOfflineVehicleReport(dateRange, reportMail.getId().getId(), reportMail.getUserId());
						this.sendMail(mailId, mailText, reportFilePath3, filename3);
					}
					MailingListReport report1 = new MailingListReport(reportMail.getName(), reportMail.getUserId(), mailId, schedule,
							new Timestamp(Calendar.getInstance().getTimeInMillis()), reportMail.getVehicleStatistics(), 
							reportMail.getVehicleStatus(), reportMail.getOfflineVehicleReport());
					try {
						report1 = ((MailinglistReportDaoImp) DBManager.getInstance().getDao(DAOEnum.MAILINGLIST_REPORT_DAO)).update(report1);
					} catch (OperationNotSupportedException e) {
						LOG.error("Error while updating mailing listReport");
					}
				}

				else if ((lastScheduledMonth == 1 && !(lastScheduledYear % 400 == 0 || (lastScheduledYear % 100 != 0 && lastScheduledYear % 4 == 0)))
						&& schedule == 2 && diff == 28) {
					if (vehicleStatistics == true) {
						mailText = "The attachments of the mail provides you the monthly vehiclestatisticsreport of your vehicles";
						mailText += " under the subscribed user " + reportMail.getId().getId()+ " of Fleet Tracking.";
						reportFilePath1 = VehicleReportsGenerator.createVehicleStatisticsReport(dateRange,reportMail.getId().getId(), reportMail.getUserId());
						this.sendMail(mailId, mailText, reportFilePath1, filename1);
					}
					if (vehicleStatus == true) {
						mailText = "The attachments of the mail provides you the monthly vehiclestatusreport of your vehicles";
						mailText += " under the subscribed user " + reportMail.getId().getId()+ " of Fleet Tracking.";
						reportFilePath2 = VehicleReportsGenerator.createVehicleStatusReport(dateRange, reportMail.getId().getId(), reportMail.getUserId());
						this.sendMail(mailId, mailText, reportFilePath2, filename2);
					}
					if (offlineVehiclereport == true) {
						mailText = "The attachments of the mail provides you the monthly offlinevehiclereport of your vehicles";
						mailText += " under the subscribed user " + reportMail.getId().getId()+ " of Fleet Tracking.";
						reportFilePath3 = VehicleReportsGenerator.createOfflineVehicleReport(dateRange, reportMail.getId().getId(), reportMail.getUserId());
						this.sendMail(mailId, mailText, reportFilePath3, filename3);
					}
					MailingListReport report1 = new MailingListReport(reportMail.getName(), reportMail.getUserId(), mailId, schedule,
							new Timestamp(Calendar.getInstance().getTimeInMillis()), reportMail.getVehicleStatistics(), 
							reportMail.getVehicleStatus(), reportMail.getOfflineVehicleReport());
					try{
						report1 = ((MailinglistReportDaoImp) DBManager.getInstance().getDao(DAOEnum.MAILINGLIST_REPORT_DAO)).update(report1);
					} catch (OperationNotSupportedException e) {
						LOG.error("Error while updating mailing listReport");
					}
				}
			}
	}

	// Sends mails without attachments (used to send alert mails)
	public static boolean postMail(String recipients[], String subject, String message, String contentType) {
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "false");
		props.put("mail.smtp.starttls.enable","false");
		System.setProperty("java.net.preferIPv4Stack" , "true");
		Session mailSession = Session.getDefaultInstance(props);
		mailSession.setDebug(true);
		String recipientAddresses="";
		try{
			Transport transport = mailSession.getTransport();	
			Message msg = new MimeMessage(mailSession);
			InternetAddress addressFrom = new InternetAddress(SMTP_AUTH_USER);
			msg.setFrom(addressFrom);
			InternetAddress[] addressTo = new InternetAddress[recipients.length];
			for (int i = 0; i < recipients.length; i++) {
				addressTo[i] = new InternetAddress(recipients[i]);
				recipientAddresses = recipientAddresses + recipients[i];
				if (i != (recipients.length - 1)) {
					recipientAddresses = recipientAddresses + ",";
					LOG.debug("recipientAddresses Addresses is "+recipientAddresses);
				}
			}
			msg.setRecipients(Message.RecipientType.TO, addressTo);
			msg.setSubject(subject);
			msg.setContent(message, contentType);
			transport.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);	
			transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));
			transport.close();
		}catch (Exception e) {
			LOG.error("Error while posting mail ",e);
			return false;
		}
		return true;
	}

	// sends mails with attachments (used to send report mails)
	public void sendMail(String mailId, String mailText, String reportFilePath, String filename) {
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "false");
		props.put("mail.smtp.starttls.enable","false");
		System.setProperty("java.net.preferIPv4Stack" , "true");

		Session mailSession = Session.getDefaultInstance(props);
		mailSession.setDebug(true);
		Transport transport = null;
		try {
			transport = mailSession.getTransport();
			MimeMessage message = new MimeMessage(mailSession);
			Calendar cal = Calendar.getInstance();
			Date date = cal.getTime();
			message.setSubject("Automated Vehicle Reports from Fleetcheck for "+date.toString());

			MimeBodyPart messageBodyPart = new MimeBodyPart();
			MimeBodyPart messagePart = new MimeBodyPart();
			messagePart.setText(mailText);

			Multipart multipart = new MimeMultipart();

			DataSource source = new FileDataSource(reportFilePath);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(filename);
			multipart.addBodyPart(messagePart);
			multipart.addBodyPart(messageBodyPart);

			message.setContent(multipart);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailId));

			transport.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER,SMTP_AUTH_PWD);
			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
		} catch (NoSuchProviderException e) {
			LOG.error("Error while getting trasport instance for Mail session",e);
		} catch (MessagingException e) {
			LOG.error("Error while sending mail ",e);
		} finally {
			if(transport != null ){
				try {
					transport.close();
				} catch (MessagingException e) {
					LOG.error("Error while closing the mail transport service");
				}
			}
		}


	}

	@SuppressWarnings({"deprecation"})
	@Override
	public void run() {
		while (true) {
			try {
				Calendar cal = Calendar.getInstance();
				Date date = cal.getTime();
				int hours = 23 - (cal.getTime().getHours());
				int minutes = 59 - (cal.getTime().getMinutes());

				// sleep time calculation
				long sleepTime = ((hours * 60 * 60) + (minutes * 60)) * 1000;
				LOG.debug("Sleep Time : "+sleepTime);
				LOG.debug("Date.getHours() : "+ date.getHours());
				if (date.getHours() == 0){  
					LOG.debug("Calling mail scheduler");
					this.scheduler();
					sleepTime = 24*60*60*1000;
					LOG.debug("Sleeping for 24 Hours");
				}
				Thread.sleep(sleepTime);
			} catch (Exception e) {
				LOG.error("Error while sending scheduler mails",e);
			}
		}
	}
}