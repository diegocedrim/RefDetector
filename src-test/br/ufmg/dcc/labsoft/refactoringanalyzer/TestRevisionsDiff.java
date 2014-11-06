package br.ufmg.dcc.labsoft.refactoringanalyzer;

import static org.hamcrest.CoreMatchers.equalTo;
import gr.uom.java.xmi.diff.Refactoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.ufmg.dcc.labsoft.refactoringanalyzer.dao.CrudProjectDaoGit;
import br.ufmg.dcc.labsoft.refactoringanalyzer.dao.ProjectGit;

public class TestRevisionsDiff {
	
	private static CrudProjectDaoGit crud = new CrudProjectDaoGit();

	@Test
	public void testRevisionsDiff() throws Exception {
		GitService gitService = new GitServiceImpl();
		List<ProjectGit> projetoList = crud.getProjects();
		gitService.cloneIfNotExists("D:\\dirgithub2\\junit", "https://github.com/junit-team/junit.git");
		
		final List<String> found = new ArrayList<String>();
		RefactoringDetector detector = new RefactoringDetectorImpl(projetoList.get(0), new RefactoringHandler(){
			@Override
			public void handleRefactoring(Revision revision, Refactoring refactoring) {
				found.add(revision.getId() + " " + refactoring.toString());
			}
		});
		detector.detectAll();

		final List<String> expected = new ArrayList<String>();
		expected.addAll(Arrays.asList(
			"36287f7c3b09eff78395267a3ac0d7da067863fd Move Attribute	private age : int from class org.animals.Labrador to class org.animals.Dog",
			"36287f7c3b09eff78395267a3ac0d7da067863fd Move Attribute	private age : int from class org.animals.Poodle to class org.animals.Dog",
			"36287f7c3b09eff78395267a3ac0d7da067863fd Move Operation	public getAge() : int from class org.animals.Labrador to public getAge() : int from class org.animals.Dog",
			"36287f7c3b09eff78395267a3ac0d7da067863fd Move Operation	public getAge() : int from class org.animals.Poodle to public getAge() : int from class org.animals.Dog",
			"40950c317bd52ea5ce4cf0d19707fe426b66649c Extract Operation	public takeABreath() : void extracted from public bark() : void in class org.animals.Dog",
			"63cbed99a601e79c6a0ae389b2a57acdbd3e1b44 Rename Class	org.animals.Cow renamed to org.animals.CowRenamed",
			"58495630295833c9d73559bd958c2f95339f9c62 Extract Superclass	org.animals.Bird from classes [org.animals.Chicken, org.animals.Duck]",
			"70b71b7fd3c5973511904c468e464d4910597928 Move Class	org.animals.Cat moved to org.felines.Cat",
			"05c1e773878bbacae64112f70964f4f2f7944398 Extract Superclass	org.felines.Feline from classes [org.felines.Cat]"
		));
		Collections.sort(found);
		Collections.sort(expected);
		Assert.assertThat(found, equalTo(expected));
	}

}
