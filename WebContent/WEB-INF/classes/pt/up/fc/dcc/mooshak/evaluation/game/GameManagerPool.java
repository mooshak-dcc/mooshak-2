package pt.up.fc.dcc.mooshak.evaluation.game;

import java.io.IOException;
import java.nio.file.Path;

import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.util.JarFileClassLoader;
import pt.up.fc.dcc.mooshak.content.util.ObjectPool;
import pt.up.fc.dcc.mooshak.evaluation.game.wrappers.GameManagerWrapper;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Pool of wrapped game managers
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class GameManagerPool extends ObjectPool<GameManagerWrapper> {
	
	private String gamePackagePath;
	private String gameManagerClassname;
	
	public GameManagerPool(Path gamePackagePath, String gameManagerClassname) {
		this.gamePackagePath = PersistentCore.getAbsoluteFile(gamePackagePath)
				.toString();
		this.gameManagerClassname = gameManagerClassname;
	}
	
	public void setGamePackagePath(Path gamePackagePath) {
		
		String previousGamePackagePath = this.gamePackagePath;
		this.gamePackagePath = PersistentCore.getAbsoluteFile(gamePackagePath)
				.toString();
		
		if (previousGamePackagePath != null && 
				!previousGamePackagePath.equals(this.gamePackagePath))
			setAllObjectsToExpire();
	}
	
	public void setGameManagerClassname(String gameManagerClassname) {
		
		String previousGameManagerClassname = this.gameManagerClassname;
		this.gameManagerClassname = gameManagerClassname;
		
		if (previousGameManagerClassname != null && 
				!previousGameManagerClassname.equals(this.gameManagerClassname))
			setAllObjectsToExpire();
	}

	@Override
	protected GameManagerWrapper create() throws MooshakException {
		
		Object gameManagerObj;
		try {
			gameManagerObj = JarFileClassLoader.initializeClassFromJar(
					gamePackagePath, gameManagerClassname);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
			throw new MooshakException("Could not create game manager!", e);
		}

		return new GameManagerWrapper(gameManagerObj);
	}

	@Override
	public boolean validate(GameManagerWrapper o) {		
		
		return true;
	}

	@Override
	public void expire(GameManagerWrapper o) {
		
	}

}
