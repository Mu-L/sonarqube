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
package org.sonar.db.user;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class UserMembershipDto {

  private String uuid;
  private String groupUuid;
  private String login;
  private String name;

  public String getUuid() {
    return uuid;
  }

  public UserMembershipDto setUuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  public String getName() {
    return name;
  }

  public UserMembershipDto setName(String name) {
    this.name = name;
    return this;
  }

  @CheckForNull
  public String getLogin() {
    return login;
  }

  public UserMembershipDto setLogin(@Nullable String login) {
    this.login = login;
    return this;
  }

  @CheckForNull
  public String getGroupUuid() {
    return groupUuid;
  }

  public UserMembershipDto setGroupUuid(@Nullable String groupUuid) {
    this.groupUuid = groupUuid;
    return this;
  }
}
