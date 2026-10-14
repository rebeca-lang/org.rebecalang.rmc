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

    /**
     * Adds conditional delay statements to the list of statements for the network message server.
     * <p>
     * This method iterates through the delay expressions defined in the `NetworkDeclaration`. For each delay expression,
     * it generates code with this condition if the sender and receiver match the provided reactive class and network conditions. If the receiver matches
     * the given reactive class, a conditional statement is created with a delay corresponding to the delay expression.
     * The conditional statement is then added to the list of statements for the network message server. This ensures that
     * the network-assigned delay is applied when the message sender and receiver correspond to the delay statement defined
     * in the network declaration.
     *
     * @param network the network reactive class declaration to which the delay statements will be added
     * @param networkDeclaration the network declaration containing delay expressions
     * @param rebec the reactive class declaration for which the delay statements are being defined
     * @param statements the list of statements to which the conditional delay statements will be added
     */
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

    /**
     * Adds conditional loss statements to the list of statements for the network message server.
     * <p>
     * This method iterates through the loss expressions defined in the `NetworkDeclaration`. For each loss expression,
     * it generates code with this condition if the sender and receiver match the provided reactive class and network conditions. If the receiver matches
     * the given reactive class, a conditional statement is created indicating whether the message should be lost based on
     * the loss expression. The conditional statement is then added to the list of statements for the network message server.
     * This ensures that the network-assigned message loss behavior is applied when the message sender and receiver correspond
     * to the loss statement defined in the network declaration.
     *
     * @param network the network reactive class declaration to which the loss statements will be added
     * @param networkDeclaration the network declaration containing loss expressions
     * @param rebec the reactive class declaration for which the loss statements are being defined
     * @param statements the list of statements to which the conditional loss statements will be added
     */
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

    /**
     * Creates and adds message server declarations to the network reactive class based on the message servers
     * of a given reactive class declaration.
     * <p>
     * For each message server declaration in the provided reactive class, a corresponding message server
     * declaration is created and added to the network reactive class. This method also sets up the necessary
     * formal parameters and access modifiers for the message server. By creating these message servers for the
     * network reactive class, the network can call these message servers instead of directly invoking the Rebec
     * message server. This allows for better integration and handling of network-level interactions.
     *
     * @param network the network reactive class declaration to which message servers will be added
     * @param networkDeclaration the network declaration that provides context for the network reactive class
     * @param rebec the reactive class declaration from which message servers are derived
     */
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

    /**
     * Adds a "wired" annotation to the specified reactive class declaration.
     * <p>
     * This method checks if the given reactive class already has an annotation with the identifier
     * {@code REACTIVE_CLASS_WIRED_ANNOTATION}. If not, it creates and adds this annotation to the reactive
     * class declaration. Reactive classes that are defined as wired for network interfaces are annotated
     * with "wired" to distinguish them and facilitate direct interaction with the Rebec message server.
     *
     * @param wired the reactive class declaration to which the "wired" annotation will be added
     */
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

    /**
     * Defines a wired reactive class for a given reactive class name.
     * <p>
     * This method looks for a wired reactive class associated with the specified name. If found, it creates
     * a `TimedMainRebecDefinition` for the wired reactive class, setting its name and type accordingly.
     * Wired reactive classes are defined for every declared reactive class to ensure that if a network
     * interface is assigned to a wired class, the Rebec message server should be called directly.
     *
     * @param name the name of the reactive class for which to define a wired reactive class
     * @return a `MainRebecDefinition` for the wired reactive class, or an empty `MainRebecDefinition` if no wired class is found
     */
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

    /**
     * Finds the maximum priority value from the annotations of main Rebec definitions.
     * <p>
     * In the context of reactive classes, priority is descending: the minimum value (0) has the highest priority,
     * while positive infinity has the lowest priority. This method iterates through the annotations of each main
     * Rebec definition and identifies the highest priority value specified.
     *
     * @return the maximum priority value found among the reactive class definitions
     */
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

    /**
     * Retrieves the priority annotation from a given main Rebec definition.
     * <p>
     * This method iterates through the annotations of the provided main Rebec definition to find
     * the one that specifies the reactive class priority. In the context of reactive classes,
     * priority is descending: the minimum value (0) has the highest priority, while positive infinity
     * has the lowest priority. Reactive classes generated for networks are assigned the lowest priority
     * because network-related classes inherently have lower precedence.
     *
     * @param mainRebecDefinition the main Rebec definition from which to retrieve the priority annotation
     * @return the priority annotation if found, otherwise {@code null}
     */
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

    /**
     * Sets a network annotation for a reactive class declaration which is generated for a network.
     * <p>
     * This method creates an annotation with a specific identifier and adds it to the annotations
     * list of the provided reactive class declaration. The annotation indicates that the reactive
     * class is associated with a network.
     *
     * @param network the reactive class declaration to which the network annotation will be added
     */
    private void setNetworkAnnotation(ReactiveClassDeclaration network) {
        Annotation annotation = new Annotation();
        annotation.setIdentifier(REACTIVE_CLASS_NETWORK_ANNOTATION);
        network.getAnnotations().add(annotation);
    }

    /**
     * Adds reactive class declarations based on network declarations from the `timedRebecaCode` object
     * to the list of reactive class declarations.
     * <p>
     * Each reactive class declaration is initialized with corresponding names, constructors, known nodes,
     * and other necessary attributes derived from the network declarations.
     *
     * @see TimedRebecaCode#getReactiveClassDeclaration()
     * @see TimedRebecaCode#getNetworkDeclaration()
     */
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

    /**
     * Converts network definitions into Rebec definitions and adds them to the main Rebec definitions list
     * while updating the main network definition map.
     *
     * @see TimedRebecaCode#getMainDeclaration()
     */
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

    /**
     * This method processes the network and reactive class declarations from the `timedRebecaCode` object.
     * It populates several maps with these declarations, applies priorities, and binds network reactive classes
     * to reactive classes based on their definitions.
     * <p>
     * The method performs the following steps:
     * <ul>
     *     <li>Iterates over the network declarations and populates the {@code networkDeclarationMap}.</li>
     *     <li>Adds created reactive class declarations for network declarations to the declarations list.</li>
     *     <li>Iterates over the reactive class declarations and populates the {@code reactiveClassDeclarationMap}.</li>
     *     <li>Applies network reactive definition priorities.</li>
     *     <li>Adds created Rebec definitions for network definitions to the definitions list.</li>
     *     <li>Iterates over the main Rebec definitions and populates the {@code mainRebecDefinitionMap}.</li>
     *     <li>Binds network reactive classes to reactive classes based on Rebec definitions.</li>
     * </ul>
     */
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