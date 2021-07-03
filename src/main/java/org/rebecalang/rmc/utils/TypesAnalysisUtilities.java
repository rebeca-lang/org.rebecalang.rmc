package org.rebecalang.rmc.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ArrayType;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FormalParameterDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.GenericTypeInstance;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;

public class TypesAnalysisUtilities {

	static TypesAnalysisUtilities object = new TypesAnalysisUtilities();

	TypesAnalysisUtilities() {
	}

	public static TypesAnalysisUtilities getInstance() {
		return object;
	}
	
	@Deprecated //Think about Timer!
	public int getTypeSize(Type type) throws TypeAnalysisException {
		int size = 1;
		if (type instanceof ArrayType) {
			for (Integer dimention : ((ArrayType) type).getDimensions())
				size *= dimention;
			type = ((ArrayType) type).getOrdinaryPrimitiveType();
		}
//		Type TIMER_TYPE = null;
//		try {
//			TIMER_TYPE  = TypesUtilities.getInstance().getType("Timer");
//		} catch (CodeCompilationException e) {
//			e.printStackTrace();
//		}
		if (type == CoreRebecaTypeSystem.BOOLEAN_TYPE)
			size *= 1;
		else if (type == CoreRebecaTypeSystem.BYTE_TYPE)
			size *= 1;
		else if (type == CoreRebecaTypeSystem.SHORT_TYPE)
			size *= 2;
		else if (type == CoreRebecaTypeSystem.INT_TYPE)
			size *= 4;
		else if (type == CoreRebecaTypeSystem.FLOAT_TYPE)
			size *= 4;
		else if (type == CoreRebecaTypeSystem.DOUBLE_TYPE)
			size *= 8;
		else if (type.getTypeName().equals("Timer"))
			size *= 4;
		//I have to think about dynamic pointer size in OSx and 32bit systems
		else if (type.canTypeUpCastTo(CoreRebecaTypeSystem.REACTIVE_CLASS_TYPE))
			size *= 8;
		else 
			throw new TypeAnalysisException("Unknown type " + type.getTypeName());
		return size;
	}
	
	@Deprecated //Think about Timer!
	public String getTypeSizeLabel(Type type) throws TypeAnalysisException {
		int size = 1;
		if (type instanceof ArrayType) {
			for (Integer dimention : ((ArrayType) type).getDimensions())
				size *= dimention;
			type = ((ArrayType) type).getOrdinaryPrimitiveType();
		}
		String label = "(" + size + " * ";
		if (type == CoreRebecaTypeSystem.BOOLEAN_TYPE)
			label += "BOOLEAN_SIZE";
		else if (type == CoreRebecaTypeSystem.BYTE_TYPE)
			label += "BYTE_SIZE";
		else if (type == CoreRebecaTypeSystem.SHORT_TYPE)
			label += "SHORT_SIZE";
		else if (type == CoreRebecaTypeSystem.INT_TYPE)
			label += "INT_SIZE";
		else if (type == CoreRebecaTypeSystem.FLOAT_TYPE)
			label += "FLOAT_SIZE";
		else if (type == CoreRebecaTypeSystem.DOUBLE_TYPE)
			label += "DOUBLE_SIZE";
		else if (type.getTypeName().equals("Timer"))
			label += "INT_SIZE";
		else if (type.canTypeUpCastTo(CoreRebecaTypeSystem.REACTIVE_CLASS_TYPE))
			label += "REACTIVE_CLASS_SIZE";
		else 
			throw new TypeAnalysisException("Unknown type " + type.getTypeName());
		return label + ")";
	}

	public static List<FieldDeclaration> convertToFieldDeclaration(List<FormalParameterDeclaration> formalParameters) {
		List<FieldDeclaration> fields = new LinkedList<FieldDeclaration>();
		for (FormalParameterDeclaration fpd : formalParameters) {
			FieldDeclaration fd = new FieldDeclaration();
			VariableDeclarator vd = new VariableDeclarator();
			vd.setVariableName(fpd.getName());
			fd.setType(fpd.getType());
			fd.getVariableDeclarators().add(vd);
			fields.add(fd);
		}
		return fields;
	}

	public static List<FormalParameterDeclaration> convertToFormalParameterDeclaration(List<FieldDeclaration> fieldDeclarations) {
		List<FormalParameterDeclaration> fields = new LinkedList<FormalParameterDeclaration>();
		if (fieldDeclarations == null)
			return fields;
		for (FieldDeclaration fd : fieldDeclarations) {
			Type type = fd.getType();
			for (VariableDeclarator vd : fd.getVariableDeclarators()) {
				FormalParameterDeclaration fpd = new FormalParameterDeclaration();
				fpd.setName(vd.getVariableName());
				fpd.setType(type);
				fields.add(fpd);
			}
		}
		return fields;
	}

	@Deprecated //Think about Timer!
	public static String getVaribleValue(String varName, Type type) {
		String retValue = "";
		if (type instanceof ArrayType) {
			retValue = "\"[\" << ";
			ArrayType aType = (ArrayType) type;
			int dimention = aType.getDimensions().get(0);
			Type newType = null;
			if (aType.getDimensions().size() == 1) {
				newType = aType.getOrdinaryPrimitiveType();
			} else {
				newType = new ArrayType();
				((ArrayType)newType).setOrdinaryPrimitiveType(aType.getOrdinaryPrimitiveType());
				for (int cnt = 1; cnt < aType.getDimensions().size(); cnt++)
					((ArrayType)newType).getDimensions().add(aType.getDimensions().get(cnt));
			}
			
			for (int cnt = 0; cnt < dimention; cnt++) {
				retValue += getVaribleValue(varName+ "[" + cnt + "]", newType) + " << \", \" << ";
			}
			retValue += "\"]\"";
		} else {
			if (type == CoreRebecaTypeSystem.BOOLEAN_TYPE)
				retValue = "(" + varName + "? \"true\" : \"false\")";
			else 
				if (type.canTypeUpCastTo(CoreRebecaTypeSystem.INT_TYPE)) 
					retValue = "((int)" + varName +")";
				else if (type.getTypeName().equals("Timer"))
					retValue = "((" + varName + " == -1) ? string(\"STOP\") : " + "std::to_string(" + varName + "))";
				else if (type.canTypeUpCastTo(CoreRebecaTypeSystem.REACTIVE_CLASS_TYPE)) 
					retValue = "(" + varName + " == NULL ? \"NULL\" : " + varName +"->getName())";
				else
					retValue = "\"unknown type " + type.getTypeName() + "\"";
		}
		return retValue;
	}
	
	public static Type getBaseType(Type inputType) {
		if (inputType instanceof ArrayType)
			return ((ArrayType) inputType).getOrdinaryPrimitiveType();
		return inputType;
	}

	public static String getTypeName(Type type) {
		
		if (type == CoreRebecaTypeSystem.REACTIVE_CLASS_TYPE)
			return "AbstractActor*";	
		else if (type.canTypeUpCastTo(CoreRebecaTypeSystem.REACTIVE_CLASS_TYPE))
			return type.getTypeName()+ "Actor*";
		return type.getTypeName();
	}

	public static String getCPPTypeName(Type type) {
		String typeName = "";
		Type baseType = (type instanceof ArrayType) ? ((ArrayType)type).getOrdinaryPrimitiveType() : type;
		if (baseType == CoreRebecaTypeSystem.REACTIVE_CLASS_TYPE)
			typeName = "AbstractActor*";	
		else if (baseType.canTypeUpCastTo(CoreRebecaTypeSystem.REACTIVE_CLASS_TYPE))
			typeName = baseType.getTypeName() + "Actor*";
		else if (baseType instanceof GenericTypeInstance) {
			GenericTypeInstance git = (GenericTypeInstance) baseType;
			typeName = git.getBase().getName() +"<";
			for (Type t : git.getParameters())
				typeName += getCPPTypeName(t) +", ";
			typeName = typeName.substring(0,typeName.length()-2)+ ">";
		}
			
		else
			typeName = baseType.getTypeName();
		if (type instanceof ArrayType) {
			List<Integer> dimentions = ((ArrayType)type).getDimensions();
			ListIterator<Integer> dimentionsIterator = dimentions.listIterator(dimentions.size());
			while (dimentionsIterator.hasPrevious()) {
				typeName = "std::array<" + typeName + ", " + dimentionsIterator.previous() + ">";
			}
		}
		return typeName;
	}
}