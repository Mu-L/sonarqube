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
package org.sonarqube.ws.client.permissions;

import jakarta.annotation.Generated;

/**
 * This is part of the internal API.
 * This is a POST request.
 * @see <a href="https://next.sonarqube.com/sonarqube/web_api/api/permissions/add_project_creator_to_template">Further information about this action online (including a response example)</a>
 * @since 6.0
 */
@Generated("sonar-ws-generator")
public class AddProjectCreatorToTemplateRequest {

  private String permission;
  private String templateId;
  private String templateName;

  /**
   * This is a mandatory parameter.
   * Possible values:
   * <ul>
   *   <li>"admin"</li>
   *   <li>"codeviewer"</li>
   *   <li>"issueadmin"</li>
   *   <li>"scan"</li>
   *   <li>"user"</li>
   * </ul>
   */
  public AddProjectCreatorToTemplateRequest setPermission(String permission) {
    this.permission = permission;
    return this;
  }

  public String getPermission() {
    return permission;
  }

  /**
   * Example value: "AU-Tpxb--iU5OvuD2FLy"
   */
  public AddProjectCreatorToTemplateRequest setTemplateId(String templateId) {
    this.templateId = templateId;
    return this;
  }

  public String getTemplateId() {
    return templateId;
  }

  /**
   * Example value: "Default Permission Template for Projects"
   */
  public AddProjectCreatorToTemplateRequest setTemplateName(String templateName) {
    this.templateName = templateName;
    return this;
  }

  public String getTemplateName() {
    return templateName;
  }
}
