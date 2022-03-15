
/**
 * Requests handled by Mooshak. Each class in this message groups a
 * genre of request. Currently supported genres are
 * 
 *  <ul>
 *  	<li> AuthManager - authentication/authorization and session management
 *  	<li> ParticipantManager - commands used be participants (teams, students)
 *  	<li> AdministrationManager - commands used by administrators
 *  </ul>
 * 
 * Classes in this package ignore types from any particular communication layer,
 * such as GWT RPC or Jersey
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 * @version 2.0
 * @since 2013-02-21
 */
package pt.up.fc.dcc.mooshak.managers;