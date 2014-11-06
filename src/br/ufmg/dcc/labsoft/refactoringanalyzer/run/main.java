package br.ufmg.dcc.labsoft.refactoringanalyzer.run;

import java.io.File;
import java.net.UnknownHostException;
import java.util.List;

import org.eclipse.jgit.api.errors.JGitInternalException;

import br.ufmg.dcc.labsoft.refactoringanalyzer.TestRevisionsDiff;
import br.ufmg.dcc.labsoft.refactoringanalyzer.dao.CrudProjectDaoGit;
import br.ufmg.dcc.labsoft.refactoringanalyzer.dao.ProjectGit;
import br.ufmg.dcc.labsoft.refactoringanalyzer.operations.BuscaRepositorioGitHub;
import br.ufmg.dcc.labsoft.refactoringanalyzer.util.Constantes;
import br.ufmg.dcc.labsoft.refactoringanalyzer.util.DeleteDiretorios;
import br.ufmg.dcc.labsoft.refactoringanalyzer.util.InternetReachable;

public class main {

	/**
	 * @param args
	 * @throws Exception
	 */

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		BuscaRepositorioGitHub bUrl = new BuscaRepositorioGitHub();
		DeleteDiretorios del = new DeleteDiretorios();
		int op = 2;

		switch (op) {
		case 1:
			// Busca de projetos escritos em java e com stars > 100
			new BuscaRepositorioGitHub().cargaMetaDadosProjects();
			break;
		case 2:
			// Extrai refatorações
			try {
				CrudProjectDaoGit crud = new CrudProjectDaoGit();
				List<ProjectGit> projectGit = crud.getProjects();

				for (ProjectGit proj : projectGit) {

					boolean online = true;

					do {
						online = new InternetReachable().isInternetReachable();
						if (online) {
							System.out.println("Projeto em processamento: "
									+ proj.getName());
							RunDiff r = new RunDiff();
							r.getRevisionsDiff(proj);
							del.deleteDir(new File(Constantes.dirClone
									+ proj.getName()));
						} else {
							System.out.println("Falha ao clonar o projeto " + proj.getName());
							del.deleteDir(new File(Constantes.dirClone
									+ proj.getName()));
							System.out.println("Deletado projeto " + proj.getName());
							Thread.sleep(5000);
						}
					} while (!online);

				}
			} catch (UnknownHostException ex) {
				System.out
						.println("Falha conexão internet: " + ex.getMessage());
			}
			break;
		/*
		 * case 3: try {
		 * 
		 * CrudProjectDaoGit crud = new CrudProjectDaoGit(); List<ProjectGit>
		 * projectGit = crud.getProjects();
		 * 
		 * for (ProjectGit proj : projectGit) {
		 * System.out.println("Projeto em processamento: " + proj.getName());
		 * RunDiff r = new RunDiff(); r.getRevisionsDiff(proj); proj =
		 * (ProjectGit) crud.getProjectSelected(proj.getId());
		 * //proj.setFinalizado(Boolean.TRUE); crud.mergeObject(proj); //
		 * del.deleteDir(new File(Constantes.dirClone + // proj.getName())); } }
		 * catch (java.net.UnknownHostException ex) { System.out
		 * .println("Falha conexão internet: " + ex.getMessage()); }
		 * 
		 * break;
		 */
		}

	}

}
