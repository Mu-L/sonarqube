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
package org.sonar.server.v2.api.projectbindings.request;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.annotation.Nullable;

public record ProjectBindingsSearchRestRequest(

  @Nullable @Schema(
    description = """
      Filter on the repository name.
      This parameter performs an exact, case insensitive, match.
      """) String repository,

  @Nullable @Schema(description = "Filter on the DevOps Platform setting id.") String dopSettingId,

  @Nullable @Schema(
    description = """
      Filter on the repository URL.
      This parameter can be in different formats, the traditional URL or the git remote URL (https or ssh).
      """) String repositoryUrl

) {

}
