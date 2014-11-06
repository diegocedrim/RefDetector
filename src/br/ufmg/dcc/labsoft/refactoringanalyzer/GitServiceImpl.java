package br.ufmg.dcc.labsoft.refactoringanalyzer;

import java.io.File;
import java.net.UnknownHostException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.errors.TransportException;

public class GitServiceImpl implements GitService {

	@Override
	public void cloneIfNotExists(String folder, String cloneUrl) {
		try{
		File f = new File(folder);
		if (!f.exists()) {
			Git.cloneRepository()
			.setDirectory(f)
			.setURI(cloneUrl)
			.setCloneAllBranches(true)
			.call();	
		}
		}catch (JGitInternalException ex) {
			System.out
			.println("Falha conexão internet: " + ex.getMessage());
}
		
	}

}
