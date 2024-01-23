/*
 * SonarQube
 * Copyright (C) 2009-2024 SonarSource SA
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
import styled from '@emotion/styled';
import {
  LightGreyCard,
  LightLabel,
  SnoozeCircleIcon,
  TextError,
  TextSubdued,
  TrendUpCircleIcon,
  themeColor,
} from 'design-system';
import React from 'react';
import { useIntl } from 'react-intl';
import { getTabPanelId } from '../../../components/controls/BoxedTabs';
import { getLeakValue } from '../../../components/measure/utils';
import { DEFAULT_ISSUES_QUERY } from '../../../components/shared/utils';
import { getBranchLikeQuery } from '../../../helpers/branch-like';
import { findMeasure, formatMeasure } from '../../../helpers/measures';
import {
  getComponentDrilldownUrl,
  getComponentIssuesUrl,
  getComponentSecurityHotspotsUrl,
} from '../../../helpers/urls';
import { Branch } from '../../../types/branch-like';
import { isApplication } from '../../../types/component';
import { IssueStatus } from '../../../types/issues';
import { MetricKey, MetricType } from '../../../types/metrics';
import { QualityGateStatus } from '../../../types/quality-gates';
import { Component, MeasureEnhanced } from '../../../types/types';
import { IssueMeasuresCardInner } from '../components/IssueMeasuresCardInner';
import MeasuresCardNumber from '../components/MeasuresCardNumber';
import MeasuresCardPercent from '../components/MeasuresCardPercent';
import {
  MeasurementType,
  MeasuresTabs,
  Status,
  getConditionRequiredLabel,
  getMeasurementMetricKey,
} from '../utils';

interface Props {
  branch?: Branch;
  component: Component;
  measures: MeasureEnhanced[];
  qgStatuses?: QualityGateStatus[];
}

export default function NewCodeMeasuresPanel(props: Readonly<Props>) {
  const { branch, component, measures, qgStatuses } = props;
  const intl = useIntl();
  const isApp = isApplication(component.qualifier);

  const failedConditions = qgStatuses?.flatMap((qg) => qg.failedConditions) ?? [];

  const newIssues = getLeakValue(findMeasure(measures, MetricKey.new_violations));
  const newIssuesCondition = failedConditions.find((c) => c.metric === MetricKey.new_violations);
  const issuesConditionFailed = newIssuesCondition?.level === Status.ERROR;
  const newAcceptedIssues = getLeakValue(findMeasure(measures, MetricKey.new_accepted_issues));
  const newSecurityHotspots = getLeakValue(
    findMeasure(measures, MetricKey.new_security_hotspots),
  ) as string;

  let issuesFooter;
  if (newIssuesCondition && !isApp) {
    issuesFooter = issuesConditionFailed ? (
      <TextError
        className="sw-font-regular sw-body-xs sw-inline"
        text={getConditionRequiredLabel(newIssuesCondition, intl, true)}
      />
    ) : (
      <LightLabel className="sw-body-xs">
        {getConditionRequiredLabel(newIssuesCondition, intl)}
      </LightLabel>
    );
  }

  return (
    <div className="sw-mt-6" id={getTabPanelId(MeasuresTabs.New)}>
      <LightGreyCard className="sw-flex sw-rounded-2 sw-gap-4">
        <IssueMeasuresCardInner
          data-test="overview__measures-new_issues"
          linkDisabled={component.needIssueSync}
          className="sw-w-1/2"
          metric={MetricKey.new_violations}
          value={formatMeasure(newIssues, MetricType.ShortInteger)}
          header={intl.formatMessage({
            id: 'overview.new_issues',
          })}
          url={getComponentIssuesUrl(component.key, {
            ...getBranchLikeQuery(branch),
            ...DEFAULT_ISSUES_QUERY,
            isNewCodePeriod: 'true',
          })}
          failed={issuesConditionFailed}
          icon={issuesConditionFailed && <TrendUpCircleIcon />}
          footer={issuesFooter}
        />
        <StyledCardSeparator />
        <IssueMeasuresCardInner
          data-test="overview__measures-accepted_issues"
          linkDisabled={component.needIssueSync}
          className="sw-w-1/2"
          metric={MetricKey.new_accepted_issues}
          value={formatMeasure(newAcceptedIssues, MetricType.ShortInteger)}
          header={intl.formatMessage({
            id: 'overview.accepted_issues',
          })}
          url={getComponentIssuesUrl(component.key, {
            ...getBranchLikeQuery(branch),
            issueStatuses: IssueStatus.Accepted,
            isNewCodePeriod: 'true',
          })}
          footer={
            <TextSubdued className="sw-body-xs">
              {intl.formatMessage({ id: 'overview.accepted_issues.help' })}
            </TextSubdued>
          }
          icon={
            <SnoozeCircleIcon
              color={
                newAcceptedIssues === '0' ? 'overviewCardDefaultIcon' : 'overviewCardWarningIcon'
              }
            />
          }
        />
      </LightGreyCard>
      <div className="sw-grid sw-grid-cols-2 sw-gap-4 sw-mt-4">
        <MeasuresCardPercent
          branchLike={branch}
          componentKey={component.key}
          conditions={failedConditions}
          measures={measures}
          measurementType={MeasurementType.Coverage}
          label="overview.quality_gate.coverage"
          url={getComponentDrilldownUrl({
            componentKey: component.key,
            metric: getMeasurementMetricKey(MeasurementType.Coverage, true),
            branchLike: branch,
            listView: true,
          })}
          conditionMetric={MetricKey.new_coverage}
          linesMetric={MetricKey.new_lines_to_cover}
          useDiffMetric
          showRequired={!isApp}
        />

        <MeasuresCardPercent
          branchLike={branch}
          componentKey={component.key}
          conditions={failedConditions}
          measures={measures}
          measurementType={MeasurementType.Duplication}
          label="overview.quality_gate.duplications"
          url={getComponentDrilldownUrl({
            componentKey: component.key,
            metric: getMeasurementMetricKey(MeasurementType.Coverage, true),
            branchLike: branch,
            listView: true,
          })}
          conditionMetric={MetricKey.new_duplicated_lines_density}
          linesMetric={MetricKey.new_lines}
          useDiffMetric
          showRequired={!isApp}
        />

        <MeasuresCardNumber
          label={
            newSecurityHotspots === '1'
              ? 'issue.type.SECURITY_HOTSPOT'
              : 'issue.type.SECURITY_HOTSPOT.plural'
          }
          url={getComponentSecurityHotspotsUrl(component.key, {
            ...getBranchLikeQuery(branch),
          })}
          value={newSecurityHotspots}
          conditions={failedConditions}
          conditionMetric={MetricKey.new_security_hotspots_reviewed}
          showRequired={!isApp}
        />
      </div>
    </div>
  );
}

const StyledCardSeparator = styled.div`
  width: 1px;
  background-color: ${themeColor('projectCardBorder')};
`;