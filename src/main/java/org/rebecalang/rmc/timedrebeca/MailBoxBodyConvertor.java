package org.rebecalang.rmc.timedrebeca;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.UnaryExpression;

public class MailBoxBodyConvertor {
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
}
