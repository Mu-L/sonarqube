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
package org.sonar.db.webhook;

import java.util.Optional;
import javax.annotation.Nullable;
import org.sonar.db.DbTester;
import org.sonar.db.project.ProjectDto;

import static org.sonar.db.webhook.WebhookTesting.newGlobalWebhook;
import static org.sonar.db.webhook.WebhookTesting.newWebhook;

public class WebhookDbTester {

  private final DbTester dbTester;

  public WebhookDbTester(DbTester dbTester) {
    this.dbTester = dbTester;
  }

  public WebhookDto insertGlobalWebhook() {
    return insert(newGlobalWebhook(), null, null);
  }

  public WebhookDto insertWebhook(ProjectDto project) {
    return insert(newWebhook(project), project.getKey(), project.getName());
  }

  public WebhookDto insert(WebhookDto dto, @Nullable String projectKey, @Nullable String projectName) {
    dbTester.getDbClient().webhookDao().insert(dbTester.getSession(), dto, projectKey, projectName);
    dbTester.commit();
    return dto;
  }

  public Optional<WebhookDto> selectWebhook(String uuid) {
    return dbTester.getDbClient().webhookDao().selectByUuid(dbTester.getSession(), uuid);
  }
}
