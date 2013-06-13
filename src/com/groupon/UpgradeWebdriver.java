/**
 * Copyright (c) 2013, Groupon, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of GROUPON nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * Created with IntelliJ IDEA.
 * User: Dima Kovalenko (@dimacus) && Darko Marinov
 * Date: 5/10/13
 * Time: 4:06 PM
 */

package com.groupon;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UpgradeWebdriver extends ExecuteOSTask {

  @Override
  public String getEndpoint() {
    return "/upgrade_webdriver";
  }

  @Override
  public String getDescription() {
    return "Downloads a version of WebDriver jar to node, and upgrades the setting to use new version on restart";
  }

  @Override
  public String execute() {
    return JsonWrapper.upgradeWebdriverToJson(1, "", "", "version parameter is required");
  }

  @Override
  public String execute(String version) {

    DownloadWebdriver downloader = new DownloadWebdriver();
    Map<String, String> result = JsonWrapper.parseJson(downloader.execute(version));
    String oldVersion = RuntimeConfig.getWebdriverVersion();

    if (result.get("standard_error").isEmpty()) {
      RuntimeConfig.setWebdriverVersion(version);
      try {
        RuntimeConfig.saveConfigToFile();
        return JsonWrapper.upgradeWebdriverToJson(0, oldVersion, version, "");
      } catch (IOException error) {
        return JsonWrapper.upgradeWebdriverToJson(1, oldVersion, "", error.toString());
      }
    } else {
      return JsonWrapper
          .upgradeWebdriverToJson(1, oldVersion, "", result.get("standard_error").toString());
    }
  }

  @Override
  public String execute(Map<String, String> parameter) {

    if (parameter.isEmpty() || !parameter.containsKey("version")) {
      return execute();
    } else {
      return execute(parameter.get("version").toString());
    }
  }

  @Override
  public List<String> getDependencies() {
    List<String> dependencies = new LinkedList();
    dependencies.add("com.groupon.DownloadWebdriver");
    return dependencies;
  }

  @Override
  public Map getResponseDescription() {
    Map response = new HashMap();
    response.put("exit_code", "Record if upgrade was successful or not");
    response.put("old_version", "");
    response.put("new_version", "");
    response.put("standard_error", "Any error returned from.");
    return response;
  }

  @Override
  public Map getAcceptedParams() {
    Map<String, String> params = new HashMap();
    params.put("version", "(Required) - Version of WebDriver to download, such as 2.33.0");
    return params;
  }

}
