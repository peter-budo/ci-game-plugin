package hudson.plugins.cigame.rules.plugins.lint;

import java.util.List;

import org.jenkinsci.plugins.android_lint.LintResultAction;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.cigame.util.ActionSequenceRetriever;
import hudson.plugins.cigame.util.ResultSequenceValidator;

public class DefaultLintRule implements Rule {

    private final Priority tasksPriority;
    private final int pointsForAddingAnAnnotation;
    private final int pointsForRemovingAnAnnotation;

    public DefaultLintRule(Priority tasksPriority, int pointsForAddingAnAnnotation, int pointsForRemovingAnAnnotation) {
        this.tasksPriority = tasksPriority;
        this.pointsForAddingAnAnnotation = pointsForAddingAnAnnotation;
        this.pointsForRemovingAnAnnotation = pointsForRemovingAnAnnotation;
    }

    public RuleResult evaluate(AbstractBuild<?, ?> build) {

        if (new ResultSequenceValidator(Result.UNSTABLE, 2).isValid(build)) {
            List<List<LintResultAction>> sequence = new ActionSequenceRetriever<LintResultAction>(LintResultAction.class, 2).getSequence(build);
            if ((sequence != null)
                    && hasNoErrors(sequence.get(0)) && hasNoErrors(sequence.get(1))) {
                int delta = getNumberOfAnnotations(sequence.get(0)) - getNumberOfAnnotations(sequence.get(1));

                if (delta < 0) {
                    return new RuleResult(Math.abs(delta) * pointsForRemovingAnAnnotation,
                            Messages.LintRuleSet_DefaultRule_FixedWarningsCount(Math.abs(delta), tasksPriority.name())); //$NON-NLS-1$
                }
                if (delta > 0) {
                    return new RuleResult(Math.abs(delta) * pointsForAddingAnAnnotation,
                            Messages.LintRuleSet_DefaultRule_NewWarningsCount(Math.abs(delta), tasksPriority.name())); //$NON-NLS-1$
                }
            }
        }
        return RuleResult.EMPTY_RESULT;
    }

    private boolean hasNoErrors(List<LintResultAction> actions) {
        for (LintResultAction action : actions) {
            if (action.getResult().hasError()) {
                return false;
            }
        }
        return true;
    }

    private int getNumberOfAnnotations(List<LintResultAction> actions) {
        int numberOfAnnotations = 0;
        for (LintResultAction action : actions) {
            numberOfAnnotations += action.getResult().getNumberOfAnnotations(tasksPriority);
        }
        return numberOfAnnotations;
    }

    public String getName() {
        return Messages.LintRuleSet_DefaultRule_Name(tasksPriority.name()); //$NON-NLS-1$
    }
}
