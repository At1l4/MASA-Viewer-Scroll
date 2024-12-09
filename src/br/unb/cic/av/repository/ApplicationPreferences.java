/*******************************************************************************
 * *
 * * Copyright (c) 2010-2015   Edans Sandes
 * *
 * * This file is part of MASA-Viewer.
 * * 
 * * MASA-Viewer is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * * 
 * * MASA-Viewer is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * * GNU General Public License for more details.
 * * 
 * * You should have received a copy of the GNU General Public License
 * * along with MASA-Viewer.  If not, see <http://www.gnu.org/licenses/>.
 * *
 ******************************************************************************/
package br.unb.cic.av.repository;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ApplicationPreferences {
	private static final String DEFAULT_REPOSITORY_NAME = "SequenceRepository";
	private static final String REPOSITORY_PATH_PROPERTY = "repositoryPath";
	private static final String LAST_DIR_PROPERTY = "lastDir";
	private static Preferences preferences;

	static {
		preferences = Preferences
				.userNodeForPackage(ApplicationPreferences.class);
	}

	public static void clear() {
		try {
			preferences.clear();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public static File getRepositoryPath() {
		return new File(preferences.get(REPOSITORY_PATH_PROPERTY,
				System.getProperty("user.home") + File.separator
						+ DEFAULT_REPOSITORY_NAME));
	}

	public static void setRepositoryPath(File repositoryPath) {
		preferences.put(REPOSITORY_PATH_PROPERTY, repositoryPath.getPath());
	}

	public static void setLastDir(String path) {
		preferences.put(LAST_DIR_PROPERTY, path);
	}

	public static String getLastDir() {
		return preferences.get(LAST_DIR_PROPERTY, System.getProperty("user.home"));
	}
}
