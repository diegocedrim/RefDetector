package gr.uom.java.xmi;

import gr.uom.java.xmi.decomposition.OperationBody;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class ASTReader {
	private UMLModel umlModel;
	//private UMLModel bytecodeModel;

	public ASTReader(File rootFile) {
		this.umlModel = new UMLModel();
		//this.bytecodeModel = new BytecodeReader(rootFile).getUmlModel();
		recurse(rootFile);
	}

	public UMLModel getUmlModel() {
		return umlModel;
	}

	private void recurse(File rootFile) {
		if(rootFile.isDirectory()) {
			File[] files = rootFile.listFiles();
			for(File file : files) {
				if(file.isDirectory())
					recurse(file);
				else {
					String fileName = file.getName();
					if(fileName.contains(".")) {
						String extension = fileName.substring(fileName.lastIndexOf("."));
						if(extension.equalsIgnoreCase(".java")) {
							parseAST(file);
						}
					}
				}
			}
		}
		else {
			String fileName = rootFile.getName();
			if(fileName.contains(".")) {
				String extension = fileName.substring(fileName.lastIndexOf("."));
				if(extension.equalsIgnoreCase(".java")) {
					parseAST(rootFile);
				}
			}
		}
	}

	private void parseAST(File javaFile) {
		try {
			String source = FileUtils.readFileToString(javaFile);
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setSource(source.toCharArray());
			//parser.setResolveBindings(true);
			CompilationUnit compilationUnit = (CompilationUnit)parser.createAST(null);
			
			PackageDeclaration packageDeclaration = compilationUnit.getPackage();
			String packageName = null;
			if(packageDeclaration != null)
				packageName = packageDeclaration.getName().getFullyQualifiedName();
			else
				packageName = "";
			List<AbstractTypeDeclaration> topLevelTypeDeclarations = compilationUnit.types();
	        for(AbstractTypeDeclaration abstractTypeDeclaration : topLevelTypeDeclarations) {
	        	if(abstractTypeDeclaration instanceof TypeDeclaration) {
	        		TypeDeclaration topLevelTypeDeclaration = (TypeDeclaration)abstractTypeDeclaration;
	        		processTypeDeclaration(topLevelTypeDeclaration, packageName);
	        	}
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processTypeDeclaration(TypeDeclaration typeDeclaration, String packageName) {
		String className = typeDeclaration.getName().getFullyQualifiedName();
		UMLClass umlClass = new UMLClass(packageName, className, null, typeDeclaration.isPackageMemberTypeDeclaration());
		//UMLClass bytecodeClass = bytecodeModel.getClass(umlClass.getName());
		
		if(typeDeclaration.isInterface()) {
			umlClass.setInterface(true);
    	}
    	
    	int modifiers = typeDeclaration.getModifiers();
    	if((modifiers & Modifier.ABSTRACT) != 0)
    		umlClass.setAbstract(true);
    	
    	if((modifiers & Modifier.PUBLIC) != 0)
    		umlClass.setVisibility("public");
    	else if((modifiers & Modifier.PROTECTED) != 0)
    		umlClass.setVisibility("protected");
    	else if((modifiers & Modifier.PRIVATE) != 0)
    		umlClass.setVisibility("private");
    	else
    		umlClass.setVisibility("package");
		
    	Type superclassType = typeDeclaration.getSuperclassType();
    	if(superclassType != null) {
    		UMLType umlType = UMLType.extractTypeObject(superclassType.toString());
    		UMLGeneralization umlGeneralization = new UMLGeneralization(umlClass.getName(), umlType.getClassType());
    		umlClass.setSuperclass(umlType);
    		/*UMLGeneralization bytecodeGeneralization = bytecodeModel.matchGeneralization(umlGeneralization);
    		if(bytecodeGeneralization != null) {
    			umlGeneralization.setParent(bytecodeGeneralization.getParent());
    		}
    		if(bytecodeClass != null) {
    			umlClass.setSuperclass(bytecodeClass.getSuperclass());
    		}*/
    		umlModel.addGeneralization(umlGeneralization);
    	}
    	
    	List<Type> superInterfaceTypes = typeDeclaration.superInterfaceTypes();
    	for(Type interfaceType : superInterfaceTypes) {
    		UMLRealization umlRealization = new UMLRealization(umlClass.getName(), interfaceType.toString());
    		/*UMLRealization bytecodeRealization = bytecodeModel.matchRealization(umlRealization);
    		if(bytecodeRealization != null) {
    			umlRealization.setSupplier(bytecodeRealization.getSupplier());
    		}*/
    		umlModel.addRealization(umlRealization);
    	}
    	
    	FieldDeclaration[] fieldDeclarations = typeDeclaration.getFields();
    	for(FieldDeclaration fieldDeclaration : fieldDeclarations) {
    		List<UMLAttribute> attributes = processFieldDeclaration(fieldDeclaration/*, bytecodeClass*/);
    		for(UMLAttribute attribute : attributes) {
    			attribute.setClassName(umlClass.getName());
    			umlClass.addAttribute(attribute);
    		}
    	}
    	
    	MethodDeclaration[] methodDeclarations = typeDeclaration.getMethods();
    	for(MethodDeclaration methodDeclaration : methodDeclarations) {
    		UMLOperation operation = processMethodDeclaration(methodDeclaration/*, bytecodeClass*/);
    		operation.setClassName(umlClass.getName());
    		umlClass.addOperation(operation);
    	}
    	
    	AnonymousClassDeclarationVisitor visitor = new AnonymousClassDeclarationVisitor();
    	typeDeclaration.accept(visitor);
    	Set<AnonymousClassDeclaration> anonymousClassDeclarations = visitor.getAnonymousClassDeclarations();
    	
    	DefaultMutableTreeNode root = new DefaultMutableTreeNode();
    	for(AnonymousClassDeclaration anonymous : anonymousClassDeclarations) {
    		insertNode(anonymous, root);
    	}
    	
    	Enumeration<DefaultMutableTreeNode> enumeration = root.preorderEnumeration();
    	while(enumeration.hasMoreElements()) {
    		DefaultMutableTreeNode node = enumeration.nextElement();
    		if(node.getUserObject() != null) {
    			AnonymousClassDeclaration anonymous = (AnonymousClassDeclaration)node.getUserObject();
    			String anonymousName = getAnonymousName(node);
    			processAnonymousClassDeclaration(anonymous, packageName + "." + className, anonymousName);
    		}
    	}
    	
    	umlModel.addClass(umlClass);
		
		TypeDeclaration[] types = typeDeclaration.getTypes();
		for(TypeDeclaration type : types) {
			processTypeDeclaration(type, packageName + "." + className);
		}
	}

	private UMLOperation processMethodDeclaration(MethodDeclaration methodDeclaration/*, UMLClass bytecodeClass*/) {
		String methodName = methodDeclaration.getName().getFullyQualifiedName();
		UMLOperation umlOperation = new UMLOperation(methodName, null);
		//umlOperation.setClassName(umlClass.getName());
		if(methodDeclaration.isConstructor())
			umlOperation.setConstructor(true);
		
		int methodModifiers = methodDeclaration.getModifiers();
		if((methodModifiers & Modifier.PUBLIC) != 0)
			umlOperation.setVisibility("public");
		else if((methodModifiers & Modifier.PROTECTED) != 0)
			umlOperation.setVisibility("protected");
		else if((methodModifiers & Modifier.PRIVATE) != 0)
			umlOperation.setVisibility("private");
		else
			umlOperation.setVisibility("package");
		
		if((methodModifiers & Modifier.ABSTRACT) != 0)
			umlOperation.setAbstract(true);
		
		if((methodModifiers & Modifier.FINAL) != 0)
			umlOperation.setFinal(true);
		
		if((methodModifiers & Modifier.STATIC) != 0)
			umlOperation.setStatic(true);
		
		Block block = methodDeclaration.getBody();
		if(block != null) {
			OperationBody body = new OperationBody(block);
			umlOperation.setBody(body);
			if(block.statements().size() == 0) {
				umlOperation.setEmptyBody(true);
			}
		}
		else {
			umlOperation.setBody(null);
		}
		
		Type returnType = methodDeclaration.getReturnType2();
		if(returnType != null) {
			UMLType type = UMLType.extractTypeObject(returnType.toString());
			UMLParameter returnParameter = new UMLParameter("return", type, "return");
			umlOperation.addParameter(returnParameter);
		}
		List<SingleVariableDeclaration> parameters = methodDeclaration.parameters();
		for(SingleVariableDeclaration parameter : parameters) {
			Type parameterType = parameter.getType();
			String parameterName = parameter.getName().getFullyQualifiedName();
			UMLType type = UMLType.extractTypeObject(parameterType.toString());
			UMLParameter umlParameter = new UMLParameter(parameterName, type, "in");
			umlOperation.addParameter(umlParameter);
		}
		
		/*if(bytecodeClass != null) {
			UMLOperation bytecodeOperation = bytecodeClass.matchOperation(umlOperation);
			if(bytecodeOperation != null) {
				int i = 0;
				for(UMLParameter bytecodeParameter : bytecodeOperation.getParameters()) {
					umlOperation.getParameters().get(i).setType(bytecodeParameter.getType());
					i++;
				}
				umlOperation.setAccessedMembers(new LinkedHashSet<AccessedMember>(bytecodeOperation.getAccessedMembers()));
			}
		}*/
		
		return umlOperation;
	}

	private List<UMLAttribute> processFieldDeclaration(FieldDeclaration fieldDeclaration/*, UMLClass bytecodeClass*/) {
		List<UMLAttribute> attributes = new ArrayList<UMLAttribute>();
		Type fieldType = fieldDeclaration.getType();
		UMLType type = UMLType.extractTypeObject(fieldType.toString());
		List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
		for(VariableDeclarationFragment fragment : fragments) {
			String fieldName = fragment.getName().getFullyQualifiedName();
			UMLAttribute umlAttribute = new UMLAttribute(fieldName, type);
			//umlAttribute.setClassName(umlClass.getName());
			
			int fieldModifiers = fieldDeclaration.getModifiers();
			if((fieldModifiers & Modifier.PUBLIC) != 0)
				umlAttribute.setVisibility("public");
			else if((fieldModifiers & Modifier.PROTECTED) != 0)
				umlAttribute.setVisibility("protected");
			else if((fieldModifiers & Modifier.PRIVATE) != 0)
				umlAttribute.setVisibility("private");
			else
				umlAttribute.setVisibility("package");
			
			if((fieldModifiers & Modifier.FINAL) != 0)
				umlAttribute.setFinal(true);
			
			if((fieldModifiers & Modifier.STATIC) != 0)
				umlAttribute.setStatic(true);
			
			/*if(bytecodeClass != null) {
				UMLAttribute bytecodeAttribute = bytecodeClass.matchAttribute(umlAttribute);
				if(bytecodeAttribute != null)
					umlAttribute.setType(bytecodeAttribute.getType());
			}*/
			
			attributes.add(umlAttribute);
		}
		return attributes;
	}
	
	private void processAnonymousClassDeclaration(AnonymousClassDeclaration anonymous, String packageName, String className) {
		List<BodyDeclaration> bodyDeclarations = anonymous.bodyDeclarations();
		
		UMLAnonymousClass anonymousClass = new UMLAnonymousClass(packageName, className);
		//UMLClass bytecodeClass = bytecodeModel.getClass(anonymousClass.getName());
		
		for(BodyDeclaration bodyDeclaration : bodyDeclarations) {
			if(bodyDeclaration instanceof FieldDeclaration) {
				FieldDeclaration fieldDeclaration = (FieldDeclaration)bodyDeclaration;
				List<UMLAttribute> attributes = processFieldDeclaration(fieldDeclaration/*, bytecodeClass*/);
	    		for(UMLAttribute attribute : attributes) {
	    			attribute.setClassName(anonymousClass.getName());
	    			anonymousClass.addAttribute(attribute);
	    		}
			}
			else if(bodyDeclaration instanceof MethodDeclaration) {
				MethodDeclaration methodDeclaration = (MethodDeclaration)bodyDeclaration;
				UMLOperation operation = processMethodDeclaration(methodDeclaration/*, bytecodeClass*/);
				operation.setClassName(anonymousClass.getName());
				anonymousClass.addOperation(operation);
			}
		}
		
		umlModel.addAnonymousClass(anonymousClass);
	}
	
	private void insertNode(AnonymousClassDeclaration childAnonymous, DefaultMutableTreeNode root) {
		Enumeration<DefaultMutableTreeNode> enumeration = root.postorderEnumeration();
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childAnonymous);
		
		DefaultMutableTreeNode parentNode = root;
		while(enumeration.hasMoreElements()) {
			DefaultMutableTreeNode currentNode = enumeration.nextElement();
			AnonymousClassDeclaration currentAnonymous = (AnonymousClassDeclaration)currentNode.getUserObject();
			if(currentAnonymous != null && isParent(childAnonymous, currentAnonymous)) {
				parentNode = currentNode;
				break;
			}
		}
		parentNode.add(childNode);
	}
	
	private String getAnonymousName(DefaultMutableTreeNode node) {
		StringBuilder name = new StringBuilder();
		TreeNode[] path = node.getPath();
		for(int i=0; i<path.length; i++) {
			DefaultMutableTreeNode tmp = (DefaultMutableTreeNode)path[i];
			if(tmp.getUserObject() != null) {
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode)tmp.getParent();
				int index = parent.getIndex(tmp);
				name.append(index+1);
				if(i < path.length-1)
					name.append(".");
			}
		}
		return name.toString();
	}
	
	private boolean isParent(ASTNode child, ASTNode parent) {
		ASTNode current = child;
		while(current.getParent() != null) {
			if(current.getParent().equals(parent))
				return true;
			current = current.getParent();
		}
		return false;
	}
}
