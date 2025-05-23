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
package org.sonar.core.util.issue;

import java.util.Arrays;
import org.junit.Test;
import org.sonar.api.issue.impact.Severity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sonar.api.issue.impact.SoftwareQuality.MAINTAINABILITY;

public class IssueChangedEventTest {
  private static final String BRANCH_NAME = "branch-name";
  private static final String ISSUE_KEY = "issue-key";
  private static final String PROJECT_KEY = "project-key";

  @Test
  public void issueChangedEvent_instantiation_accepts_nulls() {
    Issue issue = new Issue(ISSUE_KEY, BRANCH_NAME);
    issue.addImpact(MAINTAINABILITY, Severity.HIGH);

    IssueChangedEvent event = new IssueChangedEvent(PROJECT_KEY, new Issue[] {issue}, null, null, null);

    assertThat(event.getEvent()).isEqualTo("IssueChanged");
    assertThat(event.getProjectKey()).isEqualTo(PROJECT_KEY);
    assertThat(event.getResolved()).isNull();
    assertThat(event.getUserSeverity()).isNull();
    assertThat(event.getUserType()).isNull();
    assertThat(event.getIssues()).hasSize(1);
    assertThat(Arrays.stream(event.getIssues()).iterator().next().getImpacts())
      .hasSize(1)
      .containsExactlyInAnyOrder(new Issue.Impact(MAINTAINABILITY, Severity.HIGH));
  }

  @Test
  public void issueChangedEvent_instantiation_accepts_actual_values() {
    Issue[] issues = new Issue[] {new Issue(ISSUE_KEY, BRANCH_NAME)};
    IssueChangedEvent event = new IssueChangedEvent(PROJECT_KEY, issues, true, "BLOCKER", "BUG");

    assertThat(event.getEvent()).isEqualTo("IssueChanged");
    assertThat(event.getProjectKey()).isEqualTo(PROJECT_KEY);
    assertThat(event.getResolved()).isTrue();
    assertThat(event.getUserSeverity()).isEqualTo("BLOCKER");
    assertThat(event.getUserType()).isEqualTo("BUG");
    assertThat(event.getIssues()).hasSize(1);
  }

  @Test
  public void issueChangedEvent_instantiation_doesNotAccept_emptyIssues() {
    Issue[] issues = new Issue[0];

    assertThatThrownBy(() -> new IssueChangedEvent(PROJECT_KEY, issues, true, "BLOCKER", "BUG"))
      .isInstanceOf(IllegalArgumentException.class)
      .withFailMessage("Can't create IssueChangedEvent without any issues that have changed");
  }

}
