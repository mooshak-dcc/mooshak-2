package pt.up.fc.dcc.mooshak.rest.repository;

import java.util.List;

/**
 * Interface of a repository of objects
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 *
 * @param <T> Type of Model
 */
public interface Repository<T> {

	/**
	 * Get all objects of this repository
	 * 
	 * @return {@code List<T>} all objects of this repository
	 */
	public List<T> getAll();
	
	/**
	 * Add object to repository
	 * 
	 * @param obj {@code T} object to add
	 */
	public void add(T obj);
	
	/**
	 * Update object from repository
	 * 
	 * @param obj {@code T} new object
	 * @param id {@code String} ID of the object
	 */
	public void update(T obj, String id);
	
	/**
	 * Get an object given its ID
	 * 
	 * @param id {@code String} ID of the object
	 * @return {@code T} object
	 */
	public T get(String id);
	
	/**
	 * Delete an object given its ID
	 * 
	 * @param id {@code String} ID of the object
	 */
	public void delete(String id);

}
