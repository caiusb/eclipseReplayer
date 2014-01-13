package edu.oregonstate.cope.replaying.junit;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ltk.internal.core.refactoring.resource.undostates.ProjectUndoState;
import org.junit.Test;

import edu.oregonstate.cope.eclipse.ProjectLoader;
import edu.oregonstate.cope.eclipse.ProjectLoader.Util;

public class ProjectLoaderTest {

	private String projectName = "ProjectLoadTest";
	
	@Test
	public void testUnzip() throws IOException {
		String zipFilePath = "snapshots" + File.separator + "snapshot1.zip";
		String snapshotPath = "snapshots" + File.separator + "testsnapshot1";
		ProjectLoader.Util.unzipSnapshot(zipFilePath, snapshotPath);
		File snapshotDir = new File(snapshotPath);
		assertTrue(snapshotDir.exists() && snapshotDir.isDirectory());
		ProjectLoader.Util.deleteFolder(snapshotDir);
	}
	
	@Test 
	public void testProjectExists() {
		try {
			ProjectLoader.createProjectInCurrentWorkspace(projectName);
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			assertTrue(projects[0].getName().equals(projectName));
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFileExists() {
		try {
			ProjectLoader.createProjectInCurrentWorkspace(projectName);
			String zipFilePath = "snapshots" + File.separator + "snapshot1.zip";
			String snapshotPath = "snapshots" + File.separator + "testsnapshot1";
			ProjectLoader.Util.unzipSnapshot(zipFilePath, snapshotPath);
			ProjectLoader.loadProjectFromSnapshot(projectName, snapshotPath);
		} catch (InvocationTargetException | InterruptedException | IOException | CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath path = Path.fromPortableString("RecordReplayTest.java");
		String javaFilePath = workspace.getRoot().getProject(projectName).getLocation().toString() + File.separator + "src" + File.separator + "RecordReplayTest.java";
		System.out.println(javaFilePath);
		File javaFile = new File(javaFilePath);
		assertTrue(javaFile.exists() && javaFile.isFile());
	}

}
