package hudson.plugins.cigame.rules.plugins.lint;

import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.cigame.rules.plugins.PluginRuleSet;

public class LintRuleSet extends PluginRuleSet {

    public LintRuleSet() {
        super("android-lint", Messages.LintRuleSet_Title());  //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    protected void loadRules() {
        add(new DefaultLintRule(Priority.HIGH, -5, 5));
        add(new DefaultLintRule(Priority.NORMAL, -3, 3));
        add(new DefaultLintRule(Priority.LOW, -1, 1));
    }
}
