package gr.uom.java.xmi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.ufmg.dcc.labsoft.util.serialization.JsonIgnore;

public class UMLAnonymousClass implements Comparable<UMLAnonymousClass>, Serializable {
	private static final long serialVersionUID = -9069124689708967242L;
	private String packageName;
    private String name;
    private String sourceFile;
    
    @JsonIgnore
    private List<UMLOperation> operations;
    
    @JsonIgnore
    private List<UMLAttribute> attributes;
    
    public UMLAnonymousClass(String packageName, String name) {
    	this(packageName, name, packageName.replace('.', '/') + '/' + name + ".java");
    }
    
    public UMLAnonymousClass(String packageName, String name, String sourceFile) {
    	this.packageName = packageName;
        this.name = name;
        this.operations = new ArrayList<UMLOperation>();
        this.attributes = new ArrayList<UMLAttribute>();
        this.sourceFile = sourceFile;
    }

    public String getName() {
    	if(packageName.equals(""))
    		return name;
    	else
    		return packageName + "." + name;
    }

	public void addOperation(UMLOperation operation) {
    	this.operations.add(operation);
    }

    public void addAttribute(UMLAttribute attribute) {
    	this.attributes.add(attribute);
    }

    public List<UMLOperation> getOperations() {
		return operations;
	}

	public List<UMLAttribute> getAttributes() {
		return attributes;
	}

    public boolean equals(Object o) {
    	if(this == o) {
    		return true;
    	}
    	
    	if(o instanceof UMLAnonymousClass) {
    		UMLAnonymousClass umlClass = (UMLAnonymousClass)o;
    		return this.packageName.equals(umlClass.packageName) && this.attributes.equals(umlClass.attributes) &&
    				this.operations.equals(umlClass.operations) && this.sourceFile.equals(umlClass.sourceFile);
    	}
    	return false;
    }

    public String toString() {
    	return getName();
    }

	public int compareTo(UMLAnonymousClass umlClass) {
		return this.toString().compareTo(umlClass.toString());
	}

	public String getPackageName() {
		return packageName;
	}

	public String getSourceFile() {
		return sourceFile;
	}
	
	
	
}
