package org.rebecalang.rmc.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ArrayType;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FormalParameterDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.GenericTypeInstance;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.compiler.utils.TypesUtilities;

public class TypesAnalysisUtilities {

	static TypesAnalysisUtilities object = new TypesAnalysisUtilities();

	TypesAnalysisUtilities() {
	}

	public static TypesAnalysisUtilities getInstance() {
		return object;
	}
	
	public int getTypeSize(Type type) throws TypeAnalysisException {
		int size = 1;
		if (type instanceof ArrayType) {
			for (Integer dimention : ((ArrayType) type).getDimensions())
				size *= dimention;
			type = ((ArrayType) type).getOrdinaryPrimitiveType();
		}
		if (type == TypesUtilities.BOOLEAN_TYPE)
			size *= 1;
		else if (type == TypesUtilities.BYTE_TYPE)
			size *= 1;
		else if (type == TypesUtilities.SHORT_TYPE)
			size *= 2;
		else if (type == TypesUtilities.INT_TYPE)
			size *= 4;
		else if (type == TypesUtilities.FLOAT_TYPE)
			size *= 4;
		else if (type == TypesUtilities.DOUBLE_TYPE)
			size *= 8;
		//I have to think about dynamic pointer size in OSx and 32bit systems
		else if (TypesUtilities.getInstance().canTypeUpCastTo(type, TypesUtilities.REACTIVE_CLASS_TYPE))
			size *= 8;
		else 
			throw new TypeAnalysisException("Unknown type " + TypesUtilities.getTypeName(type));
		return size;
	}
	
	public String getTypeSizeLabel(Type type) throws TypeAnalysisException {
		int size = 1;
		if (type instanceof ArrayType) {
			for (Integer dimention : ((ArrayType) type).getDimensions())
				size *= dimention;
			type = ((ArrayType) type).getOrdinaryPrimitiveType();
		}
		String label = "(" + size + " * ";
		if (type == TypesUtilities.BOOLEAN_TYPE)
			label += "BOOLEAN_SIZE";
		else if (type == TypesUtilities.BYTE_TYPE)
			label += "BYTE_SIZE";
		else if (type == TypesUtilities.SHORT_TYPE)
			label += "SHORT_SIZE";
		else if (type == TypesUtilities.INT_TYPE)
			label += "INT_SIZE";
		else if (type == TypesUtilities.FLOAT_TYPE)
			label += "FLOAT_SIZE";
		else if (type == TypesUtilities.DOUBLE_TYPE)
			label += "DOUBLE_SIZE";
		else if (TypesUtilities.getInstance().canTypeUpCastTo(type, TypesUtilities.REACTIVE_CLASS_TYPE))
			label += "REACTIVE_CLASS_SIZE";
		else 
			throw new TypeAnalysisException("Unknown type " + TypesUtilities.getTypeName(type));
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
			if (type == TypesUtilities.BOOLEAN_TYPE)
				retValue = "(" + varName + "? \"true\" : \"false\")";
			else 
				if (TypesUtilities.getInstance().canTypeCastTo(type, TypesUtilities.INT_TYPE)) 
					retValue = "((int)" + varName +")";
				else if (TypesUtilities.getInstance().canTypeCastTo(type, TypesUtilities.REACTIVE_CLASS_TYPE)) 
					retValue = "(" + varName + " == NULL ? \"NULL\" : " + varName +"->getName())";
				else
					retValue = "\"unknown type " + TypesUtilities.getTypeName(type) + "\"";
		}
		return retValue;
	}
	
	public static Type getBaseType(Type inputType) {
		if (inputType instanceof ArrayType)
			return ((ArrayType) inputType).getOrdinaryPrimitiveType();
		return inputType;
	}

	public static String getTypeName(Type type) {
		
		if (type == TypesUtilities.REACTIVE_CLASS_TYPE)
			return "AbstractActor*";	
		else if (TypesUtilities.getInstance().canTypeUpCastTo(type, TypesUtilities.REACTIVE_CLASS_TYPE))
			return TypesUtilities.getTypeName(type)+ "Actor*";
		return TypesUtilities.getTypeName(type);
	}

	public static String getCPPTypeName(Type type) {
		String typeName = "";
		Type baseType = (type instanceof ArrayType) ? ((ArrayType)type).getOrdinaryPrimitiveType() : type;
		if (baseType == TypesUtilities.REACTIVE_CLASS_TYPE)
			typeName = "AbstractActor*";	
		else if (TypesUtilities.getInstance().canTypeUpCastTo(baseType, TypesUtilities.REACTIVE_CLASS_TYPE))
			typeName = TypesUtilities.getTypeName(baseType) + "Actor*";
		else if (baseType instanceof GenericTypeInstance) {
			GenericTypeInstance git = (GenericTypeInstance) baseType;
			typeName = git.getBase().getName() +"<";
			for (Type t : git.getParameters())
				typeName += getCPPTypeName(t) +", ";
			typeName = typeName.substring(0,typeName.length()-2)+ ">";
		}
			
		else
			typeName = TypesUtilities.getTypeName(baseType);
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