package org.rebecalang.rmc.timedrebeca;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.compiler.modelcompiler.timedrebeca.objectmodel.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private ReactiveClassDeclaration getReactiveClassDeclaration(String rebecName) {
        for (ReactiveClassDeclaration reactiveClassDeclaration : timedRebecaCode.getReactiveClassDeclaration()) {
            if (reactiveClassDeclaration.getName().equals(rebecName)) {
                return reactiveClassDeclaration;
            }
        }

        return null;
    }
    private Map<Integer, Integer> getRebecMsgsvrID(String msgsrv) {
        msgsrv = removeQuotes(msgsrv);
        Map<Integer, Integer> rebecToMsgsrv = new HashMap<>();
        for (Integer i = 0; i < timedRebecaCode.getReactiveClassDeclaration().size(); i++) {
            ReactiveClassDeclaration reactiveClassDeclaration = timedRebecaCode.getReactiveClassDeclaration().get(i);
            for (Integer j = 0; j < reactiveClassDeclaration.getMsgsrvs().size(); j++) {
                if (reactiveClassDeclaration.getMsgsrvs().get(j).getName().equals(msgsrv)) {
                    rebecToMsgsrv.put(i, j);
                }
            }
        }

        return rebecToMsgsrv;
    }

    private String getMessageServerNameCondition(String msgsrv, String operator, String index) {
        Map<Integer, Integer> rebecMsgsvrID = getRebecMsgsvrID(msgsrv);
        if (rebecMsgsvrID.isEmpty()) {
            return "";
        }

        String condition = "";
        Integer counter = 0;
        for (Integer key : rebecMsgsvrID.keySet()) {
            condition += String.format("assignedRebecID == %d && messageQueue[%s]", key, index);
            condition += String.format(" %s %d", operator, rebecMsgsvrID.get(key));
            if (counter != rebecMsgsvrID.keySet().size() - 1) {
                condition += " || ";
            }
        }
        return condition;
    }
    public String ConvertConditionalOrderSpec(Expression expression, String index) {
        if (expression instanceof TermPrimary termPrimary) {
            String name = termPrimary.getName();
            if (name.equals("sender")) {
                return String.format("rebecs[senderQueue[%s]]->getName()", index);
            }
            if (name.equals("messageServerName")) {
                return name;
            }
            return String.format("\"%s\"", name);
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
             return left + " " + binaryExpression.getOperator() + " " + right;
        }
        return "";
    }

    private String getConditionalOrderSpec(String name, Integer counter, Integer rebecId) {
        return String.format("new %sOrderSpec%d(%d)", name, counter, rebecId);
    }

    private String getAggregationOrderSpec(AggregationConditionPrimary aggregationConditionPrimary) {
        String aggregator = aggregationConditionPrimary.getName();
        String value = ((TermPrimary) aggregationConditionPrimary.getArgument()).getName();
        String isMin = "true";
        if (aggregator.equals("max")) {
            isMin = "false";
        }

        //TODO: support messageArrivalTime
        switch (value) {
            case "messageDeadline":
                return String.format("new DeadlineOrderSpec(%s)", isMin);
            case "messageExecutionTime":
                return String.format("new DelayOrderSpec(%s)", isMin);
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
        MailboxDeclaration mailboxDeclaration = getMailboxDeclaration(mainMailboxDefinition);
        String output = "";
        String rebecName = timedMainRebecDefinition.getName();
        Integer counter = 1;
        for (Expression expression : mailboxDeclaration.getOrders()) {
            String orderSpec = "";
            if (expression instanceof BinaryExpression || expression instanceof UnaryExpression) {
                orderSpec = getConditionalOrderSpec(mailboxDeclaration.getName(), counter, rebecID);
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
