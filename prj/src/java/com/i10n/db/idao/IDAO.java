package com.i10n.db.idao;

import java.util.List;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.IEntity.IPrimaryKey.IPrimaryKey;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("rawtypes")
public interface IDAO<T extends IEntity, PrimaryKey extends IPrimaryKey> {
	
	public T insert(T entity) throws OperationNotSupportedException;

	public T delete(T entity) throws OperationNotSupportedException;
	
	public T update(T entity) throws OperationNotSupportedException;
	
	public List<T> selectAll();
	
	public List<T> selectByPrimaryKey(PrimaryKey primaryKey);
}
