package pt.up.fc.dcc.mooshak.content;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.content.types.Profile;
import pt.up.fc.dcc.mooshak.content.types.Profiles;
import pt.up.fc.dcc.mooshak.content.types.Top;
import pt.up.fc.dcc.mooshak.content.types.User;
import pt.up.fc.dcc.mooshak.content.types.Users;

/**
 * Fix problems with the data structure of Mooshak when it is imported 
 * from previous versions. 
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class Healer {

	private static Logger LOGGER = Logger.getLogger("");
	
	Top data;
	
	public Healer() throws MooshakContentException {
		 data = PersistentObject.openPath("data");
	}
	
	public void heal() throws MooshakContentException {
		healProfiles();
		healUsers();
	}

	private void healUsers() throws MooshakContentException {
		Profiles profiles = data.open("configs/profiles");
		Users users = data.open("configs/users");
		
		for(String name: Arrays.asList("public","creator"))
		if(missing(users,name)) {
			User user = users.create(name, User.class);
			user.setName(name);
			user.setPassword("");
			user.setProfile(profiles.open(name));
			user.save();
			LOGGER.info("Healer: Created missing user "+name);
		}
	}

	private void healProfiles() throws MooshakContentException {
		Profiles profiles = data.open("configs/profiles");
		
		for(String name: Arrays.asList("public","creator"))
			if(missing(profiles,name)) {
				Profile profile = profiles.create(name, Profile.class);
				profile.save();
				LOGGER.info("Healer: Created missing profile "+name);
			}
	}
	
	
	private boolean missing(PersistentObject persistent, String name) {
		Path path = persistent.getAbsoluteFile(name);
		
		return ! PersistentObject.isPersistentObjectAbsolutePath(path);
	}
}
