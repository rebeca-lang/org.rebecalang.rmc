package org.rebecalang.rmc.timedrebeca;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.compiler.modelcompiler.timedrebeca.objectmodel.*;

import java.util.List;

public class MailBoxBodyConvertor {
    public void setTimedRebecaCode(TimedRebecaCode timedRebecaCode) {
        this.timedRebecaCode = timedRebecaCode;
    }

    private TimedRebecaCode timedRebecaCode;

    public String ConvertConditionalOrderSpec(Expression expression, String index) {
        if (expression instanceof TermPrimary termPrimary) {
            String name = termPrimary.getName();
            if (name.equals("sender")) {
                return String.format("rebecs[senderQueue[%s]]->getName()", index);
            }
            return String.format("\"%s\"", name);
        }
        if (expression instanceof UnaryExpression unaryExpression) {
            return unaryExpression.getOperator() + "(" + ConvertConditionalOrderSpec(unaryExpression.getExpression(), index) + ")";
        }
        if (expression instanceof BinaryExpression binaryExpression) {
            return ConvertConditionalOrderSpec(binaryExpression.getLeft(), index) + " " + binaryExpression.getOperator() +
                    " " + ConvertConditionalOrderSpec(binaryExpression.getRight(), index);
        }
        return "";
    }

    private String getConditionalOrderSpec(String name, Integer counter) {
        return String.format("new %sOrderSpec%d()", name, counter);
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
    public String AddOrderSpecsToAnActor(MainRebecDefinition mainRebecDefinition) {
        TimedMainRebecDefinition timedMainRebecDefinition = (TimedMainRebecDefinition) mainRebecDefinition;
        String mailboxName = ((TermPrimary) timedMainRebecDefinition.getMailbox()).getName();
        List<MainMailboxDefinition> mainMailboxDefinitions = ((TimedMainDeclaration) timedRebecaCode.getMainDeclaration()).getMainMailboxDefinition();
        MainMailboxDefinition mainMailboxDefinition = getMailboxDefinition(mainMailboxDefinitions, mailboxName);
        MailboxDeclaration mailboxDeclaration = getMailboxDeclaration(mainMailboxDefinition);
        String output = "";
        String rebecName = timedMainRebecDefinition.getName();
        Integer counter = 1;
        for (Expression expression : mailboxDeclaration.getOrders()) {
            String orderSpec = "";
            if (expression instanceof BinaryExpression || expression instanceof UnaryExpression) {
                orderSpec = getConditionalOrderSpec(mailboxDeclaration.getName(), counter);
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
