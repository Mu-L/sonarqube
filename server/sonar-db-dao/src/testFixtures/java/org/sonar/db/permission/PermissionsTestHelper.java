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
package org.sonar.db.permission;

import java.util.Set;

import static org.sonar.db.permission.GlobalPermission.APPLICATION_CREATOR;
import static org.sonar.db.permission.GlobalPermission.PORTFOLIO_CREATOR;
import static org.sonar.db.permission.GlobalPermission.SCAN;

public class PermissionsTestHelper {

  public static final Set<String> ALL_PERMISSIONS = Set.of(ProjectPermission.ADMIN.getKey(), ProjectPermission.CODEVIEWER.getKey(),
    ProjectPermission.ISSUE_ADMIN.getKey(), ProjectPermission.SECURITYHOTSPOT_ADMIN.getKey(),
    SCAN.getKey(), ProjectPermission.USER.getKey(), APPLICATION_CREATOR.getKey(), PORTFOLIO_CREATOR.getKey());

  private PermissionsTestHelper() {
  }
}
