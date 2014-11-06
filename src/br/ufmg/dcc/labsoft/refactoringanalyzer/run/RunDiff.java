package br.ufmg.dcc.labsoft.refactoringanalyzer.run;


import gr.uom.java.xmi.diff.Refactoring;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import br.ufmg.dcc.labsoft.refactoringanalyzer.GitService;
import br.ufmg.dcc.labsoft.refactoringanalyzer.GitServiceImpl;
import br.ufmg.dcc.labsoft.refactoringanalyzer.RefactoringDetector;
import br.ufmg.dcc.labsoft.refactoringanalyzer.RefactoringDetectorImpl;
import br.ufmg.dcc.labsoft.refactoringanalyzer.RefactoringHandler;
import br.ufmg.dcc.labsoft.refactoringanalyzer.Revision;
import br.ufmg.dcc.labsoft.refactoringanalyzer.dao.CrudProjectDaoGit;
import br.ufmg.dcc.labsoft.refactoringanalyzer.dao.ProjectGit;
import br.ufmg.dcc.labsoft.refactoringanalyzer.util.Constantes;

public class RunDiff {

	
	
	
	public void getRevisionsDiff(ProjectGit proj) throws Exception {
		 
		try{
		 String projectFolder = Constantes.dirClone + proj.getName();
	     String url = proj.getCloneUrl();
		 
	    GitService gitService = new GitServiceImpl();
		gitService.cloneIfNotExists(projectFolder, url);
		
		
		final List<String> found = new ArrayList<String>();
		RefactoringDetector detector = new RefactoringDetectorImpl(proj, new RefactoringHandler(){
			@Override
			public void handleRefactoring(Revision revision, Refactoring refactoring) {
				found.add(revision.getId() + " " + refactoring.toString());
			}
		});
		detector.detectAll();
		}catch(UnknownHostException ex){
			System.out.println("Erro conexão internet!!!");
			System.out.println(ex.getMessage());
			
		}
		
		
	}
	
	

}
