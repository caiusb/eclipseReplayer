package edu.oregonstate.cope.replaying.junit;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.internal.wizards.datatransfer.ZipLeveledStructureProvider;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.eclipse.ui.wizards.datatransfer.ZipFileStructureProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.illinois.codingtracker.helpers.ViewerHelper;
import edu.illinois.codingtracker.operations.UserOperation;
import edu.illinois.codingtracker.replaying.OperationSequenceView;
import edu.illinois.codingtracker.replaying.UserOperationReplayer;

public class ReplayToSnapshotTest {

	private String projectName = "RecordReplayTest";
	private String innerDirName = "RecordReplayTest";

	private UserOperationReplayer replayer = null;
	private IWorkspace workspace = null;
	@SuppressWarnings("restriction")
	@Before
	public void setUp() throws Exception {
		workspace = ResourcesPlugin.getWorkspace();
		IProjectDescription newProjectDescription = workspace.newProjectDescription(projectName);
		IProject newProject = workspace.getRoot().getProject(projectName);
		if(!newProject.exists()) {
			newProject.create(newProjectDescription, null);
		}
		if(!newProject.isOpen()) {
			newProject.open(null);
		}
		
		
		String zipFilePath = "snapshots" + File.separator + "snapshot1.zip";
		ZipFile zipFile = new ZipFile( zipFilePath );
//		ZipEntry zipEntry = new ZipEntry( newProject.getLocation() + zipFilePath );
//		ZipEntry zipEntry = zipFile.getEntry("RecordReplayTest/src/RecordReplayTest.java");
		ZipEntry zipEntry = zipFile.entries().nextElement();
//		ZipFile zipFile = new ZipFile(newProject.getLocation() + "snapshots" + File.separator + "snapshot1.zip");
		ZipFileStructureProvider provider = new ZipFileStructureProvider(zipFile);
/*		ImportOperation importOperation = new ImportOperation(
			newProject.getFullPath(), 
			new ZipEntry("RecordReplayTest/.project"), 
//			zipFile,
			provider, 
			new IOverwriteQuery() {
				@Override
				public String queryOverwrite(String pathString) {
					return ALL;
				}
			}
		);*/
//		File file = new File(newProject.getLocation() + File.separator + "snapshots" + File.separator + "snapshot1");
//		File snapshotFile1 = new File("snapshots" + File.separator + "snapshot1");
//		File snapshotFile2 = new File("snapshots" + File.separator + "snapshot2");
		File snapshotsFolder = new File("snapshots");
		boolean initializedFirstSnapshot = false;
		ImportOperation importOperation = null;
		for(File snapshotDir : snapshotsFolder.listFiles()) {
			if(snapshotDir.isDirectory()) {
				
				// importing first snapshot
				if(!initializedFirstSnapshot) {
					importOperation = new ImportOperation(new Path(projectName), 
						new File(snapshotDir.getAbsolutePath() + File.separator + innerDirName + File.separator), 
						FileSystemStructureProvider.INSTANCE, 
						new IOverwriteQuery() {
							@Override
							public String queryOverwrite(String pathString) {
								return ALL;
							}
						}
					);
					
					importOperation.setCreateContainerStructure(false);
					importOperation.run(new NullProgressMonitor());
					initializedFirstSnapshot = true;
				}
			
				// importing all snapshots to separate dir
				importOperation = new ImportOperation(new Path(projectName + File.separator + snapshotDir.getName()), 
					new File(snapshotDir.getAbsolutePath() + File.separator + innerDirName), 
					FileSystemStructureProvider.INSTANCE, 
					new IOverwriteQuery() {
						@Override
						public String queryOverwrite(String pathString) {
							return ALL;
						}
					}
				);
				
				importOperation.setCreateContainerStructure(false);
				importOperation.run(new NullProgressMonitor());
			}
		}
		initializeReplayer();
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test 
	public void testProjectExists() {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		assertTrue(projects[0].getName().equals(projectName));
	}
	
	@Test
	public void testFileExists() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath path = Path.fromPortableString("RecordReplayTest.java");
		String javaFilePath = workspace.getRoot().getProject(projectName).getLocation().toString() + File.separator + "snapshot1" + File.separator + "src" + File.separator + "RecordReplayTest.java";
		File javaFile = new File(javaFilePath);
		assertTrue(javaFile.exists() && javaFile.isFile());
	}
	
	private void initializeReplayer() {
		OperationSequenceView opSeqView = null;// = new OperationSequenceView();
		try {
			opSeqView = (OperationSequenceView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("edu.illinois.codingtracker.replaying.views.OperationSequenceView");
			this.replayer = opSeqView.getUserOperationReplayer();
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(this.replayer == null) {
			fail();
		}
		loadEventsFile();
		//opSeqView.createPartControl(null);
	}
	
	private void loadEventsFile() {
		final String eventFilePath = new File("eventFiles" + File.separator + "eventFile1").getAbsolutePath();
		//this.replayer.LoadOperationsFromFile(eventFilePath);
		IAction loadAction= new Action() {
			@Override
			public void run() {
				replayer.initializeActions();
				replayer.initializeBreakpoints();
				replayer.LoadOperationsFromFile(eventFilePath);	
//				if (userOperations.size() > 0) {
//					resetAction.setEnabled(true);
//					findAction.setEnabled(true);
//					markPatternAction.setEnabled(true);
//				}
				replayer.prepareForReplay();
			}

		};
		loadAction.run();
		ViewerHelper.initAction(loadAction, "Load", "Load operation sequence from a file", true, false, false);
	}
	
//	@Test
//	public void testLoadEventFile() {
//		loadEventsFile();
//		assertEquals(70, replayer.getNumberOfUserOperations());
//	}
	
	@Test
	public void testReplay() {
//		this.replayer.replayAndAdvanceCurrentUserOperation(null, false);
		loadEventsFile();
		IAction action = new Action() {
			@Override
			public void run() {
				try {
					//replayer.prepareForReplay();
					
					//replayer.advanceCurrentUserOperation(null);
					//replayer.advanceCurrentUserOperation(UserOperationReplayer.ReplayPace.FAST);
					replayer.replayUserOperationSequence(this, UserOperationReplayer.ReplayPace.FAST);
					
				} catch ( Exception e ) {
					e.printStackTrace();
				}
			}
		};
		ViewerHelper.initAction(action, "Replay", "", false, false, false);
		action.run();
		
		/*try {
			replayer.getUserOperationExecutionThread().join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
//		try {
//			Thread.sleep(20000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		String javaFile1Path = workspace.getRoot().getProject(projectName).getLocation().toString() + File.separator + "src" + File.separator + "RecordReplayTest.java";
		String javaFile2Path = workspace.getRoot().getProject(projectName).getLocation().toString() + File.separator + "snapshot2" + File.separator + "src" + File.separator + "RecordReplayTest.java";
		String javaFile1 = "";
		String javaFile2 = "";
		try {
			javaFile1 = FileUtils.readFileToString(new File(javaFile1Path));
			javaFile2 = FileUtils.readFileToString(new File(javaFile2Path)); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
		System.out.println("Contents of " +javaFile1Path+ ":" + javaFile1);
		System.out.println("Contents of " +javaFile2Path+ ":" + javaFile2);
		assertNotEquals(javaFile1, "");
		assertNotEquals(javaFile2, "");
		assertEquals(javaFile1, javaFile2);
	}

}