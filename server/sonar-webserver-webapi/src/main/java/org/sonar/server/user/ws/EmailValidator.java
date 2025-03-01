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
package org.sonar.server.user.ws;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import javax.annotation.Nullable;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class EmailValidator {

  private EmailValidator() {
    // Hide constructor
  }

  static boolean isValidIfPresent(@Nullable String email) {
    return isEmpty(email) || isValidEmail(email);
  }

  public static boolean isValidEmail(String email) {
    try {
      InternetAddress emailAddr = new InternetAddress(email);
      emailAddr.validate();
      return true;
    } catch (AddressException ex) {
      return false;
    }
  }

}
