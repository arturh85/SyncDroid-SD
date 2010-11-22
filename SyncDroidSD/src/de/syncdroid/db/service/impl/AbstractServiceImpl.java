package de.syncdroid.db.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.syncdroid.DatabaseHelper;
import de.syncdroid.db.model.Model;
import de.syncdroid.db.service.Service;

public abstract class AbstractServiceImpl<T extends Model> implements Service<T> {
	@Inject protected DatabaseHelper databaseHelper;

	protected abstract T read(Cursor cursor);
	protected abstract ContentValues write(T obj);
	protected abstract String getTableName();
	

	@Override
	public T findById(Long id) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.query(getTableName(), null, 
				"id = ?", new String[] {id.toString()}, 
				null, null, null, null);

		if (cursor == null || cursor.moveToFirst() == false) {
			return null;
		}
		
		T obj = read(cursor);

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}		
		
		db.close();

		return obj;
	}

	protected List<T> _list(Cursor cursor) {
		List<T> lst = new ArrayList<T>();
		if (cursor != null && cursor.moveToFirst()) {
			do {
				T obj = read(cursor);
				if(obj != null) {
					lst.add(obj);
				}
			} while (cursor.moveToNext());
		}

		
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}        

		return lst;
	}
	
	@Override
	public List<T> list() {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.query(getTableName(), null, 
				null, null, 
				null, null, null, null);

		List<T> lst = _list(cursor);
		db.close();
		
		return lst;
	}

	@Override
	public void save(T obj) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		obj.setId(db.insert(getTableName(), null, write(obj)));			
		db.close();
	}

	@Override
	public void update(T obj) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		db.update(getTableName(), write(obj), "id = ?", 
				new String[] {obj.getId().toString()});
		db.close();
	}
	
	public void saveOrUpdate(T obj) {
		if(obj.getId() == null) {
			save(obj);
		} else {
			update(obj);
		}
	}

	@Override
	public void delete(T obj) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		db.delete(getTableName(), "id = ?", new String[] {obj.getId().toString()});
		db.close();
	}
}
