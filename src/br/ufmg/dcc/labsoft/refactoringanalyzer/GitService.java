package br.ufmg.dcc.labsoft.refactoringanalyzer;

import java.net.UnknownHostException;

import org.eclipse.jgit.api.errors.JGitInternalException;

public interface GitService{

	void cloneIfNotExists(String folder, String cloneUrl)  throws UnknownHostException, JGitInternalException;

}
