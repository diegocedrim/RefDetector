package ca.ualberta.cs.data;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class ChangeSet implements Serializable {
	
	private static final long serialVersionUID = 7363713908739003710L;

	private Set<FileChange> fileChangeSet;

	public ChangeSet() {
		this.fileChangeSet = new LinkedHashSet<FileChange>(); 
	}
	 
	public void addFileChange(FileChange fileChange) {
		fileChangeSet.add(fileChange);
	}

	public Set<FileChange> getFileChangeSet() {
		return fileChangeSet;
	}

}
