package pt.up.fc.dcc.mooshak.client.utils;

import java.util.Collection;

import com.google.gwt.user.client.ui.HasValue;


/**
 * MVP-friendly interface for use with any widget that can be populated
 * with a Collection of items, one of which may be selected 
 * 
 * @author David Chandler
 *
 * @param <T>
 */
public interface HasSelectedValue<T> extends HasValue<T>
{
    void setSelections(Collection<T> selections);
    
    Collection<T> getSelections();
 
    void setSelectedValue(T selected);
     
    T getSelectedOption();
}