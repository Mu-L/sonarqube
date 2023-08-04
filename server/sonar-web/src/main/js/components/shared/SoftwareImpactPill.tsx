/*
 * SonarQube
 * Copyright (C) 2009-2023 SonarSource SA
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
import classNames from 'classnames';
import { Pill } from 'design-system';
import React from 'react';
import { FormattedMessage } from 'react-intl';
import { translate } from '../../helpers/l10n';
import { SoftwareImpactSeverity, SoftwareQuality } from '../../types/issues';
import DocumentationTooltip from '../common/DocumentationTooltip';
import SoftwareImpactSeverityIcon from '../icons/SoftwareImpactSeverityIcon';

export interface Props {
  className?: string;
  severity: SoftwareImpactSeverity;
  quality: SoftwareQuality;
}

export default function SoftwareImpactPill(props: Props) {
  const { className, severity, quality } = props;

  const variant = {
    [SoftwareImpactSeverity.High]: 'danger',
    [SoftwareImpactSeverity.Medium]: 'warning',
    [SoftwareImpactSeverity.Low]: 'info',
  }[severity] as 'danger' | 'warning' | 'info';

  return (
    <DocumentationTooltip
      content={
        <FormattedMessage
          id="issue.impact.severity.tooltip"
          defaultMessage={translate('issue.impact.severity.tooltip')}
          values={{
            severity: translate('severity', severity).toLowerCase(),
            quality: translate('issue.software_quality', quality).toLowerCase(),
          }}
        />
      }
      links={[
        {
          href: '/user-guide/clean-code',
          label: translate('learn_more'),
        },
      ]}
    >
      <Pill className={classNames('sw-flex sw-gap-1 sw-items-center', className)} variant={variant}>
        {translate('issue.software_quality', quality)}
        <SoftwareImpactSeverityIcon severity={severity} data-guiding-id="issue-3" />
      </Pill>
    </DocumentationTooltip>
  );
}
