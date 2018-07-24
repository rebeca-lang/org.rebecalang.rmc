package org.rebecalang.rmc.utils;

import java.util.List;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Annotation;

public class AnnotationsUtility {
	
	private static AnnotationsUtility instance = new AnnotationsUtility();
	
	public static boolean contains(List<Annotation> annotations, String label) {
		for (Annotation annotation : annotations) 
			if (annotation.getIdentifier().equals(label))
				return true;
		return false;
	}

	public static AnnotationsUtility getInstance() {
		return instance;
	}
}
