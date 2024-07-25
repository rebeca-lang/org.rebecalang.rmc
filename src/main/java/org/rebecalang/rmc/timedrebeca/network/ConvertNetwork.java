package org.rebecalang.rmc.timedrebeca.network;

import java.util.*;

import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaLabelUtility;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.compiler.modelcompiler.timedrebeca.objectmodel.*;

import static org.rebecalang.compiler.modelcompiler.timedrebeca.network.NetworkNameUtility.generateMsgSrvName;
import static org.rebecalang.compiler.modelcompiler.timedrebeca.network.NetworkNameUtility.generateReceiverParameterName;

public class ConvertNetwork {
    private static final String REACTIVE_CLASS_NETWORK_ANNOTATION = "network";
    private static final String REACTIVE_CLASS_WIRED_ANNOTATION = "wired";
    private static final String REACTIVE_CLASS_PRIORITY_ANNOTATION = "priority";
    private static final String DELAY_KEYWORD = "delay";
    private static final String NETWORK_LOSS_VARIABLE_NAME = "loss";
    private static final String REACTIVE_CLASS_SENDER_KEYWORD = "sender";
    private static final String EQUALITY_OPERATOR = "==";
    private static final String AND_OPERATOR = "&&";
    private static final String ASSIGN_OPERATOR = "=";
    private static final String FALSE = "false";
    private static final String TRUE = "true";
    private static final String PUBLIC_ACCESS_LEVEL = "public";


    private TimedRebecaCode timedRebecaCode;
    private RebecaModelNetworkDecorator rebecaModelNetworkDecorator;
    private Map<String, NetworkDeclaration> networkDeclarationMap;
    private Map<String, MainNetworkDefinition> mainNetworkDefinitionMap;
    private Map<String, ReactiveClassDeclaration> reactiveClassDeclarationMap;
    private Map<String, TimedMainRebecDefinition> mainRebecDefinitionMap;
    private Map<String, Set<String>> networkRebecs;

    public ConvertNetwork(TimedRebecaCode timedRebecaCode, RebecaModelNetworkDecorator rebecaModelNetworkDecorator) {
        this.timedRebecaCode = timedRebecaCode;
        this.rebecaModelNetworkDecorator = rebecaModelNetworkDecorator;
        this.networkDeclarationMap = new HashMap<>();
        this.reactiveClassDeclarationMap = new HashMap<>();
        this.mainRebecDefinitionMap  = new HashMap<>();
        this.networkRebecs = new HashMap<>();
        this.mainNetworkDefinitionMap = new HashMap<>();
    }
    public boolean checkNetworkImplementReactiveClass(String networkName, String reactiveClassName, Map<String, Set<String>> networkImplementRebecs) {
        Set<String> networkSet = networkImplementRebecs.get(networkName);
        if (networkSet == null || !networkSet.contains(reactiveClassName)) {
            if (networkSet == null) {
                Set<String> newNetworkSet = new HashSet<>();
                newNetworkSet.add(reactiveClassName);
                networkImplementRebecs.put(networkName, newNetworkSet);
            } else {
                networkSet.add(reactiveClassName);
            }

            return true;
        }

        return false;
    }

    private BinaryExpression getNetworkCondition(
            ReactiveClassDeclaration sender,
            ReactiveClassDeclaration receiver,
            ReactiveClassDeclaration rebec,
            String senderDefinitionName,
            String receiverDefinitionName
    ) {
        BinaryExpression binaryExpression = new BinaryExpression();

        BinaryExpression leftBinaryExpression = new BinaryExpression();

        TermPrimary leftLeftTermPrimary = new TermPrimary();
        leftLeftTermPrimary.setName(receiverDefinitionName);
        leftLeftTermPrimary.setLabel(CoreRebecaLabelUtility.KNOWNREBEC_VARIABLE);
        leftLeftTermPrimary.setType(getRebecType(receiver.getName()));
        leftBinaryExpression.setLeft(leftLeftTermPrimary);

        TermPrimary leftRightTermPrimary = new TermPrimary();
        leftRightTermPrimary.setName(generateReceiverParameterName(rebec.getName()));
        leftRightTermPrimary.setLabel(CoreRebecaLabelUtility.METHOD_PARAMETER_VARIABLE);
        leftRightTermPrimary.setType(getRebecType(receiver.getName()));
        leftBinaryExpression.setRight(leftRightTermPrimary);

        leftBinaryExpression.setOperator(EQUALITY_OPERATOR);
        leftBinaryExpression.setType(CoreRebecaTypeSystem.getBOOLEAN_TYPE());


        BinaryExpression rightBinaryExpression = new BinaryExpression();

        TermPrimary rightLeftTermPrimary = new TermPrimary();
        rightLeftTermPrimary.setName(senderDefinitionName);
        rightLeftTermPrimary.setLabel(CoreRebecaLabelUtility.KNOWNREBEC_VARIABLE);
        rightLeftTermPrimary.setType(getRebecType(sender.getName()));
        rightBinaryExpression.setLeft(rightLeftTermPrimary);

        TermPrimary rightRightTermPrimary = new TermPrimary();
        rightRightTermPrimary.setName(REACTIVE_CLASS_SENDER_KEYWORD);
        rightRightTermPrimary.setLabel(CoreRebecaLabelUtility.RESERVED_WORD);
        rightRightTermPrimary.setType(CoreRebecaTypeSystem.REACTIVE_CLASS_TYPE);
        rightBinaryExpression.setRight(rightRightTermPrimary);

        rightBinaryExpression.setOperator(EQUALITY_OPERATOR);
        rightBinaryExpression.setType(CoreRebecaTypeSystem.getBOOLEAN_TYPE());

        binaryExpression.setLeft(leftBinaryExpression);
        binaryExpression.setRight(rightBinaryExpression);
        binaryExpression.setOperator(AND_OPERATOR);
        binaryExpression.setType(CoreRebecaTypeSystem.getBOOLEAN_TYPE());

        return binaryExpression;
    }

    private FieldDeclaration getLossFieldDeclaration() {
        FieldDeclaration fieldDeclaration = new FieldDeclaration();
        List<VariableDeclarator> variableDeclarators = fieldDeclaration.getVariableDeclarators();
        VariableDeclarator variableDeclarator = new VariableDeclarator();
        variableDeclarator.setVariableName(NETWORK_LOSS_VARIABLE_NAME);
        OrdinaryVariableInitializer variableInitializer = new OrdinaryVariableInitializer();
        variableInitializer.setType(CoreRebecaTypeSystem.getBOOLEAN_TYPE());
        Literal literal = new Literal();
        literal.setType(CoreRebecaTypeSystem.getBOOLEAN_TYPE());
        literal.setLiteralValue(FALSE);
        variableInitializer.setValue(literal);
        variableDeclarator.setVariableInitializer(variableInitializer);
        variableDeclarators.add(variableDeclarator);
        fieldDeclaration.setType(CoreRebecaTypeSystem.getBOOLEAN_TYPE());
        return fieldDeclaration;
    }

    private FieldDeclaration getDelayFieldDeclaration() {
        FieldDeclaration fieldDeclaration = new FieldDeclaration();
        List<VariableDeclarator> variableDeclarators = fieldDeclaration.getVariableDeclarators();
        VariableDeclarator variableDeclarator = new VariableDeclarator();
        variableDeclarator.setVariableName(DELAY_KEYWORD);
        OrdinaryVariableInitializer variableInitializer = new OrdinaryVariableInitializer();
        variableInitializer.setType(CoreRebecaTypeSystem.getBYTE_TYPE());
        Literal literal = new Literal();
        literal.setType(CoreRebecaTypeSystem.getBYTE_TYPE());
        literal.setLiteralValue("0");
        variableInitializer.setValue(literal);
        variableDeclarator.setVariableInitializer(variableInitializer);
        variableDeclarators.add(variableDeclarator);
        fieldDeclaration.setType(CoreRebecaTypeSystem.getINT_TYPE());
        return fieldDeclaration;
    }

    private BlockStatement getNetworkConditionalLossStatement() {
        BlockStatement blockStatement = new BlockStatement();
        List<Statement> statements = blockStatement.getStatements();
        BinaryExpression binaryExpression = new BinaryExpression();

        TermPrimary left = new TermPrimary();
        left.setName(NETWORK_LOSS_VARIABLE_NAME);
        left.setType(CoreRebecaTypeSystem.getBOOLEAN_TYPE());
        left.setLabel(CoreRebecaLabelUtility.LOCAL_VARIABLE);
        binaryExpression.setLeft(left);

        Literal right = new Literal();
        right.setLiteralValue(TRUE);
        right.setType(CoreRebecaTypeSystem.getBOOLEAN_TYPE());

        binaryExpression.setLeft(left);
        binaryExpression.setRight(right);
        binaryExpression.setOperator(ASSIGN_OPERATOR);

        statements.add(binaryExpression);
        return blockStatement;
    }

    private BlockStatement getNetworkConditionalDelayStatement(Expression delay) {
        BlockStatement blockStatement = new BlockStatement();
        List<Statement> statements = blockStatement.getStatements();
        BinaryExpression binaryExpression = new BinaryExpression();

        TermPrimary left = new TermPrimary();
        left.setName(DELAY_KEYWORD);
        left.setType(CoreRebecaTypeSystem.getINT_TYPE());
        left.setLabel(CoreRebecaLabelUtility.LOCAL_VARIABLE);
        binaryExpression.setLeft(left);

        binaryExpression.setLeft(left);
        binaryExpression.setRight(delay);
        binaryExpression.setOperator(ASSIGN_OPERATOR);
        binaryExpression.setType(CoreRebecaTypeSystem.getINT_TYPE());
        statements.add(binaryExpression);
        return blockStatement;
    }

    private ReactiveClassDeclaration getKnownNode(String rebecName, ReactiveClassDeclaration network) {
        for (FieldDeclaration fieldDeclaration : network.getKnownRebecs()) {
            for (VariableDeclarator variableDeclarator : fieldDeclaration.getVariableDeclarators()) {
                if (variableDeclarator.getVariableName().equals(rebecName)) {
                    return this.reactiveClassDeclarationMap.get(fieldDeclaration.getType().getTypeName());
                }
            }
        }

        return null;
    }

    private DotPrimary getDotPrimaryStatement(ReactiveClassDeclaration rebec, MsgsrvDeclaration msgsrvDeclaration) {
        DotPrimary dotPrimary = new DotPrimary();

        TermPrimary leftDotPrimary = new TermPrimary();
        leftDotPrimary.setLabel(CoreRebecaLabelUtility.METHOD_PARAMETER_VARIABLE);
        leftDotPrimary.setName(generateReceiverParameterName(rebec.getName()));
        leftDotPrimary.setType(getRebecType(rebec.getName()));

        TermPrimary rightDotPrimary = new TermPrimary();
        rightDotPrimary.setLabel(CoreRebecaLabelUtility.MSGSRV);
        rightDotPrimary.setName(msgsrvDeclaration.getName());
        rightDotPrimary.setType(CoreRebecaTypeSystem.MSGSRV_TYPE);

        TimedRebecaParentSuffixPrimary timedRebecaParentSuffixPrimary = new TimedRebecaParentSuffixPrimary();
        TermPrimary afterExpression = new TermPrimary();
        afterExpression.setLabel(CoreRebecaLabelUtility.LOCAL_VARIABLE);
        afterExpression.setName(DELAY_KEYWORD);
        afterExpression.setType(CoreRebecaTypeSystem.getINT_TYPE());
        timedRebecaParentSuffixPrimary.setAfterExpression(afterExpression);
        List<Expression> arguments = timedRebecaParentSuffixPrimary.getArguments();
        for (FormalParameterDeclaration formalParameterDeclaration : msgsrvDeclaration.getFormalParameters()) {
            TermPrimary termPrimary = new TermPrimary();
            termPrimary.setName(formalParameterDeclaration.getName());
            termPrimary.setType(formalParameterDeclaration.getType());
            termPrimary.setLabel(CoreRebecaLabelUtility.METHOD_PARAMETER_VARIABLE);
            arguments.add(termPrimary);
        }
        rightDotPrimary.setParentSuffixPrimary(timedRebecaParentSuffixPrimary);

        dotPrimary.setLeft(leftDotPrimary);
        dotPrimary.setRight(rightDotPrimary);
        dotPrimary.setType(CoreRebecaTypeSystem.MSGSRV_TYPE);

        return dotPrimary;
    }

    private ConditionalStatement getConditionalDotPrimaryStatement(ReactiveClassDeclaration rebec, MsgsrvDeclaration msgsrvDeclaration) {
        ConditionalStatement conditionalStatement = new ConditionalStatement();
        TermPrimary termPrimary = new TermPrimary();
        termPrimary.setName(NETWORK_LOSS_VARIABLE_NAME);
        termPrimary.setLabel(CoreRebecaLabelUtility.LOCAL_VARIABLE);
        termPrimary.setType(CoreRebecaTypeSystem.getBOOLEAN_TYPE());

        conditionalStatement.setCondition(termPrimary);
        BlockStatement blockStatement = new BlockStatement();
        List<Statement> statements = blockStatement.getStatements();
        ConditionalStatement ifConditionalStatement = new ConditionalStatement();
        NonDetExpression nonDetExpression = new NonDetExpression();
        List<Expression> choices = nonDetExpression.getChoices();
        Literal trueChoice = new Literal();
        trueChoice.setLiteralValue(TRUE);
        trueChoice.setType(CoreRebecaTypeSystem.getBOOLEAN_TYPE());
        choices.add(trueChoice);

        Literal falseChoice = new Literal();
        falseChoice.setLiteralValue(FALSE);
        falseChoice.setType(CoreRebecaTypeSystem.getBOOLEAN_TYPE());
        choices.add(falseChoice);

        nonDetExpression.setType(CoreRebecaTypeSystem.getBOOLEAN_TYPE());

        ifConditionalStatement.setCondition(nonDetExpression);
        BlockStatement ifConditionalStatementBlock = new BlockStatement();
        List<Statement> ifConditionalStatementBlocks = ifConditionalStatementBlock.getStatements();


        ifConditionalStatementBlocks.add(getDotPrimaryStatement(rebec, msgsrvDeclaration));

        ifConditionalStatement.setStatement(ifConditionalStatementBlock);
        statements.add(ifConditionalStatement);
        conditionalStatement.setStatement(blockStatement);

        BlockStatement elseBlockStatement = new BlockStatement();
        List<Statement> elseStatements = elseBlockStatement.getStatements();
        elseStatements.add(getDotPrimaryStatement(rebec, msgsrvDeclaration));

        conditionalStatement.setElseStatement(elseBlockStatement);
        return conditionalStatement;
    }
    private void addNetworkConditionalDelayStatementToMsgsrv(ReactiveClassDeclaration network, NetworkDeclaration networkDeclaration, ReactiveClassDeclaration rebec, List<Statement> statements) {
        for (DelayExpression delayExpression : networkDeclaration.getDelays()) {
            ReactiveClassDeclaration sender = getKnownNode(delayExpression.getSenderName(), network);
            ReactiveClassDeclaration receiver = getKnownNode(delayExpression.getReceiverName(), network);
            if (!receiver.getName().equals(rebec.getName())) {
                continue;
            }

            ConditionalStatement conditionalStatement = new ConditionalStatement();
            conditionalStatement.setCondition(getNetworkCondition(sender, receiver, rebec, delayExpression.getSenderName(), delayExpression.getReceiverName()));
            conditionalStatement.setStatement(getNetworkConditionalDelayStatement(delayExpression.getAmount()));
            statements.add(conditionalStatement);
        }
    }
    private void addNetworkConditionalLossStatementToMsgsrv(ReactiveClassDeclaration network, NetworkDeclaration networkDeclaration, ReactiveClassDeclaration rebec, List<Statement> statements) {
        for (LossExpression lossExpression : networkDeclaration.getLosses()) {
            ReactiveClassDeclaration sender = getKnownNode(lossExpression.getSenderName(), network);
            ReactiveClassDeclaration receiver = getKnownNode(lossExpression.getReceiverName(), network);
            if (!receiver.getName().equals(rebec.getName())) {
                continue;
            }

            ConditionalStatement conditionalStatement = new ConditionalStatement();
            conditionalStatement.setCondition(getNetworkCondition(sender, receiver, rebec, lossExpression.getSenderName(), lossExpression.getReceiverName()));
            conditionalStatement.setStatement(getNetworkConditionalLossStatement());
            statements.add(conditionalStatement);
        }
    }
    private BlockStatement createBlockToMsgsrv(ReactiveClassDeclaration network, NetworkDeclaration networkDeclaration, ReactiveClassDeclaration rebec, MsgsrvDeclaration msgsrvDeclaration) {
        BlockStatement blockStatement = new BlockStatement();
        List<Statement> statements = blockStatement.getStatements();
        statements.add(getLossFieldDeclaration());
        statements.add(getDelayFieldDeclaration());

        addNetworkConditionalDelayStatementToMsgsrv(network, networkDeclaration, rebec, statements);
        addNetworkConditionalLossStatementToMsgsrv(network, networkDeclaration, rebec, statements);

        statements.add(getConditionalDotPrimaryStatement(rebec, msgsrvDeclaration));
        return blockStatement;
    }

    private void addMsgsrvToNetwork(ReactiveClassDeclaration network, NetworkDeclaration networkDeclaration, ReactiveClassDeclaration rebec) {
        List<MsgsrvDeclaration> networkMsgsrvs = network.getMsgsrvs();
        for (MsgsrvDeclaration msgsrvDeclaration : rebec.getMsgsrvs()) {
            MsgsrvDeclaration networkMsgsrv = new MsgsrvDeclaration();
            networkMsgsrv.setName(generateMsgSrvName(rebec.getName(), msgsrvDeclaration.getName()));
            List<FormalParameterDeclaration> formalParameters = networkMsgsrv.getFormalParameters();
            FormalParameterDeclaration formalParameterDeclaration = new FormalParameterDeclaration();

            formalParameterDeclaration.setName(generateReceiverParameterName(rebec.getName()));
            formalParameterDeclaration.setType(getRebecType(rebec.getName()));
            formalParameters.add(formalParameterDeclaration);
            AccessModifier accessModifier = new AccessModifier();
            accessModifier.setName(PUBLIC_ACCESS_LEVEL);
            networkMsgsrv.setAccessModifier(accessModifier);

            for (FormalParameterDeclaration rebecParameterDeclaration : msgsrvDeclaration.getFormalParameters()) {
                formalParameters.add(rebecParameterDeclaration);
            }

            networkMsgsrv.setBlock(createBlockToMsgsrv(network, networkDeclaration, rebec, msgsrvDeclaration));

            networkMsgsrvs.add(networkMsgsrv);
        }
        network.setQueueSize(network.getQueueSize()+rebec.getQueueSize());
    }

    private OrdinaryPrimitiveType getRebecType(String rebecTpyeName) {
        for (ReactiveClassDeclaration reactiveClassDeclaration : timedRebecaCode.getReactiveClassDeclaration()) {
            for (FieldDeclaration fieldDeclaration : reactiveClassDeclaration.getKnownRebecs()) {
                if (fieldDeclaration.getType().getTypeName().equals(rebecTpyeName))
                    return (OrdinaryPrimitiveType) fieldDeclaration.getType();
            }
        }

        return null;
    }

    private void setWiredAnnotationInReactiveClass(ReactiveClassDeclaration wired) {
        for (Annotation annotation : wired.getAnnotations()) {
            if (annotation.getIdentifier().equals(REACTIVE_CLASS_WIRED_ANNOTATION)) {
                return;
            }
        }
        Annotation annotation = new Annotation();
        annotation.setIdentifier(REACTIVE_CLASS_WIRED_ANNOTATION);
        wired.getAnnotations().add(annotation);
    }
    private MainRebecDefinition defineWiredReactiveClass(String name) {
        Optional<ReactiveClassDeclaration> wired = this.rebecaModelNetworkDecorator.findWiredReactiveClassOf(name);
        if (wired.isEmpty()) {
            return new MainRebecDefinition();
        }

        TimedMainRebecDefinition mainRebecDefinition = new TimedMainRebecDefinition();
        mainRebecDefinition.setName("_" + wired.get().getName());
        OrdinaryPrimitiveType type = new OrdinaryPrimitiveType();
        type.setName((wired.get().getName()));
        mainRebecDefinition.setType(type);
        setWiredAnnotationInReactiveClass(wired.get());
        return mainRebecDefinition;
    }

    private Expression getNewBinding(MainRebecDefinition mainRebecDefinition) {
        TermPrimary termPrimary = new TermPrimary();
        termPrimary.setName(mainRebecDefinition.getName());
        termPrimary.setType(mainRebecDefinition.getType());
        termPrimary.setLabel(CoreRebecaLabelUtility.LOCAL_VARIABLE);
        return termPrimary;
    }

    private void bindNetworkReactiveClassToReactiveClassesBasedOnRebecDefinition() {
        List<MainRebecDefinition> mainRebecDefinitions = new ArrayList<>();
        Map<String, MainRebecDefinition> mainRebecDefinitionNames = new HashMap<>();
        for (MainRebecDefinition mainRebecDefinition : timedRebecaCode.getMainDeclaration().getMainRebecDefinition()) {
            List<Expression> newBindings = new ArrayList<>();
            for (Expression expression : mainRebecDefinition.getBindings()) {
                TermPrimary termPrimary = ((TermPrimary) expression);
                String bindingTypeName = termPrimary.getType().getTypeName();
                if (termPrimary.getAnnotations().isEmpty()) {
                    if (mainRebecDefinitionNames.get(bindingTypeName) != null) {
                        newBindings.add(getNewBinding(mainRebecDefinitionNames.get(bindingTypeName)));
                        continue;
                    }

                    MainRebecDefinition wiredMainRebecDefinition = defineWiredReactiveClass(bindingTypeName);
                    mainRebecDefinitions.add(wiredMainRebecDefinition);
                    newBindings.add(getNewBinding(wiredMainRebecDefinition));
                    mainRebecDefinitionNames.put(bindingTypeName, wiredMainRebecDefinition);

                    continue;
                }

                String networkName = termPrimary.getAnnotations().get(0).getIdentifier();
                newBindings.add(getNewBinding(mainRebecDefinitionMap.get(networkName)));

                if (!checkNetworkImplementReactiveClass(networkName, bindingTypeName, networkRebecs)) {
                    continue;
                }

                ReactiveClassDeclaration reactiveClassDeclaration = reactiveClassDeclarationMap.get(bindingTypeName);
                ReactiveClassDeclaration network = reactiveClassDeclarationMap.
                        get(mainRebecDefinitionMap.get(networkName).getType().getTypeName());

                network.getImplements().add(this.rebecaModelNetworkDecorator.getNetworkInterfaceType(reactiveClassDeclaration.getName()));
                NetworkDeclaration networkDeclaration = networkDeclarationMap.get(mainNetworkDefinitionMap.get(networkName).getType().getTypeName());
                addMsgsrvToNetwork(network, networkDeclaration, reactiveClassDeclaration);
            }
            mainRebecDefinition.getBindings().addAll(newBindings);
        }

        timedRebecaCode.getMainDeclaration().getMainRebecDefinition().addAll(mainRebecDefinitions);
    }

    private Integer findMaxReactiveDefinitionPriorities() {
        Integer max = 0;
        for (MainRebecDefinition mainRebecDefinition : timedRebecaCode.getMainDeclaration().getMainRebecDefinition()){
            for (Annotation annotation : mainRebecDefinition.getAnnotations()) {
                if (annotation.getIdentifier().equals(REACTIVE_CLASS_PRIORITY_ANNOTATION)) {
                    if (annotation.getValue() instanceof Literal) {
                        String value = ((Literal) annotation.getValue()).getLiteralValue();
                        Integer intValue = 0;
                        try {
                            intValue = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            intValue = 0;
                        }

                        if (intValue > max) {
                            max = intValue;
                        }
                    }
                }
            }

        }

        return max;
    }

    private Annotation getReactiveDefinitionPriority(MainRebecDefinition mainRebecDefinition) {
        for (Annotation annotation : mainRebecDefinition.getAnnotations()) {
            if (annotation.getIdentifier().equals(REACTIVE_CLASS_PRIORITY_ANNOTATION)) {
                return annotation;
            }
        }

        return null;
    }

    private void applyNetworkReactiveDefinitionPriority() {
        Integer maxRebecPriority = findMaxReactiveDefinitionPriorities();
        for (MainRebecDefinition mainRebecDefinition : timedRebecaCode.getMainDeclaration().getMainRebecDefinition()){
            Annotation annotation = getReactiveDefinitionPriority(mainRebecDefinition);
            if (annotation == null) {
                Annotation newAnnotation = new Annotation();
                newAnnotation.setIdentifier(REACTIVE_CLASS_PRIORITY_ANNOTATION);
                Literal literal = new Literal();
                literal.setType(CoreRebecaTypeSystem.getINT_TYPE());
                literal.setLiteralValue(String.valueOf(maxRebecPriority+1));
                newAnnotation.setValue(literal);
                mainRebecDefinition.getAnnotations().add(newAnnotation);
            }
        }
    }

    private void setNetworkAnnotation(ReactiveClassDeclaration network) {
        Annotation annotation = new Annotation();
        annotation.setIdentifier(REACTIVE_CLASS_NETWORK_ANNOTATION);
        network.getAnnotations().add(annotation);
    }
    private void addCreatedReactiveClassDeclarationForNetworkDeclarationToDeclarations() {
        List<ReactiveClassDeclaration> reactiveClassDeclarations = timedRebecaCode.getReactiveClassDeclaration();
        for (NetworkDeclaration networkDeclaration : timedRebecaCode.getNetworkDeclaration()) {
            ReactiveClassDeclaration reactiveClassDeclaration = new ReactiveClassDeclaration();
            reactiveClassDeclaration.setName(networkDeclaration.getName());
            ConstructorDeclaration constructorDeclaration = new ConstructorDeclaration();
            constructorDeclaration.setName(networkDeclaration.getName());
            constructorDeclaration.setBlock(new BlockStatement());
            reactiveClassDeclaration.getConstructors().add(constructorDeclaration);
            List<FieldDeclaration> knownRebecs = reactiveClassDeclaration.getKnownRebecs();
            for (FieldDeclaration knownNodes : networkDeclaration.getKnownNodes()) {
                knownRebecs.add(knownNodes);
            }
            reactiveClassDeclaration.setQueueSize(0);
            setNetworkAnnotation(reactiveClassDeclaration);

            reactiveClassDeclarations.add(reactiveClassDeclaration);
        }
    }

    private void addCreatedRebecDefinitionForNetworkDefinitionToDefinitions() {
        List<MainRebecDefinition> mainRebecDefinitions = timedRebecaCode.getMainDeclaration().getMainRebecDefinition();
        for (MainNetworkDefinition mainNetworkDefinition : ((TimedMainDeclaration) timedRebecaCode.getMainDeclaration()).getMainNetworkDefinition()) {
            TimedMainRebecDefinition mainRebecDefinition = new TimedMainRebecDefinition();
            mainRebecDefinition.setName(mainNetworkDefinition.getName());
            mainRebecDefinition.setMailbox(mainNetworkDefinition.getMailbox());
            OrdinaryPrimitiveType type = new OrdinaryPrimitiveType();
            type.setName((mainNetworkDefinition.getType().getTypeName()));
            mainRebecDefinition.setType(type);
            mainRebecDefinitions.add(mainRebecDefinition);
            mainRebecDefinition.getBindings().addAll(mainNetworkDefinition.getBindings());
            mainNetworkDefinitionMap.put(mainNetworkDefinition.getName(), mainNetworkDefinition);
        }
    }

    public void changeRebecaCode() {
        for (NetworkDeclaration networkDeclaration : timedRebecaCode.getNetworkDeclaration()) {
            networkDeclarationMap.put(networkDeclaration.getName(), networkDeclaration);
        }

        addCreatedReactiveClassDeclarationForNetworkDeclarationToDeclarations();

        for (ReactiveClassDeclaration reactiveClassDeclaration : timedRebecaCode.getReactiveClassDeclaration()) {
            reactiveClassDeclarationMap.put(reactiveClassDeclaration.getName(), reactiveClassDeclaration);
        }

        applyNetworkReactiveDefinitionPriority();

        addCreatedRebecDefinitionForNetworkDefinitionToDefinitions();

        for (MainRebecDefinition mainRebecDefinition : timedRebecaCode.getMainDeclaration().getMainRebecDefinition()){
            mainRebecDefinitionMap.put(mainRebecDefinition.getName(), (TimedMainRebecDefinition) mainRebecDefinition);
        }

        bindNetworkReactiveClassToReactiveClassesBasedOnRebecDefinition();
    }
}