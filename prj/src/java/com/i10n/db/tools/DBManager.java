package com.i10n.db.tools;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.idao.IDAO;

public class DBManager {
	
	private ClassPathResource classPathResource;
	
	private static DBManager instance = null;
	
	private XmlBeanFactory beanFactory;
	
	{
		classPathResource = new ClassPathResource("../dbConfig.xml");
		beanFactory = new XmlBeanFactory(classPathResource);
	}
	
	static{
		instance = new DBManager();
	}
	
	private DBManager(){		
	}
	
	
	public static DBManager getInstance(){
		return instance;
	}
	
	
	@SuppressWarnings("rawtypes")
	public IDAO getDao(DAOEnum daoId){
		IDAO dao = null;
		dao = (IDAO)(beanFactory.getBean(daoId.getValue()));
		return dao;
	}
	

	
}
