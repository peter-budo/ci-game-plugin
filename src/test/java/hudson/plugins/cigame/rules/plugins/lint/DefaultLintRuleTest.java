package hudson.plugins.cigame.rules.plugins.lint;

import java.util.Arrays;

import org.jenkinsci.plugins.android_lint.LintResult;
import org.jenkinsci.plugins.android_lint.LintResultAction;
import org.junit.Test;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.cigame.model.RuleResult;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class DefaultLintRuleTest {

    @Test
    public void should_be_worth_zero_points_when_failed_build() {
        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.FAILURE);

        DefaultLintRule rule = new DefaultLintRule(Priority.HIGH, 100, -100);
        RuleResult ruleResult = rule.evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
    }

    @Test
    public void should_be_worth_zero_points_when_no_previous_build() {
        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.FAILURE);
        when(build.getPreviousBuild()).thenReturn(null);

        DefaultLintRule rule = new DefaultLintRule(Priority.HIGH, 100, -100);
        RuleResult ruleResult = rule.evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
    }

    @Test
    public void should_be_worth_zero_points() {
        AbstractBuild currentBuild = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(currentBuild.getPreviousBuild()).thenReturn(previousBuild);
        when(currentBuild.getResult()).thenReturn(Result.SUCCESS);
        when(previousBuild.getResult()).thenReturn(Result.FAILURE);

        LintResult currentResult = mock(LintResult.class);
        LintResult previousResult = mock(LintResult.class);
        LintResultAction currentAction = new LintResultAction(currentBuild, mock(HealthDescriptor.class), currentResult);
        LintResultAction previousAction = new LintResultAction(previousBuild, mock(HealthDescriptor.class), previousResult);
        when(currentBuild.getActions(LintResultAction.class)).thenReturn(Arrays.asList(currentAction));
        when(currentBuild.getAction(LintResultAction.class)).thenReturn(currentAction);
        when(previousBuild.getAction(LintResultAction.class)).thenReturn(previousAction);
        when(previousBuild.getActions(LintResultAction.class)).thenReturn(Arrays.asList(previousAction));

        when(currentResult.getNumberOfAnnotations(Priority.LOW)).thenReturn(10);
        when(previousResult.getNumberOfAnnotations(Priority.LOW)).thenReturn(5);

        RuleResult ruleResult = new DefaultLintRule(Priority.LOW, 100, -100).evaluate(currentBuild);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
}
