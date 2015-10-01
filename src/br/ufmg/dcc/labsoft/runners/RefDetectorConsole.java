package br.ufmg.dcc.labsoft.runners;
import gr.uom.java.xmi.ASTReader;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.CommitRefactorings;
import gr.uom.java.xmi.diff.Refactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

import br.ufmg.dcc.labsoft.refdetector.RefDetectionException;


public class RefDetectorConsole {

	private Repository getGitRepo(File sourceFolder) throws IOException {
		RepositoryBuilder builder = new RepositoryBuilder();
		Repository repository = builder
				.setGitDir(new File(sourceFolder.getAbsolutePath(), ".git"))
				.readEnvironment()
				.findGitDir()
				.build();
		return repository;
	}
	
	private CommitRefactorings createFromGitSourceFolders(File oldVersionSourceFolder, File newVersionSourceFolder) 
			throws IOException {
		CommitRefactorings refactorings = new CommitRefactorings();
		Repository oldRepository = getGitRepo(oldVersionSourceFolder);
		ObjectId oldHeadId = oldRepository.resolve(Constants.HEAD);
		refactorings.setCommitHashFrom(oldHeadId.getName());
		
		Repository newRepository = getGitRepo(newVersionSourceFolder);
		ObjectId newHeadId = newRepository.resolve(Constants.HEAD);
		refactorings.setCommitHashTo(newHeadId.getName());
		
		Config storedConfig = oldRepository.getConfig();
		String oldUrl = storedConfig.getString("remote", "origin", "url");
		
		storedConfig = newRepository.getConfig();
		String newUrl = storedConfig.getString("remote", "origin", "url");
		
		if (!oldUrl.equals(newUrl)) {
			throw new RefDetectionException("Comparing different projects! Aborting...");
		}
		refactorings.setGitRepositoryUrl(newUrl);
		return refactorings;
	}
	
	public CommitRefactorings detectRefactorings(File oldVersionSourceFolder, File newVersionSourceFolder) throws IOException {
		CommitRefactorings envelope = this.createFromGitSourceFolders(oldVersionSourceFolder, newVersionSourceFolder);
		UMLModel oldModel = new ASTReader(oldVersionSourceFolder).getUmlModel();
		UMLModel newModel = new ASTReader(newVersionSourceFolder).getUmlModel();
		UMLModelDiff modelDiff = oldModel.diff(newModel);
		List<Refactoring> refactorings = modelDiff.getRefactorings();
		envelope.setRefactorings(refactorings);
		return envelope;
	}
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Missing arguments. You must provide two folders name.");
			System.exit(-1);
		}

		try {
			String oldCodeFolder = args[0];
			String newCodeFolder = args[1];
			RefDetectorConsole detector = new RefDetectorConsole();
			CommitRefactorings refactorings = detector.detectRefactorings(new File(oldCodeFolder), new File(newCodeFolder));
			System.out.println(refactorings.toJsonString());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
