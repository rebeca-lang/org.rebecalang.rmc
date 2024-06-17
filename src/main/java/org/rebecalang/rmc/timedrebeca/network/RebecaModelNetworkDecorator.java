package org.rebecalang.rmc.timedrebeca.network;

import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaLabelUtility;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.compiler.modelcompiler.timedrebeca.objectmodel.TimedRebecaParentSuffixPrimary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.rebecalang.compiler.modelcompiler.timedrebeca.network.NetworkNameUtility.*;

public class RebecaModelNetworkDecorator {
    private final RebecaModel rebecaModel;
    private HashMap<String, Type> networkInterfaceTypes = new HashMap<>();
    private HashMap<String, Type> wiredReactiveClassTypes = new HashMap<>();

    public RebecaModelNetworkDecorator(RebecaModel rebecaModel) {
        this.rebecaModel = rebecaModel;
        fillNetworkTypes();
    }

    public RebecaModel decorate() {
        for (ReactiveClassDeclaration rd : rebecaModel.getRebecaCode().getReactiveClassDeclaration()) {
            rd.getKnownRebecs().addAll(generateNetworkKnownRebecsFor(rd));
            changeMsgServersDeclarationBody(rd);
        }
        return rebecaModel;
    }

    private List<FieldDeclaration> generateNetworkKnownRebecsFor(ReactiveClassDeclaration reactiveClassDeclaration) {
        List<FieldDeclaration> networkKnownRebecs = new ArrayList<>();
        for (FieldDeclaration knownRebec : reactiveClassDeclaration.getKnownRebecs()) {
            FieldDeclaration networkRebec = new FieldDeclaration();
            networkRebec.setType(getNetworkInterfaceType(knownRebec.getType().getTypeName()));
            networkRebec.setAccessModifier(knownRebec.getAccessModifier());
            networkRebec.getVariableDeclarators().clear();
            networkRebec.getVariableDeclarators().addAll(generateKnownNetworkRebecVariableDeclaratorsFor(knownRebec));
            networkRebec.setLineNumber(0);
            networkRebec.setCharacter(0);
            networkKnownRebecs.add(networkRebec);
        }
        return networkKnownRebecs;
    }

    private List<VariableDeclarator> generateKnownNetworkRebecVariableDeclaratorsFor(FieldDeclaration knownRebec) {
        List<VariableDeclarator> variableDeclarators = new ArrayList<>();
        for (VariableDeclarator vd : knownRebec.getVariableDeclarators()) {
            VariableDeclarator variableDeclarator = new VariableDeclarator();
            variableDeclarator.setVariableName(generateVariableNameForKnownRebecOfNetworkType(vd.getVariableName()));
            variableDeclarator.setLineNumber(0);
            variableDeclarator.setCharacter(0);
            variableDeclarators.add(variableDeclarator);
        }
        return variableDeclarators;
    }

    public void changeMsgServersDeclarationBody(ReactiveClassDeclaration rd) {
        for (MsgsrvDeclaration msgSrvDecl : rd.getMsgsrvs()) {
            msgSrvDecl.setBlock(changeDirectMsgSrvCallsInBody(msgSrvDecl.getBlock()));
        }
    }

    public BlockStatement changeDirectMsgSrvCallsInBody(Statement inputStatement) {
        BlockStatement modifiedBlock = new BlockStatement();
        List<Statement> modifiedStatements = modifiedBlock.getStatements();

        if (!(inputStatement instanceof BlockStatement)) {
            modifiedStatements.add(inputStatement);
            return modifiedBlock;
        }
        BlockStatement inputBlock = (BlockStatement) inputStatement;
        for (Statement statement : inputBlock.getStatements()) {
            if (isMsgSrvCall(statement)) {
                Statement networkMsgSrvCall = createNetworkMsgSrvCall((DotPrimary) statement);
                Statement condWrapped = createConditionalWrappedNetworkMsgSrvCallStatement(statement, networkMsgSrvCall);
                modifiedStatements.add(condWrapped);
            } else if (statement instanceof ConditionalStatement conditionalStatement) {
                conditionalStatement.setStatement(changeDirectMsgSrvCallsInBody(((ConditionalStatement) statement).getStatement()));
                conditionalStatement.setElseStatement(changeDirectMsgSrvCallsInBody(((ConditionalStatement) statement).getElseStatement()));
                modifiedStatements.add(conditionalStatement);
            } else if (statement instanceof ForStatement forStatement) {
                forStatement.setStatement(changeDirectMsgSrvCallsInBody(forStatement.getStatement()));
                modifiedStatements.add(forStatement);
            } else if (statement instanceof WhileStatement whileStatement) {
                whileStatement.setStatement(changeDirectMsgSrvCallsInBody((((WhileStatement) statement).getStatement())));
                modifiedStatements.add(whileStatement);
            } else if (statement instanceof SwitchStatement switchStatement) {
                for (SwitchStatementGroup switchStatementGroup : switchStatement.getSwitchStatementGroups()) {
                    BlockStatement switchBlockStatement = new BlockStatement();
                    switchBlockStatement.getStatements().addAll(switchStatementGroup.getStatements());
                    switchStatementGroup.getStatements().clear();
                    switchStatementGroup.getStatements().addAll(changeDirectMsgSrvCallsInBody(switchBlockStatement).getStatements());
                }
                modifiedStatements.add(switchStatement);
            } else
                modifiedStatements.add(statement);
        }
        return modifiedBlock;
    }

    private Statement createConditionalWrappedNetworkMsgSrvCallStatement(Statement directMsgSrvCall, Statement networkMsgSrvCall) {
        TermPrimary netMsgSrvCallLeftTerm = (TermPrimary) ((DotPrimary) networkMsgSrvCall).getLeft();
        ConditionalStatement condStatement = new ConditionalStatement();

        InstanceofExpression instanceofExpression = new InstanceofExpression();
        TermPrimary valueTerm = new TermPrimary();
        valueTerm.setLabel(netMsgSrvCallLeftTerm.getLabel());
        valueTerm.setName(netMsgSrvCallLeftTerm.getName());
        valueTerm.setType(netMsgSrvCallLeftTerm.getType());
        instanceofExpression.setValue(valueTerm);
        instanceofExpression.setEvaluationType(getWiredReactiveClassType(((DotPrimary) directMsgSrvCall).getLeft().getType().getTypeName()));
        instanceofExpression.setType(CoreRebecaTypeSystem.BOOLEAN_TYPE);
        instanceofExpression.getType().setTypeSystem(netMsgSrvCallLeftTerm.getType().getTypeSystem());

        BlockStatement ifBlockStatement = new BlockStatement();
        ifBlockStatement.getStatements().add(directMsgSrvCall);

        BlockStatement elseBockStatement = new BlockStatement();
        elseBockStatement.getStatements().add(networkMsgSrvCall);

        condStatement.setCondition(instanceofExpression);
        condStatement.setStatement(ifBlockStatement);
        condStatement.setElseStatement(elseBockStatement);
        return condStatement;
    }

    private DotPrimary createNetworkMsgSrvCall(DotPrimary directMsgSrvCall) {
        DotPrimary netDotPrimary = new DotPrimary();
        netDotPrimary.setType(directMsgSrvCall.getType());
        netDotPrimary.getAnnotations().addAll(directMsgSrvCall.getAnnotations());
        TermPrimary leftTerm = (TermPrimary) directMsgSrvCall.getLeft();
        TermPrimary rightTerm = (TermPrimary) directMsgSrvCall.getRight();
        TermPrimary netLeftTerm = new TermPrimary();
        netLeftTerm.setLabel(leftTerm.getLabel());
        netLeftTerm.setParentSuffixPrimary(leftTerm.getParentSuffixPrimary());
        netLeftTerm.getIndices().addAll(leftTerm.getIndices());
        netLeftTerm.setName(generateVariableNameForKnownRebecOfNetworkType(leftTerm.getName()));
        netLeftTerm.setType(getNetworkInterfaceType(leftTerm.getType().getTypeName()));
        netLeftTerm.getAnnotations().addAll(leftTerm.getAnnotations());

        TermPrimary netRightTerm = new TermPrimary();
        netRightTerm.setLabel(rightTerm.getLabel());
        netRightTerm.setParentSuffixPrimary(createNetworkParentSuffixPrimary(leftTerm, (TimedRebecaParentSuffixPrimary) rightTerm.getParentSuffixPrimary()));
        netRightTerm.getIndices().addAll(rightTerm.getIndices());
        netRightTerm.setName(generateMsgSrvName(leftTerm.getType().getTypeName(), rightTerm.getName()));
        netRightTerm.setType(rightTerm.getType());
        netRightTerm.getAnnotations().addAll(rightTerm.getAnnotations());

        netDotPrimary.setLeft(netLeftTerm);
        netDotPrimary.setRight(netRightTerm);
        return netDotPrimary;
    }

    private TimedRebecaParentSuffixPrimary createNetworkParentSuffixPrimary(TermPrimary leftTerm, TimedRebecaParentSuffixPrimary originalParentSuffixPrimary) {
        TimedRebecaParentSuffixPrimary parentSuffixPrimary = new TimedRebecaParentSuffixPrimary();
        parentSuffixPrimary.setAfterExpression(originalParentSuffixPrimary.getAfterExpression());
        parentSuffixPrimary.setDeadlineExpression(originalParentSuffixPrimary.getDeadlineExpression());
        TermPrimary receiverArg = new TermPrimary();
        receiverArg.setLabel(leftTerm.getLabel());
        receiverArg.setName(leftTerm.getName());
        receiverArg.setType(leftTerm.getType());
        parentSuffixPrimary.getArguments().add(receiverArg);
        parentSuffixPrimary.getArguments().addAll(originalParentSuffixPrimary.getArguments());
        return parentSuffixPrimary;
    }

    private boolean isMsgSrvCall(Statement statement) {
        if (statement instanceof DotPrimary) {
            TermPrimary leftTerm = (TermPrimary) ((DotPrimary) statement).getLeft();
            TermPrimary rightTerm = (TermPrimary) ((DotPrimary) statement).getRight();
            return leftTerm.getType().canTypeCastTo(CoreRebecaTypeSystem.REACTIVE_CLASS_TYPE)
                    && leftTerm.getLabel() == CoreRebecaLabelUtility.KNOWNREBEC_VARIABLE
                    && rightTerm.getType().canTypeCastTo(CoreRebecaTypeSystem.MSGSRV_TYPE)
                    && rightTerm.getLabel() == CoreRebecaLabelUtility.MSGSRV;
        }
        return false;
    }

    private Type getWiredReactiveClassType(String rebecTypeName) {
        return wiredReactiveClassTypes.get(generateWiredReactiveClassName(rebecTypeName));
    }

    public Type getNetworkInterfaceType(String rebecTypeName) {
        return networkInterfaceTypes.get(generateReactiveNetworkInterfaceName(rebecTypeName));
    }

    private void fillNetworkTypes() {
        for (ReactiveClassDeclaration rd : rebecaModel.getRebecaCode().getReactiveClassDeclaration()) {
            findWiredReactiveClassOf(rd.getName()).ifPresent(reactiveClassDeclaration -> {
                Type type = reactiveClassDeclaration.getImplements().get(0);
                networkInterfaceTypes.put(type.getTypeName(), type);
                String wiredReactiveName = reactiveClassDeclaration.getName();
                try {
                    wiredReactiveClassTypes.put(wiredReactiveName, type.getTypeSystem().getType(wiredReactiveName));
                } catch (Exception ignored) {

                }
            });
        }
    }

    public Optional<ReactiveClassDeclaration> findWiredReactiveClassOf(String originalReactiveClassName) {
        return rebecaModel.getRebecaCode().getReactiveClassDeclaration().stream()
                .filter(rd -> rd.getName().equals(generateWiredReactiveClassName(originalReactiveClassName)))
                .findAny();
    }

    private String generateVariableNameForKnownRebecOfNetworkType(String originalVarName) {
        return "_net_" + originalVarName;
    }
}
