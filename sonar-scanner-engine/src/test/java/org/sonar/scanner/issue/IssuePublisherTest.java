/*
 * SonarQube
 * Copyright (C) 2009-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.scanner.issue;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;
import org.sonar.api.batch.bootstrap.ProjectDefinition;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.DefaultInputProject;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.batch.rule.internal.NewActiveRule;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.internal.DefaultExternalIssue;
import org.sonar.api.batch.sensor.issue.internal.DefaultIssue;
import org.sonar.api.batch.sensor.issue.internal.DefaultIssueLocation;
import org.sonar.api.batch.sensor.issue.internal.DefaultMessageFormatting;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.CleanCodeAttribute;
import org.sonar.api.rules.RuleType;
import org.sonar.scanner.protocol.output.ScannerReport;
import org.sonar.scanner.protocol.output.ScannerReport.FlowType;
import org.sonar.scanner.report.ReportPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.sonar.api.batch.sensor.issue.MessageFormatting.Type.CODE;
import static org.sonar.api.issue.impact.SoftwareQuality.MAINTAINABILITY;
import static org.sonar.api.issue.impact.SoftwareQuality.RELIABILITY;

@RunWith(MockitoJUnitRunner.class)
class IssuePublisherTest {
  private static final RuleKey JAVA_RULE_KEY = RuleKey.of("java", "AvoidCycle");

  private DefaultInputProject project;

  @TempDir
  public File temp;
  public IssueFilters filters = mock(IssueFilters.class);

  private final ActiveRulesBuilder activeRulesBuilder = new ActiveRulesBuilder();
  private IssuePublisher moduleIssues;
  private final DefaultInputFile file = new TestInputFileBuilder("foo", "src/Foo.php").initMetadata("Foo\nBar\nBiz\n").build();
  private final ReportPublisher reportPublisher = mock(ReportPublisher.class, RETURNS_DEEP_STUBS);

  @BeforeEach
  void prepare() {
    project = new DefaultInputProject(ProjectDefinition.create()
      .setKey("foo")
      .setBaseDir(temp)
      .setWorkDir(temp));

    activeRulesBuilder.addRule(new NewActiveRule.Builder()
      .setRuleKey(JAVA_RULE_KEY)
      .setSeverity(Severity.INFO)
      .setQProfileKey("qp-1")
      .build());
    initModuleIssues();
  }

  @Test
  void ignore_null_active_rule() {
    RuleKey inactiveRuleKey = RuleKey.of("repo", "inactive");
    initModuleIssues();
    DefaultIssue issue = new DefaultIssue(project)
      .at(new DefaultIssueLocation().on(file).at(file.selectLine(3)).message("Foo"))
      .forRule(inactiveRuleKey);
    boolean added = moduleIssues.initAndAddIssue(issue);

    assertThat(added).isFalse();
    verifyNoInteractions(reportPublisher);
  }

  @Test
  void ignore_null_rule_of_active_rule() {
    initModuleIssues();

    DefaultIssue issue = new DefaultIssue(project)
      .at(new DefaultIssueLocation().on(file).at(file.selectLine(3)).message("Foo"))
      .forRule(JAVA_RULE_KEY);
    boolean added = moduleIssues.initAndAddIssue(issue);

    assertThat(added).isFalse();
    verifyNoInteractions(reportPublisher);
  }

  @Test
  void add_issue_to_cache() {
    initModuleIssues();

    final String ruleDescriptionContextKey = "spring";
    DefaultIssue issue = new DefaultIssue(project)
      .at(new DefaultIssueLocation().on(file).at(file.selectLine(3)).message("Foo"))
      .forRule(JAVA_RULE_KEY)
      .overrideSeverity(org.sonar.api.batch.rule.Severity.CRITICAL)
      .setQuickFixAvailable(true)
      .setRuleDescriptionContextKey(ruleDescriptionContextKey)
      .setCodeVariants(List.of("variant1", "variant2"))
      .overrideImpact(MAINTAINABILITY, org.sonar.api.issue.impact.Severity.HIGH)
      .overrideImpact(RELIABILITY, org.sonar.api.issue.impact.Severity.LOW);

    when(filters.accept(any(InputComponent.class), any(ScannerReport.Issue.class), anyString())).thenReturn(true);

    boolean added = moduleIssues.initAndAddIssue(issue);

    assertThat(added).isTrue();
    ArgumentCaptor<ScannerReport.Issue> argument = ArgumentCaptor.forClass(ScannerReport.Issue.class);
    verify(reportPublisher.getWriter()).appendComponentIssue(eq(file.scannerId()), argument.capture());
    assertThat(argument.getValue().getOverriddenSeverity()).isEqualTo(org.sonar.scanner.protocol.Constants.Severity.CRITICAL);
    assertThat(argument.getValue().getQuickFixAvailable()).isTrue();
    assertThat(argument.getValue().getRuleDescriptionContextKey()).isEqualTo(ruleDescriptionContextKey);
    assertThat(argument.getValue().getCodeVariantsList()).containsExactly("variant1", "variant2");

    ScannerReport.Impact impact1 = ScannerReport.Impact.newBuilder().setSoftwareQuality(ScannerReport.SoftwareQuality.MAINTAINABILITY)
      .setSeverity(ScannerReport.ImpactSeverity.ImpactSeverity_HIGH).build();
    ScannerReport.Impact impact2 = ScannerReport.Impact.newBuilder().setSoftwareQuality(ScannerReport.SoftwareQuality.RELIABILITY)
      .setSeverity(ScannerReport.ImpactSeverity.ImpactSeverity_LOW).build();
    assertThat(argument.getValue().getOverriddenImpactsList()).containsExactlyInAnyOrder(impact1, impact2);
  }

  @Test
  void add_issue_flows_to_cache() {
    initModuleIssues();

    DefaultMessageFormatting messageFormatting = new DefaultMessageFormatting().start(0).end(4).type(CODE);
    DefaultIssue issue = new DefaultIssue(project)
      .at(new DefaultIssueLocation().on(file))
      // Flow without type
      .addFlow(List.of(new DefaultIssueLocation().on(file).at(file.selectLine(1)).message("Foo1", List.of(messageFormatting)),
        new DefaultIssueLocation().on(file).at(file.selectLine(2)).message("Foo2")))
      // Flow with type and description
      .addFlow(List.of(new DefaultIssueLocation().on(file)), NewIssue.FlowType.DATA, "description")
      // Flow with execution type and no description
      .addFlow(List.of(new DefaultIssueLocation().on(file)), NewIssue.FlowType.EXECUTION, null)
      .forRule(JAVA_RULE_KEY);

    when(filters.accept(any(InputComponent.class), any(ScannerReport.Issue.class), anyString())).thenReturn(true);
    moduleIssues.initAndAddIssue(issue);

    ArgumentCaptor<ScannerReport.Issue> argument = ArgumentCaptor.forClass(ScannerReport.Issue.class);
    verify(reportPublisher.getWriter()).appendComponentIssue(eq(file.scannerId()), argument.capture());
    List<ScannerReport.Flow> writtenFlows = argument.getValue().getFlowList();

    assertThat(writtenFlows)
      .extracting(ScannerReport.Flow::getDescription, ScannerReport.Flow::getType)
      .containsExactly(tuple("", FlowType.UNDEFINED), tuple("description", FlowType.DATA), tuple("", FlowType.EXECUTION));

    assertThat(writtenFlows.get(0).getLocationCount()).isEqualTo(2);
    assertThat(writtenFlows.get(0).getLocationList()).containsExactly(
      ScannerReport.IssueLocation.newBuilder()
        .setComponentRef(file.scannerId())
        .setMsg("Foo1")
        .addMsgFormatting(ScannerReport.MessageFormatting.newBuilder().setStart(0).setEnd(4).setType(ScannerReport.MessageFormattingType.CODE).build())
        .setTextRange(ScannerReport.TextRange.newBuilder().setStartLine(1).setEndLine(1).setEndOffset(3).build())
        .build(),
      ScannerReport.IssueLocation.newBuilder()
        .setComponentRef(file.scannerId())
        .setMsg("Foo2")
        .setTextRange(ScannerReport.TextRange.newBuilder().setStartLine(2).setEndLine(2).setEndOffset(3).build())
        .build());
  }

  @Test
  void add_external_issue_to_cache() {
    initModuleIssues();

    DefaultExternalIssue issue = new DefaultExternalIssue(project)
      .at(new DefaultIssueLocation().on(file).at(file.selectLine(3)).message("Foo"))
      .type(RuleType.BUG)
      .forRule(JAVA_RULE_KEY)
      .severity(org.sonar.api.batch.rule.Severity.CRITICAL);

    moduleIssues.initAndAddExternalIssue(issue);

    ArgumentCaptor<ScannerReport.ExternalIssue> argument = ArgumentCaptor.forClass(ScannerReport.ExternalIssue.class);
    verify(reportPublisher.getWriter()).appendComponentExternalIssue(eq(file.scannerId()), argument.capture());
    assertThat(argument.getValue().getSeverity()).isEqualTo(org.sonar.scanner.protocol.Constants.Severity.CRITICAL);
  }

  @Test
  void initAndAddExternalIssue_whenImpactAndCleanCodeAttributeProvided_shouldPopulateReportFields() {
    initModuleIssues();

    DefaultExternalIssue issue = new DefaultExternalIssue(project)
      .at(new DefaultIssueLocation().on(file).at(file.selectLine(3)).message("Foo"))
      .cleanCodeAttribute(CleanCodeAttribute.CLEAR)
      .forRule(JAVA_RULE_KEY)
      .addImpact(MAINTAINABILITY, org.sonar.api.issue.impact.Severity.LOW);

    moduleIssues.initAndAddExternalIssue(issue);

    ArgumentCaptor<ScannerReport.ExternalIssue> argument = ArgumentCaptor.forClass(ScannerReport.ExternalIssue.class);
    verify(reportPublisher.getWriter()).appendComponentExternalIssue(eq(file.scannerId()), argument.capture());
    assertThat(argument.getValue().getImpactsList()).extracting(ScannerReport.Impact::getSoftwareQuality, ScannerReport.Impact::getSeverity)
      .containsExactly(tuple(ScannerReport.SoftwareQuality.MAINTAINABILITY, ScannerReport.ImpactSeverity.ImpactSeverity_LOW));
    assertThat(argument.getValue().getCleanCodeAttribute()).isEqualTo(CleanCodeAttribute.CLEAR.name());
  }

  @Test
  void dont_store_severity_if_no_severity_override_on_issue() {
    initModuleIssues();

    DefaultIssue issue = new DefaultIssue(project)
      .at(new DefaultIssueLocation().on(file).at(file.selectLine(3)).message("Foo"))
      .forRule(JAVA_RULE_KEY);
    when(filters.accept(any(InputComponent.class), any(ScannerReport.Issue.class), eq("INFO"))).thenReturn(true);
    moduleIssues.initAndAddIssue(issue);

    ArgumentCaptor<ScannerReport.Issue> argument = ArgumentCaptor.forClass(ScannerReport.Issue.class);
    verify(reportPublisher.getWriter()).appendComponentIssue(eq(file.scannerId()), argument.capture());
    assertThat(argument.getValue().hasOverriddenSeverity()).isFalse();
    assertThat(argument.getValue().getOverriddenImpactsList()).isEmpty();
  }

  @Test
  void filter_issue() {
    DefaultIssue issue = new DefaultIssue(project)
      .at(new DefaultIssueLocation().on(file).at(file.selectLine(3)).message(""))
      .forRule(JAVA_RULE_KEY);

    when(filters.accept(any(InputComponent.class), any(ScannerReport.Issue.class), anyString())).thenReturn(false);

    boolean added = moduleIssues.initAndAddIssue(issue);

    assertThat(added).isFalse();
    verifyNoInteractions(reportPublisher);
  }

  @Test
  void should_ignore_lines_commented_with_nosonar() {
    initModuleIssues();

    DefaultIssue issue = new DefaultIssue(project)
      .at(new DefaultIssueLocation().on(file).at(file.selectLine(3)).message(""))
      .forRule(JAVA_RULE_KEY);

    file.noSonarAt(new HashSet<>(Collections.singletonList(3)));

    boolean added = moduleIssues.initAndAddIssue(issue);

    assertThat(added).isFalse();
    verifyNoInteractions(reportPublisher);
  }

  @ParameterizedTest
  @ValueSource(strings = {"NoSonarCheck", "S1291", "S1291Check"})
  void should_accept_issues_on_no_sonar_rules(String noSonarRule) {
    RuleKey noSonarRuleKey = RuleKey.of("java", noSonarRule);
    // The "No Sonar" rule logs violations on the lines that are flagged with "NOSONAR" !!
    activeRulesBuilder.addRule(new NewActiveRule.Builder()
      .setRuleKey(noSonarRuleKey)
      .setSeverity(Severity.INFO)
      .setQProfileKey("qp-1")
      .build());
    initModuleIssues();

    file.noSonarAt(new HashSet<>(Collections.singletonList(3)));

    DefaultIssue issue = new DefaultIssue(project)
      .at(new DefaultIssueLocation().on(file).at(file.selectLine(3)).message(""))
      .forRule(noSonarRuleKey);

    when(filters.accept(any(InputComponent.class), any(ScannerReport.Issue.class), anyString())).thenReturn(true);

    boolean added = moduleIssues.initAndAddIssue(issue);

    assertThat(added).isTrue();
    verify(reportPublisher.getWriter()).appendComponentIssue(eq(file.scannerId()), any());
  }

  /**
   * Every rules and active rules has to be added in builders before creating IssuePublisher
   */
  private void initModuleIssues() {
    moduleIssues = new IssuePublisher(activeRulesBuilder.build(), filters, reportPublisher);
  }

}
