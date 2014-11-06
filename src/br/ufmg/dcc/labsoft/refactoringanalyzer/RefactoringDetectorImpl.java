package br.ufmg.dcc.labsoft.refactoringanalyzer;

import gr.uom.java.xmi.ASTReader;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.Refactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import br.ufmg.dcc.labsoft.refactoringanalyzer.dao.CrudProjectDaoGit;
import br.ufmg.dcc.labsoft.refactoringanalyzer.dao.ProjectGit;
import br.ufmg.dcc.labsoft.refactoringanalyzer.dao.RefactoringGit;
import br.ufmg.dcc.labsoft.refactoringanalyzer.dao.RevisionGit;
import br.ufmg.dcc.labsoft.refactoringanalyzer.util.Constantes;
import br.ufmg.dcc.labsoft.refactoringanalyzer.util.GenerateHash;

public class RefactoringDetectorImpl implements RefactoringDetector {

	private final String projectFolder;
	private final RefactoringHandler handler;

	private RevCommit currentCommit;
	private RevCommit parentCommit;
	private UMLModel currentUMLModel;
	private UMLModel parentUMLModel;

	private Calendar startTime;
	private Calendar endTime;

	private int numberOfRevisionsProcessad = 0;
	private int numberOfMergeRevisions = 0;
	private int numberOfAllRevisions = 0;

	private ProjectGit projectGit;
	private CrudProjectDaoGit crud = new CrudProjectDaoGit();

	private List<Refactoring> refactorings = new ArrayList<Refactoring>();

	public RefactoringDetectorImpl(ProjectGit projectGit,
			RefactoringHandler handler) {
		this.projectFolder = Constantes.dirClone + projectGit.getName();
		this.projectGit = projectGit;
		this.handler = handler;
	}

	@Override
	public void detectAll() {
		startTime = Calendar.getInstance();

		try {
			RepositoryBuilder builder = new RepositoryBuilder();
			org.eclipse.jgit.lib.Repository repository = builder
					.setGitDir(
							new File(this.projectFolder + File.separator
									+ ".git")).readEnvironment() .findGitDir().build();

			
			
			// Inicializa repositorio
			Git git = new Git(repository);
			checkoutHead(git);
			
			
			RevWalk walk = new RevWalk(repository);
			Iterable<RevCommit> logs = git.log().call();
			Iterator<RevCommit> i = logs.iterator();
			
			String revProcessada = null;
			
			try{
				//para caso o projeto já ter sido processado algumas versões, mas não terminou de todas as revisões
			if (!projectGit.getRevisionGitList().isEmpty())
				
				revProcessada = projectGit.getRevisionGitList().get(projectGit.getRevisionGitList().size() - 1).getIdCommit();
			}catch(ArrayIndexOutOfBoundsException ex){
				ex.getMessage();
			}

			// Itera em todas as revisoes do projeto
			while (i.hasNext()) {

				currentCommit = walk.parseCommit(i.next());
				//faz o iterador ir até a revisão que já foi processada
				if(revProcessada!=null){
					if (revProcessada.equals(currentCommit.getId().name()))
						revProcessada = null;
					numberOfAllRevisions++;
					continue;
				}

				

					if (currentCommit.getParentCount() == 1) {

						// Ganho de performance - Aproveita a UML Model que ja
						// se
						// encontra em memorioa da comparacao anterior
						if (parentCommit != null
								&& currentCommit.getId().equals(
										parentCommit.getId())) {
							currentUMLModel = parentUMLModel;
						} else {
							// Faz checkout e gera UML model da revisao current
							checkoutCommand(git, currentCommit);
							currentUMLModel = new ASTReader(new File(
									this.projectFolder)).getUmlModel();
						}

						// Recupera o parent commit
						parentCommit = walk.parseCommit(currentCommit
								.getParent(0));

						// Faz checkout e gera UML model da revisao parent
						checkoutCommand(git, parentCommit);
						parentUMLModel = new ASTReader(new File(
								this.projectFolder)).getUmlModel();

						// Diff entre currentModel e parentModel
						UMLModelDiff modelDiff = parentUMLModel
								.diff(currentUMLModel);
						List<Refactoring> refactoringsAtRevision = modelDiff
								.getRefactorings();
						refactorings.addAll(refactoringsAtRevision);

						RevisionGit revisionCurrentCommit = loadDateRevision(
								currentCommit, parentCommit, projectGit);
						revisionCurrentCommit.setProjectGit(projectGit);
						// projectGit.getRevisionGitList().add(revisionCurrentCommit);
						Set<RefactoringGit> listRef = new HashSet<RefactoringGit>();

						for (Refactoring ref : refactoringsAtRevision) {
							this.handler.handleRefactoring(new Revision(
									currentCommit.getId().getName()), ref);
							RefactoringGit refact = new RefactoringGit();
							refact.setOperacaoCompleta(ref.toString());
							refact.setTipoOperacao(ref.getName());
							refact.setHashOperacao(GenerateHash
									.StringHashOperacaoCompleta(ref.toString()));
							refact.setRevisionOrderV0(parentCommit.getId()
									.name());
							refact.setRevisionOrderV1(currentCommit.getId()
									.name());
							refact.setRevisiongit(revisionCurrentCommit);

							if (checkDuplicateRefectoring(refact)) {
								listRef.add(refact);
							} else {
								System.out.println("");
								System.out
										.println("-------------------------------------------------------------------------------");
								System.out.println("Refactoring de hash: "
										+ refact.getHashOperacao()
										+ " já inserido no banco.");
								System.out
										.println("-------------------------------------------------------------------------------");
								System.out.println("");
							}

							System.out.println(ref.toString());

						}
						if (!refactoringsAtRevision.isEmpty()) {
							System.out.println("tamanho lista "
									+ listRef.size());
							if (listRef.size() != 0) {
								revisionCurrentCommit.setRefactorygit(listRef);
							}
						}
						System.out.println(revisionCurrentCommit.getIdCommit());
						revisionCurrentCommit.setComparacao(Boolean.TRUE);
						crud.mergeObject(revisionCurrentCommit);
						for (Refactoring ref : refactoringsAtRevision) {
							System.out.println(" " + ref.getName());
						}

						numberOfRevisionsProcessad++;
					} else {
						numberOfMergeRevisions++;
					}
					
					numberOfAllRevisions++;

				

				System.out
						.println("|-------------------------------------------------|");
				System.out.println("    Revisoes Verificadas: "
						+ numberOfRevisionsProcessad);
				System.out.println("    Revisoes Ignoradas (Merge): "
						+ numberOfMergeRevisions);
				System.out.println("    Revisoes total): "
						+ numberOfAllRevisions);
				System.out.println("    Refactorings: " + refactorings.size());
			}
			builder = null;
			repository.close();
			git= null;

		} catch (Exception e) {
			e.printStackTrace();
		}

		endTime = Calendar.getInstance();

		System.out
				.println("|-------------------------------------------------|");
		System.out.println("Inicio do Processo:  "
				+ startTime.get(Calendar.HOUR) + ":"
				+ startTime.get(Calendar.MINUTE));
		System.out.println("Fim do Processo:  " + endTime.get(Calendar.HOUR)
				+ ":" + endTime.get(Calendar.MINUTE));
		
		projectGit.setCountCommits(projectGit.getCountCommits() + numberOfAllRevisions);
		projectGit.setCountCommitsNotParents(projectGit.getCountCommitsNotParents() + numberOfRevisionsProcessad);
		projectGit.setFinalizado(Boolean.TRUE);
		crud.mergeObject(projectGit);
		
		
	}

	private void checkoutCommand(Git git, RevCommit commit) throws Exception {
		CheckoutCommand checkout = git.checkout().setStartPoint(commit)
				.setName(commit.getId().getName());
		checkout.call();
	}

	private void checkoutHead(Git git) throws Exception {
		CheckoutCommand checkout = git.checkout().setStartPoint(Constants.HEAD)
				.setName(Constants.MASTER);
		checkout.call();
	}

	private RevisionGit loadDateRevision(RevCommit currentCommit,
			RevCommit parentCommit, ProjectGit projectGit) {
		// dados da revisao
		RevisionGit revision = new RevisionGit();

		revision.setProjectGit(projectGit);
		revision.setIdCommit(currentCommit.getId().getName());
		revision.setAuthorName(currentCommit.getAuthorIdent().getName());
		revision.setAuthorIdent(currentCommit.getAuthorIdent().getName());
		revision.setEncoding(currentCommit.getEncoding().name());
		revision.setComparacao(Boolean.FALSE);
		revision.setIdCommitParent(parentCommit.getId().getName());

		if (currentCommit.getShortMessage().length() >= 4999) {
			revision.setShortMessage(currentCommit.getShortMessage().substring(
					0, 4999));
		} else {
			revision.setShortMessage(currentCommit.getShortMessage());
		}

		if (currentCommit.getFullMessage().length() >= 19999) {
			revision.setFullMessage(currentCommit.getFullMessage().substring(0,
					19999));
		} else {
			revision.setFullMessage(currentCommit.getFullMessage());
		}
		revision.setCommitTime(new java.util.Date((long) currentCommit
				.getCommitTime() * 1000));
		revision.setFinalizado(Boolean.TRUE);

		return revision;
	}

	private Boolean checkDuplicateRefectoring(RefactoringGit rf) {
		
		if(rf.getHashOperacao()!=null){
		
			List<RefactoringGit> rfDuplicado = crud.findRefactoringDuplicado(rf
					.getHashOperacao());
			if (rfDuplicado.isEmpty() || rfDuplicado == null) {
				return Boolean.TRUE;
			 }
		}
		
		 return Boolean.FALSE;

	}

}
