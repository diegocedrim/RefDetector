package gr.uom.java.xmi.diff;

import java.util.List;

import br.ufmg.dcc.labsoft.util.serialization.GsonFactory;

import com.google.gson.Gson;

public class CommitRefactorings {
	
	private String gitRepositoryUrl;
	
	private String commitHashFrom;
	
	private String commitHashTo;
	
	private List<Refactoring> refactorings;
	
	public CommitRefactorings(List<Refactoring> refactorings) {
		super();
		this.refactorings = refactorings;
	}
	
	public CommitRefactorings() {

	}

	public List<Refactoring> getRefactorings() {
		return refactorings;
	}

	public void setRefactorings(List<Refactoring> refactorings) {
		this.refactorings = refactorings;
	}

	public String getGitRepositoryUrl() {
		return gitRepositoryUrl;
	}

	public void setGitRepositoryUrl(String gitRepositoryUrl) {
		this.gitRepositoryUrl = gitRepositoryUrl;
	}

	public String getCommitHashFrom() {
		return commitHashFrom;
	}

	public void setCommitHashFrom(String commitHashFrom) {
		this.commitHashFrom = commitHashFrom;
	}

	public String getCommitHashTo() {
		return commitHashTo;
	}

	public void setCommitHashTo(String commitHashTo) {
		this.commitHashTo = commitHashTo;
	}
	
	public String toJsonString() {
		Gson gson = GsonFactory.getInstance().create();
		return gson.toJson(this);
	}
	
}
