package pt.up.fc.dcc.mooshak.client.data.admin;

/**
 * Contains form data and is able to notify form displaying it 
 * when data is changed
 * 
 * @author José paulo Leal <zp@dcc.fc.up.pt>
 */
public interface FormDataProvider {

	/**
	 * Notify this form of changes to provided data
	 * @param form
	 */
	public void addFormDataProvider(HasFormData form);
	
	/**
	 * Don't notify this form of changes to provided data
	 * @param form
	 */
	public void removeFormDataProvider(HasFormData form);
	
	/**
	 * Refresh all forms currently displaying the provided data 
	 */
	public void refresh();
}
