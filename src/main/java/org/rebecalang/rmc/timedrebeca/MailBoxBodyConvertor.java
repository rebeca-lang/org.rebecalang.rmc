package org.rebecalang.rmc.timedrebeca;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.compiler.modelcompiler.timedrebeca.objectmodel.*;

import java.util.*;

public class MailBoxBodyConvertor {
    public void setTimedRebecaCode(TimedRebecaCode timedRebecaCode) {
        this.timedRebecaCode = timedRebecaCode;
    }

    private TimedRebecaCode timedRebecaCode;

    public static String removeQuotes(String str) {
        if (str != null && str.length() >= 2 && str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    private Map<String, Integer> getRebecMsgsvrID(String msgsrv) {
        msgsrv = removeQuotes(msgsrv);
        Map<String, Integer> rebecToMsgsrv = new HashMap<>();
        for (ReactiveClassDeclaration reactiveClassDeclaration : timedRebecaCode.getReactiveClassDeclaration()) {
            for (Integer j = 0; j < reactiveClassDeclaration.getMsgsrvs().size(); j++) {
                if (reactiveClassDeclaration.getMsgsrvs().get(j).getName().equals(msgsrv)) {
                    rebecToMsgsrv.put(reactiveClassDeclaration.getName(), j);
                }
            }
        }

        return rebecToMsgsrv;
    }

    private String getMessageServerNameCondition(String msgsrv, String operator, String index) {
        Map<String, Integer> rebecMsgsvrID = getRebecMsgsvrID(msgsrv);
        if (rebecMsgsvrID.isEmpty()) {
            return "";
        }

        String condition = "";
        Integer counter = 0;
        for (String key : rebecMsgsvrID.keySet()) {
            condition += String.format("rebecType == \"%s\" && messageQueue[%s]", key, index);
            condition += String.format(" %s %d", operator, rebecMsgsvrID.get(key));
            if (counter != rebecMsgsvrID.keySet().size() - 1) {
                condition += " || ";
            }
            counter++;
        }
        return condition;
    }

    public String ConvertConditionalOrderSpec(Expression expression, String index) {
        if (expression instanceof TermPrimary termPrimary) {
            return termPrimary.getName();
        }
        if (expression instanceof UnaryExpression unaryExpression) {
            return unaryExpression.getOperator() + "(" + ConvertConditionalOrderSpec(unaryExpression.getExpression(), index) + ")";
        }
        if (expression instanceof BinaryExpression binaryExpression) {
            String left = ConvertConditionalOrderSpec(binaryExpression.getLeft(), index);
            String right = ConvertConditionalOrderSpec(binaryExpression.getRight(), index);
            if (left.equals("messageServerName")) {
                return getMessageServerNameCondition(right, binaryExpression.getOperator(), index);
            }
            if (left.equals("sender")) {
                return String.format("rebecs[senderQueue[%s]]->getName() %s this->%s", index, binaryExpression.getOperator(), right);
            }

            return left + " " + binaryExpression.getOperator() + " " + right;
        }
        return "";
    }

    public List<String> GetKnownSenders(MailboxDeclaration mailboxDeclaration) {
        List<String> knownSenders = new ArrayList<>();
        for (FieldDeclaration fieldDeclaration :  mailboxDeclaration.getKnownSenders()) {
            for (VariableDeclarator variableDeclarator : fieldDeclaration.getVariableDeclarators()) {
                knownSenders.add(variableDeclarator.getVariableName());
            }
        }

        return knownSenders;
    }

    private String getBindingKnownSenders(MainMailboxDefinition mainMailboxDefinition) {
        List<String> knownBindingSenders = new ArrayList<>();
        for (Expression expression :  mainMailboxDefinition.getBindings()) {
            knownBindingSenders.add(String.format("\"%s\"", ((TermPrimary) expression).getName()));
        }
        return String.join(", ", knownBindingSenders);
    }

    private String getConditionalOrderSpec(String name, Integer counter, String rebecName, String knownSenders) {
        if (knownSenders.isEmpty())
            return String.format("new %sOrderSpec%d(_ref_%s->getClassName())", name, counter, rebecName);

        return String.format("new %sOrderSpec%d(_ref_%s->getClassName(), %s)", name, counter, rebecName, knownSenders);
    }

    private String getAggregationOrderSpec(AggregationConditionPrimary aggregationConditionPrimary) {
        String aggregator = aggregationConditionPrimary.getName();
        String value = ((TermPrimary) aggregationConditionPrimary.getArgument()).getName();
        String isMin = "true";
        if (aggregator.equals("max")) {
            isMin = "false";
        }

        switch (value) {
            case "messageDeadline":
                return String.format("new DeadlineOrderSpec(%s)", isMin);
            case "messageExecutionTime":
                return String.format("new DelayOrderSpec(%s)", isMin);
            case "messageArrivalTime":
                return String.format("new ArrivalTimeOrderSpec(%s)", isMin);
            default:
                return "";
        }
    }

    private MainMailboxDefinition getMailboxDefinition(List<MainMailboxDefinition> mainMailboxDefinitions, String mailboxName) {
        for (MainMailboxDefinition mainMailboxDefinition : mainMailboxDefinitions) {
            if (mainMailboxDefinition.getName().equals(mailboxName)) {
                return mainMailboxDefinition;
            }
        }
        return null;
    }

    private MailboxDeclaration getMailboxDeclaration(MainMailboxDefinition mainMailboxDefinition) {
        String name = ((OrdinaryPrimitiveType) mainMailboxDefinition.getType()).getName();
        for (MailboxDeclaration mailboxDeclaration : timedRebecaCode.getMailboxDeclaration()) {
            if (mailboxDeclaration.getName().equals(name)) {
                return mailboxDeclaration;
            }
        }
        return null;
    }

    public String AddOrderSpecsToAnActor(MainRebecDefinition mainRebecDefinition, Integer rebecID) {
        TimedMainRebecDefinition timedMainRebecDefinition = (TimedMainRebecDefinition) mainRebecDefinition;
        Expression mailBox = timedMainRebecDefinition.getMailbox();
        if (mailBox == null)
            return "";
        String mailboxName = ((TermPrimary) mailBox).getName();
        List<MainMailboxDefinition> mainMailboxDefinitions = ((TimedMainDeclaration) timedRebecaCode.getMainDeclaration()).getMainMailboxDefinition();
        MainMailboxDefinition mainMailboxDefinition = getMailboxDefinition(mainMailboxDefinitions, mailboxName);
        if (mainMailboxDefinition == null)
            return "";
        MailboxDeclaration mailboxDeclaration = getMailboxDeclaration(mainMailboxDefinition);
        String rebecName = timedMainRebecDefinition.getName();
        String output = String.format("_ref_%s->setMailBox();\n", rebecName);
        Integer counter = 1;
        for (Expression expression : mailboxDeclaration.getOrders()) {
            String orderSpec = "";
            if (expression instanceof BinaryExpression || expression instanceof UnaryExpression) {
                String knownSenders = getBindingKnownSenders(mainMailboxDefinition);
                orderSpec = getConditionalOrderSpec(mailboxDeclaration.getName(), counter, rebecName, knownSenders);
                counter++;
            }
            else if (expression instanceof AggregationConditionPrimary aggregationConditionPrimary) {
                orderSpec = getAggregationOrderSpec(aggregationConditionPrimary);
            }
            if (!orderSpec.isEmpty()) {
                output += String.format("_ref_%s->addOrderSpecs(%s);\n", rebecName, orderSpec);
            }
        }
        return output;
    }
}
