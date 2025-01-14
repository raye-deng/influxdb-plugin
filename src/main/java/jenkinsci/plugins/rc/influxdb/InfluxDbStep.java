package jenkinsci.plugins.rc.influxdb;

import com.google.common.collect.ImmutableSet;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import jenkinsci.plugins.rc.influxdb.models.Target;
import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class InfluxDbStep extends Step {

    private String selectedTarget;
    private String customProjectName;
    private String customPrefix;
    private Map<String, Object> customData;
    private Map<String, String> customDataTags;
    private Map<String, Map<String, Object>> customDataMap;
    private Map<String, Map<String, String>> customDataMapTags;
    private List<Map<String, Map<String, Object>>> customDataMapList;
    private List<Map<String, Map<String, String>>> customDataMapTagsList;
    private String jenkinsEnvParameterField;
    private String jenkinsEnvParameterTag;
    private String measurementName;
    private boolean quiet;
    private boolean disableSonarData;
    private boolean disableGitData;
    private String actionName;

    public boolean isDisableGitData() {
        return disableGitData;
    }

    @DataBoundSetter
    public void setDisableGitData(boolean disableGitData) {
        this.disableGitData = disableGitData;
    }

    public boolean isDisableSonarData() {
        return disableSonarData;
    }

    @DataBoundSetter
    public void setDisableSonarData(boolean disableSonarData) {
        this.disableSonarData = disableSonarData;
    }

    public String getActionName() {
        return actionName;
    }

    @DataBoundSetter
    public void setActionName(String actionName) {
        this.actionName = actionName;
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        if (jenkins != null) {
            jenkins.getDescriptorByType(DescriptorImpl.class).setDisplayName(this.actionName);
        }
    }

    @Deprecated
    private transient boolean replaceDashWithUnderscore;

    @DataBoundConstructor
    public InfluxDbStep(String selectedTarget) {
        this.selectedTarget = selectedTarget;
    }

    public String getSelectedTarget() {
        String target = selectedTarget;
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        if (target == null && jenkins != null) {
            List<Target> targets = jenkins.getDescriptorByType(DescriptorImpl.class).getTargets();
            if (!targets.isEmpty()) {
                target = targets.get(0).getDescription();
            }
        }
        return target;
    }

    public void setSelectedTarget(String target) {
        Objects.requireNonNull(target);
        this.selectedTarget = target;
    }

    public String getCustomProjectName() {
        return customProjectName;
    }

    public List<Map<String, Map<String, Object>>> getCustomDataMapList() {
        return customDataMapList;
    }

    @DataBoundSetter
    public void setCustomDataMapList(List<Map<String, Map<String, Object>>> customDataMapList) {
        this.customDataMapList = customDataMapList;
    }

    public List<Map<String, Map<String, String>>> getCustomDataMapTagsList() {
        return customDataMapTagsList;
    }

    @DataBoundSetter
    public void setCustomDataMapTagsList(List<Map<String, Map<String, String>>> customDataMapTagsList) {
        this.customDataMapTagsList = customDataMapTagsList;
    }

    @DataBoundSetter
    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public boolean isQuiet() {
        return quiet;
    }

    @DataBoundSetter
    public void setCustomProjectName(String customProjectName) {
        this.customProjectName = customProjectName;
    }

    public String getCustomPrefix() {
        return customPrefix;
    }

    @DataBoundSetter
    public void setCustomPrefix(String customPrefix) {
        this.customPrefix = customPrefix;
    }

    public Map<String, Object> getCustomData() {
        return customData;
    }

    @DataBoundSetter
    public void setCustomData(Map<String, Object> customData) {
        this.customData = customData;
    }

    public Map<String, String> getCustomDataTags() {
        return customDataTags;
    }

    @DataBoundSetter
    public void setCustomDataTags(Map<String, String> customDataTags) {
        this.customDataTags = customDataTags;
    }

    public Map<String, Map<String, Object>> getCustomDataMap() {
        return customDataMap;
    }

    @DataBoundSetter
    public void setCustomDataMap(Map<String, Map<String, Object>> customDataMap) {
        this.customDataMap = customDataMap;
    }

    public Map<String, Map<String, String>> getCustomDataMapTags() {
        return customDataMapTags;
    }

    @DataBoundSetter
    public void setCustomDataMapTags(Map<String, Map<String, String>> customDataMapTags) {
        this.customDataMapTags = customDataMapTags;
    }

    public String getJenkinsEnvParameterField() {
        return jenkinsEnvParameterField;
    }

    @DataBoundSetter
    public void setJenkinsEnvParameterField(String jenkinsEnvParameterField) {
        this.jenkinsEnvParameterField = jenkinsEnvParameterField;
    }

    public String getJenkinsEnvParameterTag() {
        return jenkinsEnvParameterTag;
    }

    @DataBoundSetter
    public void setJenkinsEnvParameterTag(String jenkinsEnvParameterTag) {
        this.jenkinsEnvParameterTag = jenkinsEnvParameterTag;
    }

    public String getMeasurementName() {
        return measurementName;
    }

    @DataBoundSetter
    public void setMeasurementName(String measurementName) {
        this.measurementName = measurementName;
    }

    public boolean getReplaceDashWithUnderscore() {
        return replaceDashWithUnderscore;
    }

    @Deprecated
    @DataBoundSetter
    public void setReplaceDashWithUnderscore(boolean replaceDashWithUnderscore) {
        this.replaceDashWithUnderscore = replaceDashWithUnderscore;
    }

    public Target getTarget() {
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        if (jenkins != null) {
            List<Target> targets = jenkins.getDescriptorByType(DescriptorImpl.class).getTargets();
            if (selectedTarget == null && !targets.isEmpty()) {
                return targets.get(0);
            }
            for (Target target : targets) {
                String targetInfo = target.getDescription();
                if (targetInfo.equals(selectedTarget)) {
                    return target;
                }
            }
        }
        return null;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        if (replaceDashWithUnderscore) {
            context.get(TaskListener.class).getLogger().println("[InfluxDB Plugin][WARNING] Option \"replaceDashWithUnderscore\" is deprecated and will be removed. It is ignored now. Please remove it.");
        }

        return new InfluxDbStepExecution(this, context);
    }

    @Extension(optional = true)
    public static final class DescriptorImpl extends StepDescriptor implements Serializable {
        String displayName;

        @Nonnull
        public List<Target> getTargets() {
            return InfluxDbGlobalConfig.getInstance().getTargets();
        }

        public void addTarget(Target target) {
            InfluxDbGlobalConfig.getInstance().addTarget(target);
        }

        public void removeTarget(String targetDescription) {
            InfluxDbGlobalConfig.getInstance().removeTarget(targetDescription);
        }

        @Override
        public String getFunctionName() {
            return "influxDbStoreStep";
        }

        public void setDisplayName(@Nonnull String name) {
            this.displayName = name;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return StringUtils.isNotEmpty(this.displayName) ? this.displayName : "Publish build data to InfluxDB";
        }


        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, FilePath.class, Launcher.class, TaskListener.class);
        }

        public ListBoxModel doFillSelectedTargetItems() {
            ListBoxModel model = new ListBoxModel();
            for (Target target : getTargets()) {
                model.add(target.getDescription());
            }
            return model;
        }
    }
}
